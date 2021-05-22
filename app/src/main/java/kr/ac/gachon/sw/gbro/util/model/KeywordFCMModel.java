package kr.ac.gachon.sw.gbro.util.model;

import com.google.gson.annotations.SerializedName;

public class KeywordFCMModel {
    @SerializedName("to")
    private String token;

    @SerializedName("notification")
    private NotificationModel notification;

    @SerializedName("data")
    private KeywordFCMData fcmData;

    public KeywordFCMModel(String token, NotificationModel notification, KeywordFCMData fcmData) {
        this.token = token;
        this.notification = notification;
        this.fcmData = fcmData;
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

    public KeywordFCMData getFcmData() {
        return fcmData;
    }

    public void setFcmData(KeywordFCMData fcmData) {
        this.fcmData = fcmData;
    }
}
