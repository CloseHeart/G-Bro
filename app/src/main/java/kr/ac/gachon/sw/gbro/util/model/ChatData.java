package kr.ac.gachon.sw.gbro.util.model;

import com.google.firebase.Timestamp;

public class ChatData {

    // 메시지를 보낸 User ID
    private String userId;

    // 메시지 내용
    private String message;

    // 시간 정보
    private Timestamp date;

    public ChatData () {}

    public ChatData(String userId, String message, Timestamp date) {
        this.userId = userId;
        this.message = message;
        this.date = date;
    }

    public ChatData(String userId, String message){
        this.userId = userId;
        this.message = message;
    }

    public String getUserId(){ return userId; }
    public String getMessage(){ return message; }
    public Timestamp getDate() { return date; }
}
