package kr.ac.gachon.sw.gbro.util.model;

import com.google.firebase.Timestamp;

public class User {
    private String userEmail;
    private String userNickName;
    private String userProfileImgURL;
    private boolean isAdmin;
    private Timestamp registerTime;

    public User(String userEmail, String userNickName, String userProfileImgURL) {
        this.userEmail = userEmail;
        this.userNickName = userNickName;
        this.userProfileImgURL = userProfileImgURL;
        this.isAdmin = false;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public String getUserProfileImgURL() {
        return userProfileImgURL;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Timestamp getRegisterTime() {
        return registerTime;
    }
}
