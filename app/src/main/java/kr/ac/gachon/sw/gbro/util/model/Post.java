package kr.ac.gachon.sw.gbro.util.model;

import android.content.Context;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import kr.ac.gachon.sw.gbro.R;

/*
 * Post Class
 * @author Minjae Seon
 */
public class Post {
    // 게시글 타입 (0 - 분실, 1 - 습득)
    private int type;

    // 게시글 제목
    private String title;

    // 게시글 내용
    private String content;

    // 사진 URL Array
    private String[] photoUrls;

    // 건물 Type (values/building.xml 파일 참조)
    private int summaryBuildingType;

    // 경로 좌표 Array
    private GeoPoint[] locationList;

    // 게시자 ID
    private String writerId;

    // 작성 시간
    private Timestamp writeTime;

    // 완료 여부
    private boolean isFinished;

    public Post(int type, String title, String content, String[] photoUrls, int summaryBuildingType, GeoPoint[] locationList, String writerId, Timestamp writeTime, boolean isFinished) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.photoUrls = photoUrls;
        this.summaryBuildingType = summaryBuildingType;
        this.locationList = locationList;
        this.writerId = writerId;
        this.writeTime = writeTime;
        this.isFinished = isFinished;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String[] getPhotoUrls() {
        return photoUrls;
    }

    public GeoPoint[] getLocationList() {
        return locationList;
    }

    public String getWriterId() {
        return writerId;
    }

    public Timestamp getWriteTime() {
        return writeTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public int getSummaryBuildingType() {
        return summaryBuildingType;
    }

    public String getSummaryBuildingName(Context context) {
        return context.getResources().getStringArray(R.array.gachon_globalcampus_building)[summaryBuildingType];
    }
}
