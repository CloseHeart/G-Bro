package kr.ac.gachon.sw.gbro.chat;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.databinding.MyChatMessageBinding;
import kr.ac.gachon.sw.gbro.databinding.OtherChatMessageBinding;

import java.util.ArrayList;
import kr.ac.gachon.sw.gbro.util.model.ChatData;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ChatData> items = new ArrayList<ChatData>();
    private String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 내 uid

    public ChatAdapter(Context context, ArrayList<ChatData> items){
        this.context = context;
        addItems(items);
    }

    // 내 채팅 홀더
    public class ChatMyViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView dateTextView;

        public ChatMyViewHolder(@NonNull final View itemView){
            super(itemView);
            messageTextView = itemView.findViewById(R.id.my_chatmessage_tv_message);
            dateTextView = itemView.findViewById(R.id.my_chatmessage_tv_date);
        }
    }

    // 다른 사람 채팅 홀더
    public class ChatOtherViewHolder extends  RecyclerView.ViewHolder{
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
            ((ChatMyViewHolder) holder).dateTextView.setText(model.getDate());
        }
        else{
            ChatOtherViewHolder otherViewHolder = (ChatOtherViewHolder) holder;
            ((ChatOtherViewHolder) holder).nicknameTextView.setText(model.getUserName());

            // TODO : 프로필 사진 담아야함
            if(model.getProfileUrl().equals("basic") || model.getProfileUrl().equals("")){  // 프로필 사진 default
            }
            else{
                // TODO : 프로필 사진 있으면, URL 설정
            }
            ((ChatOtherViewHolder) holder).messageTextView.setText(model.getMessage());
            ((ChatOtherViewHolder) holder).dateTextView.setText(model.getDate());
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
    }

    public void clear() {
        items.clear();
    }
}
