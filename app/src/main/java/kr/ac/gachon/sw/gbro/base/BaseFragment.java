package kr.ac.gachon.sw.gbro.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<B extends ViewBinding> extends Fragment {
    B binding;
    protected abstract B getBinding();

    protected void initBinding() {
        binding = getBinding();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initBinding();
    }
}
