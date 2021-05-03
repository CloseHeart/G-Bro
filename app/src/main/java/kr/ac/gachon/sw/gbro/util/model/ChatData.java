package kr.ac.gachon.sw.gbro.util.model;

public class ChatData {

    private String userId;
    private String userName;
    private String message;
    private String date;
    private String profileUrl;

    public ChatData () {}

    public ChatData(String userId, String userName, String message, String date, String profileUrl) {
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.date = date;
        this.profileUrl = profileUrl;
    }

    public ChatData(String userId, String message){
        this.userId = userId;
        this.message = message;
    }

    public String getUserId(){ return userId; }
    public String getUserName(){ return userName; }
    public String getMessage(){ return message; }
    public String getDate() { return date; }
    public String getProfileUrl() { return profileUrl; }
}
