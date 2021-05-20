package kr.ac.gachon.sw.gbro.fcm;

import android.content.Context;

import com.squareup.okhttp.ResponseBody;

import kr.ac.gachon.sw.gbro.util.model.ChatFCMModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMApi {
    @POST("fcm/send")
    Call<ResponseBody> sendChatNotification(@Body ChatFCMModel model);
}
