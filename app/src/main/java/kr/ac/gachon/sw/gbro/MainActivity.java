package kr.ac.gachon.sw.gbro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.board.WriteActivity;
import kr.ac.gachon.sw.gbro.chat.ChatListActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityMainBinding;
import kr.ac.gachon.sw.gbro.databinding.CustomactionbarBinding;
import kr.ac.gachon.sw.gbro.map.MapFragment;
import kr.ac.gachon.sw.gbro.setting.SettingActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding>{
    long lastPressedTime = 0;
    long backPressedTime = 2000;
    private String searchName = null;
    private static int selectedPosition = 0;

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
        CustomactionbarBinding customactionbarBinding = customActionBar.getBinding();

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

        // 액션바에서 게시물명 입력받음
        customactionbarBinding.searchname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    searchName = customactionbarBinding.searchname.getText().toString();
                    if(!searchName.equals("") && searchName.length() != 0){
                        BoardFragment boardFragment = BoardFragment.newInstance(searchName, selectedPosition);
                        getSupportFragmentManager().beginTransaction().replace(binding.flBoard.getId(), boardFragment).commit();
                    }
                    else {
                        BoardFragment boardFragment = new BoardFragment();
                        getSupportFragmentManager().beginTransaction().replace(binding.flBoard.getId(), boardFragment).commit();
                    }
                }
                return false;
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boardtype));
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        customactionbarBinding.spinner.setAdapter(adapter);

        // 액션바의 Spinner 선택
        customactionbarBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                BoardFragment boardFragment = BoardFragment.newInstance(searchName, selectedPosition);
                getSupportFragmentManager().beginTransaction().replace(binding.flBoard.getId(), boardFragment).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
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

        // 채팅 버튼
        binding.fabChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        // 설정 버튼
        binding.fabSetting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }
}