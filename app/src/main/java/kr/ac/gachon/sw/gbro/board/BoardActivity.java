package kr.ac.gachon.sw.gbro.board;

import android.os.Bundle;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityBoardBinding;

public class BoardActivity extends BaseActivity<ActivityBoardBinding> {
    @Override
    protected ActivityBoardBinding getBinding() {
        return ActivityBoardBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
