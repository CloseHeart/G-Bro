package kr.ac.gachon.sw.gbro.util.model;

import com.google.firebase.Timestamp;

public class User {
    private String userEmail;
    private String userNickName;
    private String userProfileImgURL;
    private boolean isAdmin;
    private Timestamp registerTime;
    private String fcmToken;

    public User() { }

    public User(String userEmail, String userNickName, String userProfileImgURL, Timestamp registerTime, String fcmToken) {
        this.userEmail = userEmail;
        this.userNickName = userNickName;
        this.userProfileImgURL = userProfileImgURL;
        this.isAdmin = false;
        this.registerTime =  registerTime;
        this.fcmToken = fcmToken;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfileImgURL() {
        return userProfileImgURL;
    }

    public void setUserProfileImgURL(String userProfileImgURL) {
        this.userProfileImgURL = userProfileImgURL;
    }

    public Timestamp getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Timestamp registerTime) {
        this.registerTime = registerTime;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
