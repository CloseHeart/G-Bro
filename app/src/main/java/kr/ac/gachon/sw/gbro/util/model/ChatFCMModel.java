package kr.ac.gachon.sw.gbro.util.model;

import com.google.gson.annotations.SerializedName;

public class ChatFCMModel {
    @SerializedName("to")
    private String token;

    @SerializedName("notification")
    private NotificationModel notification;

    @SerializedName("data")
    private ChatFCMData chatFCMData;

    public ChatFCMModel(String token, NotificationModel notification, ChatFCMData chatFCMData) {
        this.token = token;
        this.notification = notification;
        this.chatFCMData = chatFCMData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

    public ChatFCMData getChatFCMData() {
        return chatFCMData;
    }

    public void setChatFCMData(ChatFCMData chatFCMData) {
        this.chatFCMData = chatFCMData;
    }
}
