package kr.ac.gachon.sw.gbro.util.model;

import com.google.firebase.Timestamp;

public class User {
    private String userEmail;
    private String userName;
    private String userProfileImgURL;
    private boolean isAdmin;
    private Timestamp registerTime;

    public User(String userEmail, String userName, String userProfileImgURL, Timestamp registerTime) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userProfileImgURL = userProfileImgURL;
        this.isAdmin = false;
        this.registerTime =  registerTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserNickName() {
        return userName;
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
