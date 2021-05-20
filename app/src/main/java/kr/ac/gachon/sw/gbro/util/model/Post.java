package kr.ac.gachon.sw.gbro.util.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;

/*
 * Post Class
 * @author Minjae Seon
 */
public class Post implements Parcelable {
    // 게시글 ID
    private String postId;

    // 게시글 타입 (1 - 분실, 2 - 습득)
    private int type;

    // 게시글 제목
    private String title;

    // 게시글 내용
    private String content;

    // 사진 URL 리스트
    private ArrayList<String> photoUrlList;

    // 건물 Type (values/building.xml 파일 참조)
    private int summaryBuildingType;

    // 경로 좌표 Array
    private ArrayList<GeoPoint> locationList;

    // 게시자 ID
    private String writerId;

    // 작성 시간
    private Timestamp writeTime;

    // 완료 여부
    private boolean finished;

    public Post() {}

    public Post(int type, String title, String content, ArrayList<String> photoUrlList, int summaryBuildingType, ArrayList<GeoPoint> locationList, String writerId, Timestamp writeTime, boolean isFinished) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.photoUrlList = photoUrlList;
        this.summaryBuildingType = summaryBuildingType;
        this.locationList = locationList;
        this.writerId = writerId;
        this.writeTime = writeTime;
        this.finished = isFinished;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getPhotoUrlList() {
        return photoUrlList;
    }

    public void setPhotoUrlList(ArrayList<String> photoUrlList) {
        this.photoUrlList = photoUrlList;
    }

    public int getSummaryBuildingType() {
        return summaryBuildingType;
    }

    public void setSummaryBuildingType(int summaryBuildingType) {
        this.summaryBuildingType = summaryBuildingType;
    }

    @Exclude
    public String getSummaryBuildingName(Context context) {
        return context.getResources().getStringArray(R.array.gachon_globalcampus_building)[summaryBuildingType];
    }

    public ArrayList<GeoPoint> getLocationList() {
        return locationList;
    }

    public void setLocationList(ArrayList<GeoPoint> locationList) {
        this.locationList = locationList;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public Timestamp getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(Timestamp writeTime) {
        this.writeTime = writeTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postId);
        dest.writeInt(this.type);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeList(this.photoUrlList);
        dest.writeInt(this.summaryBuildingType);
        dest.writeList(this.locationList);
        dest.writeString(this.writerId);
        dest.writeParcelable(this.writeTime, 0);
        dest.writeValue(this.finished);
    }

    protected Post(Parcel in) {
        this.postId = in.readString();
        this.type = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.photoUrlList = in.readArrayList(String.class.getClassLoader());
        this.summaryBuildingType = in.readInt();
        this.locationList = in.readArrayList(GeoPoint.class.getClassLoader());
        this.writerId = in.readString();
        this.writeTime = in.readParcelable(Timestamp.class.getClassLoader());
        this.finished = (boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
