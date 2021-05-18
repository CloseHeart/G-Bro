package kr.ac.gachon.sw.gbro.util.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

public class ChatRoom {
    @Exclude
    // 방 ID - Firebase에는 저장되지 않고 불러올때만 사용
    private String roomId;

    // 채팅 상대 User ID
    private String chatUserId;

    // 채팅 Data List
    private ArrayList<ChatData> chatData;

    public ChatRoom() {

    }

    public ChatRoom(String chatUserId, ArrayList<ChatData> chatData) {
        this.chatUserId = chatUserId;
        this.chatData = chatData;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public ArrayList<ChatData> getChatData() {
        return chatData;
    }

    public void setChatData(ArrayList<ChatData> chatData) {
        this.chatData = chatData;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
