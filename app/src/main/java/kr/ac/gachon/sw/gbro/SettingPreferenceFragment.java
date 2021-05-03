package kr.ac.gachon.sw.gbro;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class  SettingPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;

    ListPreference productPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);
        productPreference = (ListPreference)findPreference("found_list");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!prefs.getString("found_list","").equals("")){
            productPreference.setSummary(prefs.getString("found_list","습득물 선택"));
        }

        // 값 변경 시 바뀜 감지
        prefs.registerOnSharedPreferenceChangeListener(prefLinstener);

        // 로그아웃 이벤트 리스너
        Preference myPref_logout = (Preference)findPreference("log_out");
        myPref_logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("로그아웃");
                builder.setMessage("정말로 로그아웃하시겠습니까?");
                builder.setPositiveButton("예",null);
                builder.setNegativeButton("아니오", null);
                builder.setNeutralButton("취소",null);
                builder.create().show();
                return false;
            }
        });

        // 회원탈퇴 이벤트 리스너
        Preference myPref_withdraw_member = (Preference)findPreference("withdraw_member");
        myPref_withdraw_member.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("회원탈퇴");
                builder.setMessage("정말로 회원을 탈퇴하시겠습니까?");
                builder.setPositiveButton("예",null);
                builder.setNegativeButton("아니오", null);
                builder.setNeutralButton("취소",null);
                builder.create().show();
                return false;
            }
        });

    }

    // 값 변경 시 바뀜 감지 리스너 지정
    SharedPreferences.OnSharedPreferenceChangeListener prefLinstener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("found_list")){
                productPreference.setSummary(prefs.getString("found_list","습득물 선택"));
            }
        }
    };


}