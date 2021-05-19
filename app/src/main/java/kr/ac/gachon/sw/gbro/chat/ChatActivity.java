package kr.ac.gachon.sw.gbro.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityChattingBinding;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.ChatData;
import kr.ac.gachon.sw.gbro.util.model.User;

public class ChatActivity extends BaseActivity<ActivityChattingBinding> {
    private String chatId;
    private String targetId;
    private ChatAdapter chatAdapter;
    private EditText edit_chat;
    private Button btn_chat;
    private Bitmap userImage;

    @Override
    protected ActivityChattingBinding getBinding() {
        return ActivityChattingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        btn_chat = binding.btnChat;
        edit_chat = binding.editChat;

        if(bundle != null) {
            chatId = bundle.getString("chatid");
            targetId = bundle.getString("targetid");
            setAdapter();
        }
        else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 어댑터 설정
     */
    private void setAdapter() {
        Firestore.getUserData(targetId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            User targetUser = task.getResult().toObject(User.class);
                            // RecycleView에 LinearLayoutManager 객체 지정
                            binding.recycleViewChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                            chatAdapter = new ChatAdapter(ChatActivity.this, targetUser);
                            binding.recycleViewChat.setAdapter(chatAdapter);

                            setChatUpdate();

                            btn_chat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(!edit_chat.getText().toString().trim().isEmpty()) {
                                        // ChatData 생성
                                        ChatData chatData = new ChatData(Auth.getCurrentUser().getUid(), edit_chat.getText().toString(), new Timestamp(new Date()));

                                        // Chat 전송
                                        Firestore.sendChat(chatId, chatData)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        // 실패시
                                                        if (!task.isSuccessful()) {
                                                            // 실패 메시지
                                                            Toast.makeText(ChatActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                                                            Log.w(this.getClass().getSimpleName(), "Chat Send Error!", task.getException());
                                                        } else {
                                                            // 채팅 입력창 비우기
                                                            edit_chat.setText("");
                                                        }
                                                    }
                                                });
                                    }

                                }
                            });
                        }
                        else {
                            Log.w(ChatActivity.this.getClass().getSimpleName(), "Get Target User Data Error!", task.getException());
                            Toast.makeText(ChatActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    /**
     * 채팅 실시간 업데이트
     */
    private void setChatUpdate() {
        Firestore.getFirestoreInstance().collection("chatRoom").document(chatId).collection("chatData")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                        // 에러 존재시
                        if(error != null) {
                            // Snapshot 에러 로그 출력
                            Log.w(this.getClass().getSimpleName(), "Snapshot Error!", error);
                        }

                        if(value != null) {
                            // 변경된 리스트 가져옴
                            List<DocumentChange> changeList = value.getDocumentChanges();
                            // 변경 리스트 전체 반복
                            for(DocumentChange change : changeList) {
                                // ChatData로 변환
                                ChatData chatData = change.getDocument().toObject(ChatData.class);

                                // 어댑터에 추가하고 Refresh
                                chatAdapter.addItem(chatData);
                                chatAdapter.notifyDataSetChanged();

                                // 자동 스크롤
                                binding.recycleViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                            }
                        }
                        // Snapshot에서 넘어온 데이터가 NULL이라면
                        else {
                            // 에러 로그
                            Log.w(this.getClass().getSimpleName(), "Snapshot Data NULL!");
                        }
                    }
                });
    }
}
