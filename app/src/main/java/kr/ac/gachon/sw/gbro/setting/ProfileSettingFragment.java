package kr.ac.gachon.sw.gbro.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.model.User;


public class ProfileSettingFragment extends PreferenceFragmentCompat {
    SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_change_info);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // 프로필 정보 수정 (사진)
        Preference changeInfoImagePref = findPreference("change_info_image");
        changeInfoImagePref.setSummary(prefs.getString("change_info_image", ""));
        changeInfoImagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ImagePicker.Companion.with(ProfileSettingFragment.this)
                        .crop()
                        .galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"})
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
                return true;
            }
        });

        // 프로필 정보 수정 (닉네임)
        EditTextPreference changeInfoNickPref = findPreference("change_info_nickname");
        changeInfoNickPref.setSummary(prefs.getString("change_info_nickname", ""));
        changeInfoNickPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue instanceof String) {
                    changeInfoNickPref.setSummary("현재 닉네임 : "+ (String) newValue);
                    Firestore.updateProfileNickName(Auth.getCurrentUser().getUid(), (String) newValue).addOnCompleteListener(documentTask -> {
                        // 성공했다면
                        if(documentTask.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.change_nickname_success, Toast.LENGTH_LONG).show();
                            // 추후에 새로고침 기능 넣을 것
                        }
                        // 실패했다면
                        else {
                            // 에러 토스트
                            Toast.makeText(getContext(), R.string.change_nickname_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    changeInfoNickPref.setSummary("");
                }
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            Bitmap fileBitmap = BitmapFactory.decodeFile(ImagePicker.Companion.getFilePath(data));
            CloudStorage.uploadProfileImg(Auth.getCurrentUser().getUid(),fileBitmap).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task1) {
                    if(task1.isSuccessful()) {
                        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                User user = task2.getResult().toObject(User.class);
                                if(user != null) {
                                    CloudStorage.profileRef.child(Auth.getCurrentUser().getUid() + "/profile.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task3) {
                                            if(task3.isSuccessful()){
                                                Firestore.updateProfileImage(Auth.getCurrentUser().getUid(),task3.getResult().toString()).addOnCompleteListener(documentTask ->{
                                                    // 성공했다면
                                                    if(documentTask.isSuccessful()) {
                                                        Toast.makeText(getContext(), R.string.change_image_success, Toast.LENGTH_LONG).show();
                                                    }
                                                    // 실패했다면
                                                    else {
                                                        // 에러 토스트
                                                        Toast.makeText(getContext(), R.string.change_image_error, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }else{
                                                // 에러 토스트
                                                Toast.makeText(getContext(), R.string.change_image_error, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else {
                                    Log.d(this.getClass().getSimpleName(), "Profile Image NULL");
                                }
                            }
                        });
                    }
                    else {
                        // fail
                    }
                }
            });
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

}