package kr.ac.gachon.sw.gbro.util.model;

public class KeywordFCMData {
    private int type;
    private String postId;
    private String thumbnailURL;

    public KeywordFCMData(int type, String postId, String thumbnailURL) {
        this.type = type;
        this.postId = postId;
        this.thumbnailURL = thumbnailURL;
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

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
