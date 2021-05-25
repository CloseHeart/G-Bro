package kr.ac.gachon.sw.gbro.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.ResponseBody;

import java.util.Date;
import java.util.List;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityChattingBinding;
import kr.ac.gachon.sw.gbro.fcm.FCMApi;
import kr.ac.gachon.sw.gbro.fcm.FCMRetrofit;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Preferences;
import kr.ac.gachon.sw.gbro.util.model.ChatData;
import kr.ac.gachon.sw.gbro.util.model.ChatFCMData;
import kr.ac.gachon.sw.gbro.util.model.ChatFCMModel;
import kr.ac.gachon.sw.gbro.util.model.FCMResponse;
import kr.ac.gachon.sw.gbro.util.model.NotificationModel;
import kr.ac.gachon.sw.gbro.util.model.User;
import retrofit2.Callback;

public class ChatActivity extends BaseActivity<ActivityChattingBinding> {
    private String chatId;
    private String targetId;
    private ChatAdapter chatAdapter;
    private EditText edit_chat;
    private Button btn_chat;
    private User myUserdata;
    private User targetUser;
    private Preferences prefs;
    private ListenerRegistration chatListener;

    @Override
    protected ActivityChattingBinding getBinding() {
        return ActivityChattingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = new Preferences(getApplicationContext());

        Bundle bundle = getIntent().getExtras();

        btn_chat = binding.btnChat;
        edit_chat = binding.editChat;

        if(bundle != null) {
            chatId = bundle.getString("chatid");
            targetId = bundle.getString("targetid");
            prefs.setString("currentchat", chatId);

            getMyData();
            setAdapter();
        }
        else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        prefs.setString("currentchat", null);
        if(chatListener != null) {
            chatListener.remove();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Google 정책에 따라 MenuItem에 Switch 사용하지 않고 if문 사용
        int itemId = item.getItemId();

        // 저장 버튼
        if(itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                            targetUser = task.getResult().toObject(User.class);

                            if(getSupportActionBar() != null)
                                getSupportActionBar().setTitle(targetUser.getUserNickName());

                            targetUser.setUserId(task.getResult().getId());
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
                                                            Log.w(ChatActivity.this.getClass().getSimpleName(), "Chat Send Error!", task.getException());
                                                        } else {

                                                            // FCM Token NULL 아니면
                                                            if(targetUser.getFcmToken() != null) {
                                                                // 알림 전송
                                                                sendNotification(targetUser.getFcmToken(), myUserdata.getUserNickName(), edit_chat.getText().toString().trim());
                                                            }
                                                            else {
                                                                Log.w(ChatActivity.this.getClass().getSimpleName(), "Target FCM Token NULL");
                                                            }

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

        binding.recycleViewChat.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                binding.recycleViewChat.scrollBy(0, oldBottom - bottom);
            }
        });
    }

    /**
     * 채팅 실시간 업데이트
     */
    private void setChatUpdate() {
        chatListener = Firestore.getChatDataQuery(chatId)
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
                                // 추가된 경우
                                if(change.getType() == DocumentChange.Type.ADDED) {
                                    // ChatData로 변환
                                    ChatData chatData = change.getDocument().toObject(ChatData.class);

                                    // 어댑터에 추가하고 Refresh
                                    chatAdapter.addItem(chatData);
                                    chatAdapter.notifyDataSetChanged();

                                    // 자동 스크롤
                                    binding.recycleViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                                }
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

    /**
     * 사용자 User Data 로드
     */
    private void getMyData() {
        Firestore.getUserData(Auth.getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            myUserdata = task.getResult().toObject(User.class);
                            myUserdata.setUserId(task.getResult().getId());
                        }
                        else {
                            Log.w(this.getClass().getSimpleName(), "Snapshot Data NULL!");
                            Toast.makeText(ChatActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    /**
     * 채팅 FCM 전송
     * @param token 보낼 사용자 Token
     * @param userNick 보내는 사용자 Nickname
     * @param userMsg 유저 메시지
     */
    private void sendNotification(String token, String userNick, String userMsg) {
        NotificationModel notificationModel = new NotificationModel(userNick, userMsg);
        ChatFCMData chatFCMData = new ChatFCMData("chat", chatId, myUserdata.getUserProfileImgURL(), myUserdata.getUserId());
        ChatFCMModel chatFCMModel = new ChatFCMModel(token, notificationModel, chatFCMData);

        FCMApi sendChat = FCMRetrofit.getClient(this).create(FCMApi.class);
        retrofit2.Call<FCMResponse> responseBodyCall = sendChat.sendChatNotification(chatFCMModel);

        responseBodyCall.enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<FCMResponse> call, @NonNull retrofit2.Response<FCMResponse> response) {
                Log.d(ChatActivity.this.getClass().getSimpleName(),"Success\nMsg : " + response.message() + "\nBody : " + response.toString());
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<FCMResponse> call, @NonNull Throwable t) {
                Log.e(ChatActivity.this.getClass().getSimpleName(), "Failed!\nReq Body : " + call.toString(), t);
            }
        });
    }
}
