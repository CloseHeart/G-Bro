package kr.ac.gachon.sw.gbro.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import kr.ac.gachon.sw.gbro.LoginActivity;
import kr.ac.gachon.sw.gbro.MainActivity;
import kr.ac.gachon.sw.gbro.board.WriteActivity;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.User;

import static android.app.Activity.RESULT_OK;
import com.github.dhaval2404.imagepicker.ImagePicker;


public class SettingPreferenceFragment extends PreferenceFragmentCompat {
    SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        EditTextPreference notiKeyWordPref = findPreference("notiKeyWord");
        notiKeyWordPref.setSummary(prefs.getString("notiKeyWord", ""));
        notiKeyWordPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue instanceof String) {
                    notiKeyWordPref.setSummary((String) newValue);
                }
                else {
                    notiKeyWordPref.setSummary("");
                }
                return true;
            }
        });


        Preference myPostPref = findPreference("check_my_board");
        myPostPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent myPostIntent = new Intent(getActivity(), MyPostActivity.class);
                startActivity(myPostIntent);
                return true;
            }
        });

        // 로그아웃 이벤트 리스너
        Preference myPref_logout = (Preference) findPreference("log_out");
        myPref_logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.setting_logout);
                builder.setMessage(R.string.setting_logout_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Auth.signOut(getActivity());
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.create().show();
                return true;
            }
        });

        // 회원탈퇴 이벤트 리스너
        Preference myPref_withdraw_member = (Preference) findPreference("withdraw_member");
        myPref_withdraw_member.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.setting_withdraw);
                builder.setMessage(R.string.setting_withdraw_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Auth.deleteAccount(getActivity());
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.create().show();
                return true;
            }
        });

    }
}