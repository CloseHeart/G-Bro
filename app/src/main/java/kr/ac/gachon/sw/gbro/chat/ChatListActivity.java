package kr.ac.gachon.sw.gbro.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardAdapter;
import kr.ac.gachon.sw.gbro.databinding.ActivityChatlistBinding;
import kr.ac.gachon.sw.gbro.util.model.ChatRoom;

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
        setRefresh();
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

        // TODO : 채팅방 목록 가져온 다음 각 채팅방 ID를 ChatRoom의 setRoomId로 설정하고 onClick에서 이에 따라 제어
    }

    /**
     * 새로고침 설정
     * @author Minjae Seon
     */
    private void setRefresh() {
        binding.swipeChatlist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO : 새로 고침 반영
                binding.swipeChatlist.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v, ChatRoom chatRoom) {
        // TODO : 채팅방 연결
    }
}
