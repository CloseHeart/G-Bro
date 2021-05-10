package kr.ac.gachon.sw.gbro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.board.WriteActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityMainBinding;
import kr.ac.gachon.sw.gbro.map.MapFragment;
import kr.ac.gachon.sw.gbro.setting.SettingActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding>{
    long lastPressedTime = 0;
    long backPressedTime = 2000;

    @Override
    protected ActivityMainBinding getBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragment();
        setSlidingPanel();
        setFab();

    }

    @Override
    public void onBackPressed() {
        // 열려있다면
        if(binding.mainpanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            // 닫기
            binding.mainpanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            binding.viewSwipeBar.setVisibility(View.VISIBLE);
        }

        // 그게 아니라면
        else {
            // 두 번 눌러서 종료할 수 있도록 함
            if (System.currentTimeMillis() > lastPressedTime + backPressedTime) {
                lastPressedTime = System.currentTimeMillis();
                Toast.makeText(this, getString(R.string.backpressed), Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(binding.mainpanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            binding.mainpanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            binding.viewSwipeBar.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 각 Fragment들을 설정한다
     * @author Minjae Seon
     * @return Void
     */
    private void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        BoardFragment boardFragment = new BoardFragment();
        fragmentManager.beginTransaction().replace(binding.flBoard.getId(), boardFragment).commit();

        MapFragment mapFragment = MapFragment.getMainInstance();
        fragmentManager.beginTransaction().replace(binding.flMap.getId(), mapFragment).commit();
    }

    /*
     * SlidingPanel 관련 이벤트를 설정한다
     * @author Minjae Seon
     * @return Void
     */
    private void setSlidingPanel() {
        SlidingUpPanelLayout slidingUpPanelLayout = binding.mainpanel;

        CustomActionBar customActionBar = new CustomActionBar(this, getSupportActionBar());
        customActionBar.setActionBar();
        customActionBar.hide();

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(MainActivity.this.getLocalClassName(), "slideOffset : " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                // If Close
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    // 액션바 가리기
                    customActionBar.hide();

                    // Swiper 보이기
                    binding.viewSwipeBar.setVisibility(View.VISIBLE);
                }
                // If Open
                else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    // 액션바 보이기
                    customActionBar.show();

                    // Swiper 없애기
                    binding.viewSwipeBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /*
     * FloatingActionButton을 설정한다
     * @author Minjae Seon
     * @return Voidㅔ
     */
    private void setFab() {
        // 글쓰기 버튼
        binding.fabWrite.setOnClickListener(v -> {
            Intent writeIntent = new Intent(this, WriteActivity.class);
            startActivity(writeIntent);
        });

        // 설정 버튼
        binding.fabSetting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }
}