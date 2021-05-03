package kr.ac.gachon.sw.gbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.databinding.ActivityMainBinding;
import kr.ac.gachon.sw.gbro.map.MapFragment;
import me.relex.circleindicator.CircleIndicator;

public class PostContentActivity extends BaseActivity<ActivityMainBinding> {

        PostAdapter adapter;
        ViewPager viewPager;
        Button btn_ctm;
        Button btn_ctc;

    @Override
    protected ActivityMainBinding getBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);
        // 아까 만든 view
        viewPager = (ViewPager) findViewById(R.id.view);
        //adapter 초기화
        adapter = new PostAdapter(this);
        viewPager.setAdapter(adapter);

        btn_ctm = (Button)findViewById(R.id.content_to_main);
        btn_ctm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

        });

        /*
        // 채팅 레이아웃이랑 연결시키면 될것 같음
        // Main 대신 chatting.class
        btn_ctc = (Button)findViewById(R.id.content_to_chat);
        btn_ctc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
               // Intent intent = new Intent (getApplicationContext(), MainActivity.class);
               // startActivity(intent);
            }

        });
        */

        CircleIndicator c_indicator = (CircleIndicator) findViewById(R.id.indicator);
        c_indicator.setViewPager(viewPager);

        // 스크롤 뷰 시험해보려고 1~ 20까지 그냥 출력한거라 나중에 삭제하기.
        TextView text = (TextView)findViewById(R.id.post_content);
        String txt="";
        for(int i=0;i<20;i++){
            txt+=i+ "\n";
            text.setText(txt);
        }
    }

}