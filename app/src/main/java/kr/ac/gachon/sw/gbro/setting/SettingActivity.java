package kr.ac.gachon.sw.gbro.setting;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityChattingBinding;
import kr.ac.gachon.sw.gbro.databinding.ActivitySettingBinding;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    @Override
    protected ActivitySettingBinding getBinding() {
        return ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
