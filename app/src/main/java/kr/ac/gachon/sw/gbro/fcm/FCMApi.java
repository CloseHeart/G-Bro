package kr.ac.gachon.sw.gbro.fcm;

import kr.ac.gachon.sw.gbro.util.model.ChatFCMModel;
import kr.ac.gachon.sw.gbro.util.model.FCMResponse;
import kr.ac.gachon.sw.gbro.util.model.KeywordFCMModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FCMApi {
    @POST("fcm/send")
    Call<FCMResponse> sendChatNotification(@Body ChatFCMModel model);

    @POST("fcm/send")
    Call<FCMResponse> sendKeywordNotificaiton(@Body KeywordFCMModel model);
}
