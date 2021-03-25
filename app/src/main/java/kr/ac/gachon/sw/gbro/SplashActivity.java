package kr.ac.gachon.sw.gbro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivitySplashBinding;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    @Override
    protected ActivitySplashBinding getBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MainActivity로 이동을 위한 Intent 생성
        Intent mainActivityIntent = new Intent(this, MainActivity.class);

        // 지정한 시간 이후에 MainActivity로 이동시키기 위해 Handler 생성
        Handler splashHandler = new Handler();

        // 일정 시간 후 Runnable 안에 있는 명령을 실행
        splashHandler.postDelayed(() -> {
            // 새 액티비티 시작
            startActivity(mainActivityIntent);

            // 현재 액티비티 종료
            finish();
        }, 2000);
    }

    @Override
    public void onBackPressed() { }
}
