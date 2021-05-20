package kr.ac.gachon.sw.gbro.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.gachon.sw.gbro.LoginActivity;
import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Util;

public class FCMService extends FirebaseMessagingService {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final HashMap<String, Integer> msgList = new HashMap<>();
    private static int lastMsgNum = 1;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "Message Data Payload : " + remoteMessage.getData());
            showNotification(remoteMessage);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(LOG_TAG, "Refreshed token: " + token);

        if(Auth.getCurrentUser() != null) {
            Firestore.setUserFCMToken(Auth.getCurrentUser().getUid(), token)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d(LOG_TAG, "Success to Write FCM Token to Firestore");
                            }
                            else {
                                Log.d(LOG_TAG, "Failed to Write FCM Token to Firestore", task.getException());
                            }
                        }
                    });
        }
    }

    private void showNotification(RemoteMessage remoteMessage) {
        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, loginIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, getApplicationContext().getString(R.string.fcm_id))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(pIntent);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notiChannel = new NotificationChannel(getApplicationContext().getString(R.string.fcm_id), getApplicationContext().getString(R.string.fcm_name), NotificationManager.IMPORTANCE_HIGH);
            notiManager.createNotificationChannel(notiChannel);
        }

        // 데이터 가져옴
        Map<String, String> data = remoteMessage.getData();

        // Type이 NULL이 아니라면 - 필수
        if(data.get("type") != null) {
            // Type이 chat이라면
            if(data.get("type").equals("chat")) {
                String userId = data.get("userId");
                if(!msgList.containsKey(userId)) {
                    msgList.put(userId, lastMsgNum);
                    lastMsgNum++;
                }

                Log.d(LOG_TAG, "Msg by " + userId + "/ ID : " + msgList.get(userId));

                // Profile이 NULL이 아니라면
                if(data.get("profile") != null) {
                    // Profile에 담긴 URL로 데이터 가져와서 Notify
                    CloudStorage.getImageFromURL(data.get("profile"))
                            .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                @Override
                                public void onComplete(@NonNull Task<byte[]> task) {
                                    if(userId != null && msgList.get(userId) != null) {
                                        notiBuilder.setLargeIcon(Util.byteArrayToBitmap(task.getResult()));
                                        notiManager.notify(msgList.get(userId), notiBuilder.build());
                                    }
                                    else {
                                        Log.w(LOG_TAG, "User ID Not Found!");
                                    }
                                }
                            });
                }
                // 기본 아이콘으로 Notify
                else {
                    if(userId != null && msgList.get(userId) != null) {
                        notiBuilder.setLargeIcon(Util.drawableToBitmap(getApplicationContext(), R.drawable.profile));
                        notiManager.notify(msgList.get(userId), notiBuilder.build());
                    }
                    else {
                        Log.w(LOG_TAG, "User ID Not Found!");
                    }
                }
            }
            // chat 이외에는 기본 ID로
            else {
                notiManager.notify(Util.FCM_DEFAULT_ID, notiBuilder.build());
            }
        }
    }
}
