package kr.ac.gachon.sw.gbro;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.databinding.ActivityMainBinding;
import kr.ac.gachon.sw.gbro.map.MapFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
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
        setSpinner();
        setSlidingPanel();
        setFab();
    }

    @Override
    public void onBackPressed() {
        // 열려있다면
        if(binding.mainpanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            // 닫기
            binding.mainpanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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

        MapFragment mapFragment = new MapFragment();
        fragmentManager.beginTransaction().replace(binding.flMap.getId(), mapFragment).commit();
    }

    /*
     * SlidingPanel 관련 이벤트를 설정한다
     * @author Minjae Seon
     * @return Void
     */
    private void setSlidingPanel() {
        SlidingUpPanelLayout slidingUpPanelLayout = binding.mainpanel;

        Toolbar toolbar = binding.toolBar;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); // 커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.hide();

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(MainActivity.this.getLocalClassName(), "slideOffset : " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                // If Close
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    if(actionBar != null)  actionBar.hide();
                }
                // If Open
                else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    if(actionBar != null)  actionBar.show();
                }
            }
        });
    }

    /*
     * FloatingActionButton을 설정한다
     * @author Minjae Seon
     * @return Void
     */
    private void setFab() {
        // 글쓰기 버튼
        binding.fabWrite.setOnClickListener(v -> {
            Toast.makeText(this, "글쓰기 버튼입니다", Toast.LENGTH_SHORT).show();
        });

        // 설정 버튼
        binding.fabSetting.setOnClickListener(v -> {

            // 여기 postcontent setting으로 바꾸기(원상복구 )
            Intent intent = new Intent(MainActivity.this, PostContentActivity.class);
            startActivity(intent);
            Toast.makeText(this, "설정 버튼입니다", Toast.LENGTH_SHORT).show();
        });
    }

    /*
     * Spinner 리스너 처리
     * @author Taehyun Park
     * @return Void
     */
    private void setSpinner(){
        // 특정 아이템이 선택된다면 발생하는 이벤트
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position)+" 게시물 목록입니다.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

}