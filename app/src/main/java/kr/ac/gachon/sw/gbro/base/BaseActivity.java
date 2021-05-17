package kr.ac.gachon.sw.gbro.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import kr.ac.gachon.sw.gbro.util.Auth;

public abstract class BaseActivity<B extends ViewBinding> extends AppCompatActivity {
    protected B binding;
    protected abstract B getBinding();

    protected void initBinding() {
        binding = getBinding();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initBinding();
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 로그인이 된 유저가 아니라면
        if(Auth.getCurrentUser() == null) {
            // 로그인으로
            Auth.moveToLogin(this);
        }
    }
}
