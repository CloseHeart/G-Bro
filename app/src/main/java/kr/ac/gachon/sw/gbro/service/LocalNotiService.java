package kr.ac.gachon.sw.gbro.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.board.PostContentActivity;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Preferences;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.Post;
import kr.ac.gachon.sw.gbro.util.model.User;

public class LocalNotiService extends Service {
    private Preferences prefs;
    private ListenerRegistration boardUpdateListener;
    private boolean isKeywordFirst;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(boardUpdateListener != null) {
            boardUpdateListener.remove();
        }

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isKeywordFirst = true;
        prefs = new Preferences(getApplicationContext());

        boolean keywordOn = prefs.getBoolean("keyWordOnOff", false);
        boolean nearbyOn = prefs.getBoolean("nearbyOnOff", false);

        // 현재 유저가 null이거나 아무 알림도 켜지지 않은 경우 서비스 정지
        if(Auth.getCurrentUser() == null || (!keywordOn && !nearbyOn)) {
            stopSelf();
        }

        // Foreground 알림 활성화
        initForegroundService();

        if(keywordOn) {
            boardUpdateListener =
                    Firestore.getPostData(2, null).addSnapshotListener((value, error) -> {
                        if(error != null) {
                            // Snapshot 에러 로그 출력
                            Log.w(LocalNotiService.this.getClass().getSimpleName(), "Snapshot Error!", error);
                        }

                        if(value != null) {
                            // 처음이 아니라면
                            if(!isKeywordFirst) {
                                ArrayList<String> keywordList = prefs.getStringArrayList("keywordList", null);

                                if (keywordList != null && keywordList.size() > 0) {
                                    // 변경된 리스트 가져옴
                                    List<DocumentChange> changeList = value.getDocumentChanges();
                                    // 변경 리스트 전체 반복
                                    for (DocumentChange change : changeList) {
                                        // Post로 변환
                                        Post postData = change.getDocument().toObject(Post.class);
                                        postData.setPostId(change.getDocument().getId());

                                        // 글 작성자가 본인이 아니라면
                                        if(!postData.getWriterId().equals(Auth.getCurrentUser().getUid())) {
                                            // 추가된 경우라면
                                            if (change.getType() == DocumentChange.Type.ADDED) {
                                                for (String keyword : keywordList) {
                                                    // 내용이나 제목에 일치하는 내용이 있다면
                                                    if (postData.getContent().trim().contains(keyword) || postData.getTitle().trim().contains(keyword)) {
                                                        // 알림 전송
                                                        showKeywordNotification(keyword, postData);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // 처음이라면
                            else {
                                // 처음에는 전체 데이터가 다 ADDED이기 때문에 알림 전송 안함
                                // 대신 체크 변수를 false로
                                isKeywordFirst = false;
                            }
                        }
                        // Snapshot에서 넘어온 데이터가 NULL이라면
                        else {
                            // 에러 로그
                            Log.w(LocalNotiService.this.getClass().getSimpleName(), "Snapshot Data NULL!");
                        }
                    });
        }

        return START_STICKY;
    }

    /**
     * Foreground Service 초기화를 진행한다
     * @author Minjae Seon
     */
    private void initForegroundService() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notiSettingIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getApplication().getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, Util.KEYWORD_FOREGROUND_ID);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notiSettingIntent, 0);
            NotificationChannel notiChannel = new NotificationChannel(Util.KEYWORD_FOREGROUND_ID, getString(R.string.localnoti_service_name), NotificationManager.IMPORTANCE_LOW);

            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, Util.KEYWORD_FOREGROUND_ID)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(getString(R.string.localnoti_foreground_title))
                    .setContentText(getString(R.string.localnoti_foreground_msg))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notiManager.createNotificationChannel(notiChannel);
            startForeground(10, notiBuilder.build());
        }
    }

    /**
     * 키워드 Noti 전송
     * @author Minjae Seon
     */
    private void showKeywordNotification(String keyword, Post post) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent postContentIntent = new Intent(getApplicationContext(), PostContentActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("post", post);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, postContentIntent, 0);
            NotificationChannel notiChannel = new NotificationChannel(Util.KEYWORD_ID, getString(R.string.keywordnoti_name), NotificationManager.IMPORTANCE_HIGH);

            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, Util.KEYWORD_ID)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(getString(R.string.keywordnoti_title))
                    .setContentText(getString(R.string.keywordnoti_msg, keyword))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notiManager.createNotificationChannel(notiChannel);
            notiManager.notify(100, notiBuilder.build());
        }
    }
}
