package kr.ac.gachon.sw.gbro.board;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class Post {
    int postNumber;
    int postType;
    Drawable thumbnailPhoto;
    String postTitle;
    String postSummary;
    String postLocation;
    int writerId;
    String writerNick;
    Date writeTime;

    public Post(int postNumber, int postType, String title, String summary, String location, int writeid, String writerNick) {
        this.postNumber = postNumber;
        this.postType = postType;
        this.postTitle = title;
        this.postSummary = summary;
        this.postLocation = location;
        this.writerId = writeid;
        this.writerNick = writerNick;
    }
}
