package kr.ac.gachon.sw.gbro.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardAdapter;
import kr.ac.gachon.sw.gbro.board.PostContentActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityChatlistBinding;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.model.ChatRoom;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class ChatListActivity extends BaseActivity<ActivityChatlistBinding> implements ChatListAdapter.onItemClickListener {
    ActionBar actionBar;
    RecyclerView rvChatList;
    ChatListAdapter chatListAdapter;

    @Override
    protected ActivityChatlistBinding getBinding() {
        return ActivityChatlistBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.chatlist_title);
        }

        setAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Google 정책에 따라 MenuItem에 Switch 사용하지 않고 if문 사용
        int itemId = item.getItemId();

        if(itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adapter 설정
     * @author Minjae Seon
     */
    private void setAdapter() {
        ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
        rvChatList = binding.rvChatlist;
        rvChatList.setHasFixedSize(true);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));
        chatListAdapter = new ChatListAdapter(this, chatRoomList, this);
        rvChatList.setAdapter(chatListAdapter);

        Firestore.getMyChatRoom(Auth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
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

                                // Post로 변환
                                ChatRoom chatRoom = change.getDocument().toObject(ChatRoom.class);
                                chatRoom.setRoomId(change.getDocument().getId());
                                chatListAdapter.addItem(chatRoom);
                            }

                            // Refresh
                            chatListAdapter.notifyDataSetChanged();
                        }
                        // Snapshot에서 넘어온 데이터가 NULL이라면
                        else {
                            // 에러 로그
                            Log.w(this.getClass().getSimpleName(), "Snapshot Data NULL!");
                        }
                    }
                });
    }

    @Override
    public void onClick(View v, ChatRoom chatRoom) {
        Intent chatActivity = new Intent(ChatListActivity.this, ChatActivity.class);

        String targetId;
        ArrayList<String> userList = chatRoom.getChatUserId();
        if(userList.get(0).equals(Auth.getCurrentUser().getUid())) {
            targetId = userList.get(1);
        }
        else {
            targetId = userList.get(0);
        }

        chatActivity.putExtra("chatid", chatRoom.getRoomId());
        chatActivity.putExtra("targetid", targetId);
        startActivity(chatActivity);
    }
}
