package kr.ac.gachon.sw.gbro.util.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class FCMResponse {
    @SerializedName("multicast_id")
    private String multicastId;

    @SerializedName("success")
    private int successNum;

    @SerializedName("failure")
    private int failureNum;

    @SerializedName("results")
    private resultsObject[] results;

    @NonNull
    @Override
    public String toString() {
        return "FCMResponse{" +
                "multicastId='" + multicastId + '\'' +
                ", successNum=" + successNum +
                ", failureNum=" + failureNum +
                ", results=" + Arrays.toString(results) +
                '}';
    }
}

class resultsObject {
    @SerializedName("message_id")
    private String msgId;

    @SerializedName("registration_id")
    private String regId;

    @SerializedName("error")
    private String error;

    @NonNull
    @Override
    public String toString() {
        return "resultsObject{" +
                "msgId='" + msgId + '\'' +
                ", regId='" + regId + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
