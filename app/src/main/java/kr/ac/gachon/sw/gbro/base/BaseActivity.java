package kr.ac.gachon.sw.gbro.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

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
}
