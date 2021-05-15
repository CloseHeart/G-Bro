package kr.ac.gachon.sw.gbro.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.dhaval2404.imagepicker.ImagePicker;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivitySettingBinding;


public class SettingActivity extends BaseActivity<ActivitySettingBinding> implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private ActionBar actionBar;

    @Override
    protected ActivitySettingBinding getBinding() {
        return ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.setting);
        }

        getSupportFragmentManager().beginTransaction().replace(binding.flProfile.getId(), new ProfileFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(binding.flSetting.getId(), new SettingPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Google 정책에 따라 MenuItem에 Switch 사용하지 않고 if문 사용
        int itemId = item.getItemId();

        // Actionbar 뒤로가기 버튼
        if(itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(binding.flSetting.getId(), fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ImagePicker.REQUEST_CODE){

        }
    }
}
