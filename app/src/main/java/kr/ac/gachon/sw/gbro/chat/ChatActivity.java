package kr.ac.gachon.sw.gbro.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityChattingBinding;
import kr.ac.gachon.sw.gbro.util.model.ChatData;

public class ChatActivity extends BaseActivity<ActivityChattingBinding> {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private EditText edit_chat;

    @Override
    protected ActivityChattingBinding getBinding() {
        return ActivityChattingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btn_chat = findViewById(R.id.btn_chat);
        edit_chat = findViewById(R.id.edit_chat);

        // TODO : Firebase에서 채팅 기록 받기
        ArrayList<ChatData> chattings = new ArrayList<>();  // 채팅 데이터

        RecyclerView chat_recycle_view = findViewById(R.id.recycle_view_chat);
        // RecycleView에 LinearLayoutManager 객체 지정
        chat_recycle_view.setLayoutManager(new LinearLayoutManager(this));

        // RecycleView에 ChatAdapter 객체 지정
        ChatAdapter chatAdapter = new ChatAdapter(getApplicationContext(), chattings);
        chat_recycle_view.setAdapter(chatAdapter);

        // TODO : 메세지 전송 to Firebase
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
