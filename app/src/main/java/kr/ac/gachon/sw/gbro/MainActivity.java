package kr.ac.gachon.sw.gbro;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.databinding.ActivityMainBinding;
import kr.ac.gachon.sw.gbro.map.MapFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @Override
    protected ActivityMainBinding getBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragment();
    }

    private void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        BoardFragment boardFragment = new BoardFragment();
        fragmentManager.beginTransaction().add(binding.flBoard.getId(), boardFragment).commit();

        MapFragment mapFragment = new MapFragment();
        fragmentManager.beginTransaction().add(binding.flMap.getId(), mapFragment).commit();
    }
}