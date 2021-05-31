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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.board.WriteActivity;
import kr.ac.gachon.sw.gbro.chat.ChatListActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityMainBinding;
import kr.ac.gachon.sw.gbro.databinding.CustomactionbarBinding;
import kr.ac.gachon.sw.gbro.map.MapFragment;
import kr.ac.gachon.sw.gbro.service.LocalNotiService;
import kr.ac.gachon.sw.gbro.setting.SettingActivity;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Preferences;
import kr.ac.gachon.sw.gbro.util.model.User;

public class MainActivity extends BaseActivity<ActivityMainBinding>{
    private long lastPressedTime = 0;
    private long backPressedTime = 2000;
    private String searchName = null;
    private static int selectedPosition = 0;
    private Preferences prefs;

    @Override
    protected ActivityMainBinding getBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharedPreferences
        prefs = new Preferences(getApplicationContext());

        setFragment();
        setSlidingPanel();
        setFab();
        setService();
        checkFCMToken();
    }

    @Override
    public void onBackPressed() {
        // Fab Menu가 열려있다면
        if(binding.fabmenu.isExpanded()) {
            // 닫기
            binding.fabmenu.collapse();
        }
        // Panel이 열려있다면
        else if(binding.mainpanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
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

    /**
     * FloatingActionButton을 설정한다
     * @author Minjae Seon
     * @return Void
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

    private void checkFCMToken() {
        if(Auth.getCurrentUser() != null) {
            Log.d(MainActivity.this.getClass().getSimpleName(), "Check FCM Token");

            Firestore.getUserData(Auth.getCurrentUser().getUid())
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> tokenTask) {
                                                if(task.isSuccessful()) {
                                                    if(user.getFcmToken() == null || !user.getFcmToken().equals(tokenTask.getResult())) {
                                                        Firestore.setUserFcmToken(Auth.getCurrentUser().getUid(), tokenTask.getResult())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> setTask) {
                                                                        if (setTask.isSuccessful()) {
                                                                            Log.d(MainActivity.this.getClass().getSimpleName(), "Update Token Success");
                                                                        }
                                                                        else {
                                                                            Log.e(MainActivity.this.getClass().getSimpleName(), "Update Token Error", setTask.getException());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                                else {
                                                    Log.e(MainActivity.this.getClass().getSimpleName(), "Get Token Error", tokenTask.getException());
                                                }
                                            }
                                        });
                            }
                            else {
                                Log.e(MainActivity.this.getClass().getSimpleName(), "Get User Data Error", task.getException());
                            }
                        }
                    });

        }
    }

    private void setService() {
        if(prefs.getBoolean("keyWordOnOff", false) || prefs.getBoolean("nearbyOnOff", false)) {
            startService(new Intent(getApplicationContext(), LocalNotiService.class));
        }
    }
}