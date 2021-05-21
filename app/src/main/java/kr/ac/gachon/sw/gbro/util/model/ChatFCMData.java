package kr.ac.gachon.sw.gbro.util.model;

public class ChatFCMData {
    private String type;
    private String chatId;
    private String profile;
    private String userId;

    public ChatFCMData(String type, String chatId, String profile, String userId) {
        this.type = type;
        this.chatId = chatId;
        this.profile = profile;
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
