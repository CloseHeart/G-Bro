package kr.ac.gachon.sw.gbro.board;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.ActivityBoardBinding;

public class BoardFragment extends BaseFragment<ActivityBoardBinding> {
    @Override
    protected ActivityBoardBinding getBinding() {
        return ActivityBoardBinding.inflate(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getBinding().getRoot();
    }
}
