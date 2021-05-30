package kr.ac.gachon.sw.gbro.setting;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private SwitchPreference nearbyOnOffPref;

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

        nearbyOnOffPref = findPreference("nearbyOnOff");
        nearbyOnOffPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean) newValue) {
                    // 권한 체크
                    // Q 이상이면
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // 백그라운드 안되어있으면
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // 이전에 거부한 적 있는 경우
                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                                Toast.makeText(getContext(), R.string.setting_nearby_permission_denied, Toast.LENGTH_SHORT).show();
                                nearbyOnOffPref.setChecked(false);
                                prefs.setBoolean("nearbyOnOff", false);
                            }
                            else {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
                            }
                        }
                        // 이외엔 서비스 실행
                        else {
                            getActivity().startService(new Intent(getActivity(), LocalNotiService.class));
                        }
                    }
                    // Q 이하면
                    else {
                        // FINE_LOCATION 권한 없으면
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // 이전에 거부한 적 있는 경우
                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                Toast.makeText(getContext(), R.string.setting_nearby_permission_denied, Toast.LENGTH_SHORT).show();
                                nearbyOnOffPref.setChecked(false);
                                prefs.setBoolean("nearbyOnOff", false);
                            }
                            else {
                                // 요청
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            }
                        }
                        // 이외엔 서비스 실행
                        else {
                            getActivity().startService(new Intent(getActivity(), LocalNotiService.class));
                        }
                    }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode) {
           case 100:
           case 101:
               if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   getActivity().startService(new Intent(getActivity(), LocalNotiService.class));
               }
               else {
                   nearbyOnOffPref.setChecked(false);
                   prefs.setBoolean("nearbyOnOff", false);
                   Toast.makeText(getContext(), R.string.setting_nearby_permission_denied, Toast.LENGTH_SHORT).show();
               }
               break;
           default:
               break;
       }
    }
}