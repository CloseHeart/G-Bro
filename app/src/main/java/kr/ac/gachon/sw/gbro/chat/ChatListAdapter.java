package kr.ac.gachon.sw.gbro.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.databinding.ItemChatlistBinding;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.model.ChatData;
import kr.ac.gachon.sw.gbro.util.model.ChatRoom;
import kr.ac.gachon.sw.gbro.util.model.User;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private Context context;
    private ArrayList<ChatRoom> chatRoomList;
    private ArrayList<ListenerRegistration> chatDataListenerList;

    public interface onItemClickListener {
        void onClick(View v, ChatRoom chatRoom);
    }

    private ChatListAdapter.onItemClickListener listener = null;

    public ChatListAdapter(Context context, ArrayList<ChatRoom> chatRoomList, ChatListAdapter.onItemClickListener listener) {
        this.context = context;
        this.chatRoomList = chatRoomList;
        this.listener = listener;
        chatDataListenerList = new ArrayList<>();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        for(ListenerRegistration listener : chatDataListenerList) {
            if(listener != null) listener.remove();
        }
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

        String targetId;
        ArrayList<String> userList = chatRoomList.get(position).getChatUserId();
        if(userList.indexOf(Auth.getCurrentUser().getUid()) == 0){
           targetId = userList.get(1);
        }else{
            targetId = userList.get(0);
        }

        // ??? ????????? Snapshot
        setMessageSnapshot(position, binding);

        // ?????? ????????? ???????????? ????????? ??? ????????? ??????
        Firestore.getUserData(targetId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // ???????????????
                if(task.isSuccessful()) {
                    // ?????????
                    User chatUser = task.getResult().toObject(User.class);
                    binding.tvUsername.setText(chatUser.getUserNickName());

                    // ?????????
                    if(chatUser.getUserProfileImgURL() != null) {
                        CloudStorage.getImageFromURL(chatUser.getUserProfileImgURL()).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> imgTask) {
                                if (imgTask.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgTask.getResult(), 0, imgTask.getResult().length);
                                    binding.ivUserprofile.setImageBitmap(bitmap);
                                }
                                // ????????? ?????? ??????????????? ???????????? ?????? ??????
                                else {
                                    binding.ivUserprofile.setImageResource(R.drawable.profile);
                                }
                            }
                        });
                    }
                    // ??????????????? NULL ??????
                    else {
                        // ?????? ??????
                        binding.ivUserprofile.setImageResource(R.drawable.profile);
                    }
                }
                // ?????????
                else {
                    // ?????? ????????? ??? ????????????
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
     * Chatroom ????????? Adapter List??? ??????
     * @author Minjae Seon
     * @param chatRoom ChatRoom Object
     */
    public void addItem(ChatRoom chatRoom) {
        chatRoomList.add(chatRoom);
        notifyDataSetChanged();
    }

    /**
     * ArrayList??? ?????? ????????? Adapter List??? ?????? ??????
     * @author Minjae Seon
     * @param chatRooms ArrayList<ChatRoom>
     */
    public void addAll(ArrayList<ChatRoom> chatRooms) {
        chatRoomList.clear();
        chatRoomList.addAll(chatRooms);
        notifyDataSetChanged();
    }

    /**
     * ?????? List ?????? ??????
     * @author Minjae Seon
     */
    public void clear() {
        chatRoomList.clear();
        notifyDataSetChanged();
    }

    private void setMessageSnapshot(int position, ItemChatlistBinding binding) {
        chatDataListenerList.add(
                Firestore.getChatDataQuery(chatRoomList.get(position).getRoomId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // ?????? ?????????
                        if(error != null) {
                            // Snapshot ?????? ?????? ??????
                            Log.w(this.getClass().getSimpleName(), "Snapshot Error!", error);
                        }

                        if(value != null) {
                            // ????????? ????????? ?????????
                            List<DocumentChange> changeList = value.getDocumentChanges();
                            // ?????? ????????? ?????? ??????
                            for(DocumentChange change : changeList) {
                                // ????????? ??????
                                if(change.getType() == DocumentChange.Type.ADDED) {
                                    ChatData chatData = change.getDocument().toObject(ChatData.class);
                                    binding.tvLastchat.setText(chatData.getMessage().trim().replaceAll("\n", ""));
                                }
                            }
                        }
                        // Snapshot?????? ????????? ???????????? NULL?????????
                        else {
                            // ?????? ??????
                            Log.w(this.getClass().getSimpleName(), "Snapshot Data NULL!");
                        }
                    }
                }));
    }
}
