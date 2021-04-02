package kr.ac.gachon.sw.gbro;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
        setSlidingPanel();
    }

    private void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        BoardFragment boardFragment = new BoardFragment();
        fragmentManager.beginTransaction().add(binding.flBoard.getId(), boardFragment).commit();

        MapFragment mapFragment = new MapFragment();
        fragmentManager.beginTransaction().add(binding.flMap.getId(), mapFragment).commit();
    }

    private void setSlidingPanel() {
        SlidingUpPanelLayout slidingUpPanelLayout = binding.mainpanel;

        // TODO : 다크모드 / 일반모드 변경시에 Panel 상태와 상관 없이 ActionBar가 사라지는 문제 해결
        if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) getSupportActionBar().hide();

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                // If Close
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    getSupportActionBar().hide();
                }
                // If Open
                else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    getSupportActionBar().show();
                }
            }
        });
    }
}