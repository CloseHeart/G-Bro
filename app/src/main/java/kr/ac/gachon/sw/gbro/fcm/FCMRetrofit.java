package kr.ac.gachon.sw.gbro.fcm;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import kr.ac.gachon.sw.gbro.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMRetrofit {
    private static final String FCM_SERVER_URL = "https://fcm.googleapis.com/";
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;

    public static Retrofit getClient(Context context) {
        if(okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalReq = chain.request();

                            Request newReq = originalReq.newBuilder()
                                    .header("Authorization", "key=" + context.getString(R.string.fcm_key))
                                    .header("Content-Type", "application/json")
                                    .method(originalReq.method(), originalReq.body())
                                    .build();

                            return chain.proceed(newReq);
                        }
                    })
                    .build();
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(FCM_SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
