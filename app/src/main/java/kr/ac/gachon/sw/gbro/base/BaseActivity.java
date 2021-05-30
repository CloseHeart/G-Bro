package kr.ac.gachon.sw.gbro.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import kr.ac.gachon.sw.gbro.LoginActivity;
import kr.ac.gachon.sw.gbro.SplashActivity;
import kr.ac.gachon.sw.gbro.util.Auth;

public abstract class BaseActivity<B extends ViewBinding> extends AppCompatActivity {
    protected B binding;
    protected abstract B getBinding();

    protected void initBinding() {
        binding = getBinding();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("BaseActivity", "onCreate");
        this.initBinding();
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        Log.d("BaseActivity", "onStart @ " + this.getClass().getSimpleName());
        super.onStart();

        // 로그인이 된 유저가 아니라면
        if((!(this instanceof SplashActivity) && !(this instanceof LoginActivity)) && Auth.getCurrentUser() == null) {
            // 로그인으로
            Auth.moveToLogin(this);
            finish();
        }
    }
}
