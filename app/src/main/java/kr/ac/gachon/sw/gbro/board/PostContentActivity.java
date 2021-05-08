package kr.ac.gachon.sw.gbro.board;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.gachon.sw.gbro.MainActivity;
import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityPostContentBinding;
import me.relex.circleindicator.CircleIndicator;

public class PostContentActivity extends BaseActivity<ActivityPostContentBinding> {

        PostAdapter adapter;
        ViewPager viewPager;
        Button btn_ctm;
        Button btn_ctc;

    @Override
    protected ActivityPostContentBinding getBinding() { return ActivityPostContentBinding.inflate(getLayoutInflater());
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 아까 만든 view
        viewPager = binding.view;

        //adapter 초기화
        adapter = new PostAdapter(this);
        viewPager.setAdapter(adapter);

        btn_ctm = binding.contentToMain;

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

        CircleIndicator c_indicator = binding.indicator;
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