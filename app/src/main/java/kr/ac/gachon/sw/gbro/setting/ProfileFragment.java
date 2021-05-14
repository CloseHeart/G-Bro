package kr.ac.gachon.sw.gbro.setting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.FragmentProfileBinding;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.model.User;

public class ProfileFragment extends BaseFragment<FragmentProfileBinding> {
    @Override
    protected FragmentProfileBinding getBinding() {
        return FragmentProfileBinding.inflate(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setProfileData();

        return binding.getRoot();
    }

    /**
     * 프로필 정보를 가져온다
     * @author Minjae Seon
     */
    private void setProfileData() {
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);

                if(user != null) {
                    binding.tvName.setText(user.getUserNickName());
                    binding.tvEmail.setText(Auth.getCurrentUser().getEmail());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    binding.tvRegisterdate.setText(dateFormat.format(user.getRegisterTime().toDate()));

                    if(user.getUserProfileImgURL() != null) {
                        CloudStorage.getImageFromURL(user.getUserProfileImgURL()).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> task) {
                                if(task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                    binding.ivProfile.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                    else {
                        Log.d(ProfileFragment.this.getClass().getSimpleName(), "Profile Image NULL");
                    }
                }
                else {
                    Auth.signOut(getActivity());
                }
            }
        });
    }
}
