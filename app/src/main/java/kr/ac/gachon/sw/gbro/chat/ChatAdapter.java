package kr.ac.gachon.sw.gbro.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import kr.ac.gachon.sw.gbro.R;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.ChatData;
import kr.ac.gachon.sw.gbro.util.model.User;

// TODO : ViewBinding으로 전환
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChatData> items = new ArrayList<ChatData>();
    private Context context;
    private User targetUser = null;
    public Bitmap targetUserProfile = null;
    private String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 내 uid

    public ChatAdapter(Context context, User targetUser) {
        this.context = context;
        this.targetUser = targetUser;
    }

    // 내 채팅 홀더
    public static class ChatMyViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView dateTextView;

        public ChatMyViewHolder(@NonNull final View itemView){
            super(itemView);
            messageTextView = itemView.findViewById(R.id.my_chatmessage_tv_message);
            dateTextView = itemView.findViewById(R.id.my_chatmessage_tv_date);
        }
    }

    // 다른 사람 채팅 홀더
    public static class ChatOtherViewHolder extends  RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        CardView profileCardView;
        TextView nicknameTextView;
        TextView dateTextView;
        TextView messageTextView;
        ImageView profileUrl;   // 프로필 사진 url

        public ChatOtherViewHolder (@NonNull final View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.other_chat_message_item_linear);
            profileCardView = itemView.findViewById(R.id.other_chatmessage_iv_profile);
            nicknameTextView = itemView.findViewById(R.id.other_chatmessage_tv_nickname);
            dateTextView = itemView.findViewById(R.id.other_chatmessage_tv_date);
            messageTextView = itemView.findViewById(R.id.other_chatmessage_tv_message);
            profileUrl = itemView.findViewById(R.id.other_profile_url);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                Log.d("ViewType", "ViewType : " + viewType);
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat_message, parent, false);
                return new ChatMyViewHolder(view);
            case 1:
                Log.d("ViewType", "ViewType : " + viewType);
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_chat_message, parent, false);
                return new ChatOtherViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ChatData model = items.get(position);

        if(model.getUserId().equals(mUid)){
            ChatMyViewHolder chatMyViewHolder = (ChatMyViewHolder) holder;
            ((ChatMyViewHolder) holder).messageTextView.setText(model.getMessage());
            ((ChatMyViewHolder) holder).dateTextView.setText(Util.timeStamptoDetailString(model.getDate()));
        }
        else {
            ChatOtherViewHolder otherViewHolder = (ChatOtherViewHolder) holder;
            ((ChatOtherViewHolder) holder).nicknameTextView.setText(targetUser.getUserNickName());

            if(targetUserProfile != null) {
                ((ChatOtherViewHolder) holder).profileUrl.setImageBitmap(targetUserProfile);
            }
            else {
                if(targetUser.getUserProfileImgURL() != null) {
                    CloudStorage.getImageFromURL(targetUser.getUserProfileImgURL())
                            .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                @Override
                                public void onComplete(@NonNull Task<byte[]> task) {
                                    if (task.isSuccessful()) {
                                        targetUserProfile = Util.byteArrayToBitmap(task.getResult());
                                        ((ChatOtherViewHolder) holder).profileUrl.setImageBitmap(targetUserProfile);
                                    } else {
                                        targetUserProfile = Util.drawableToBitmap(context, R.drawable.profile);
                                        ((ChatOtherViewHolder) holder).profileUrl.setImageBitmap(targetUserProfile);
                                    }

                                }
                            });
                }
                else {
                    targetUserProfile = Util.drawableToBitmap(context, R.drawable.profile);
                    ((ChatOtherViewHolder) holder).profileUrl.setImageBitmap(targetUserProfile);
                }
            }
            ((ChatOtherViewHolder) holder).messageTextView.setText(model.getMessage());
            ((ChatOtherViewHolder) holder).dateTextView.setText(Util.timeStamptoDetailString(model.getDate()));
        }
    }

    /**
     * Item view type
     * 0 : My chatting type
     * 1 : Other chatting type
     * @return 0 or 1
     */
    @Override
    public int getItemViewType(int position) {
        ChatData chatData = items.get(position);
        Log.d("GetItemType", "GetItemViewType" + chatData.getUserId());
        Log.d("Token", "GetItemViewTypeMyToken" + mUid);

        if(chatData.getUserId().equals(mUid)) return 0; // 내가 보낸 메세지
        else return 1;  // 다른 사람이 보낸 메세지
    }

    @Override
    public int getItemCount() { return items.size(); }

    // 하나 추가
    public void addItem(ChatData chatData){
        items.add(chatData);
    }

    // 한꺼번에 추가
    public void addItems(ArrayList<ChatData> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
    }
}
