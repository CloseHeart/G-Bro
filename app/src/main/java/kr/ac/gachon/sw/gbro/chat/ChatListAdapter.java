package kr.ac.gachon.sw.gbro.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.databinding.ItemChatlistBinding;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.model.ChatRoom;
import kr.ac.gachon.sw.gbro.util.model.User;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private Context context;
    private ArrayList<ChatRoom> chatRoomList;

    public interface onItemClickListener {
        void onClick(View v, ChatRoom chatRoom);
    }

    private ChatListAdapter.onItemClickListener listener = null;

    public ChatListAdapter(Context context, ArrayList<ChatRoom> chatRoomList, ChatListAdapter.onItemClickListener listener) {
        this.context = context;
        this.chatRoomList = chatRoomList;
        this.listener = listener;
    }

public class ChatListViewHolder extends RecyclerView.ViewHolder {
    private ItemChatlistBinding binding;

    public ChatListViewHolder(@NonNull ItemChatlistBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, chatRoomList.get(getAdapterPosition()));
            }
        });
    }
}

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListAdapter.ChatListViewHolder(ItemChatlistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {
        ItemChatlistBinding binding = holder.binding;

        // 유저 데이터 가져와서 닉네임 및 이미지 설정
        Firestore.getUserData(chatRoomList.get(position).getChatUserId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // 성공했다면
                if(task.isSuccessful()) {
                    // 닉네임
                    User chatUser = task.getResult().toObject(User.class);
                    binding.tvUsername.setText(chatUser.getUserNickName());

                    // 이미지
                    if(chatUser.getUserProfileImgURL() != null) {
                        CloudStorage.getImageFromURL(chatUser.getUserProfileImgURL()).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> imgTask) {
                                if (imgTask.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgTask.getResult(), 0, imgTask.getResult().length);
                                    binding.ivUserprofile.setImageBitmap(bitmap);
                                }
                                // 프로필 사진 가져오는데 실패하면 기본 사진
                                else {
                                    binding.ivUserprofile.setImageResource(R.drawable.profile);
                                }
                            }
                        });
                    }
                    // 프로필사진 NULL 이면
                    else {
                        // 기본 사진
                        binding.ivUserprofile.setImageResource(R.drawable.profile);
                    }
                }
                // 실패시
                else {
                    // 로그 띄우고 뷰 날려버림
                    Log.w("ChatListAdapter", "Get User Data Error", task.getException());
                    holder.itemView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    /**
     * Chatroom 객체를 Adapter List에 추가
     * @author Minjae Seon
     * @param chatRoom ChatRoom Object
     */
    public void addItem(ChatRoom chatRoom) {
        chatRoomList.add(chatRoom);
        notifyDataSetChanged();
    }

    /**
     * ArrayList에 담긴 내용을 Adapter List에 전부 추가
     * @author Minjae Seon
     * @param chatRooms ArrayList<ChatRoom>
     */
    public void addAll(ArrayList<ChatRoom> chatRooms) {
        chatRoomList.clear();
        chatRoomList.addAll(chatRooms);
        notifyDataSetChanged();
    }

    /**
     * 모든 List 정보 삭제
     * @author Minjae Seon
     */
    public void clear() {
        chatRoomList.clear();
        notifyDataSetChanged();
    }
}
