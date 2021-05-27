package kr.ac.gachon.sw.gbro.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.service.LocalNotiService;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.LoadingDialog;
import kr.ac.gachon.sw.gbro.util.Preferences;
import kr.ac.gachon.sw.gbro.util.model.User;


public class SettingPreferenceFragment extends PreferenceFragmentCompat {
    private KeywordDialog keywordDialog;
    private Preferences prefs;
    private User user;
    private ArrayList<String> keywordList;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference);

        prefs = new Preferences(getContext());

        setNotiPref();

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

    private void setNotiPref() {
        if(prefs.getStringArrayList("keywordList", null) != null) {
            keywordDialog = new KeywordDialog(getActivity(), prefs.getStringArrayList("keywordList", null));
        }
        else keywordDialog = new KeywordDialog(getActivity());

        SwitchPreference keyWordOnOffPref = findPreference("keyWordOnOff");
        keyWordOnOffPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean) newValue) {
                    getActivity().startService(new Intent(getActivity(), LocalNotiService.class));
                }
                else {
                    if(!prefs.getBoolean("nearbyOnOff", false)) getActivity().stopService(new Intent(getActivity(), LocalNotiService.class));
                }
                return true;
            }
        });

        SwitchPreference nearbyOnOffPref = findPreference("nearbyOnOff");
        nearbyOnOffPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean) newValue) {
                    getActivity().startService(new Intent(getActivity(), LocalNotiService.class));
                }
                else {
                    if(!prefs.getBoolean("keyWordOnOff", false)) getActivity().stopService(new Intent(getActivity(), LocalNotiService.class));
                }
                return true;
            }
        });

        Preference notiKeyWordPref = findPreference("notiKeyWord");
        notiKeyWordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                keywordDialog.show();
                return true;
            }
        });

        keywordDialog.getSaveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> newKeywordList = keywordDialog.getKeywordList();
                keywordDialog.dismiss();

                if(newKeywordList.size() > 0) {
                    prefs.setStringArrayList("keywordList", newKeywordList);
                }
                else {
                    prefs.setStringArrayList("keywordList", null);
                }
            }
        });
    }
}