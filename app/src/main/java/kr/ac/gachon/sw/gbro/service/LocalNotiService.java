package kr.ac.gachon.sw.gbro.service;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import kr.ac.gachon.sw.gbro.map.GeofenceReceiver;
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

    private GeofencingClient geofencingClient;
    private static List<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;
    private static final int RADIUS = 80;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (boardUpdateListener != null) {
            boardUpdateListener.remove();
        }

        if(geofencingClient != null) {
            geofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(LocalNotiService.this.getClass().getSimpleName(), "Success add Geofences");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(LocalNotiService.this.getClass().getSimpleName(), "Failed Remove Geofences", e);
                        }
                    });
        }

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isKeywordFirst = true;
        prefs = new Preferences(getApplicationContext());

        boolean keywordOn = prefs.getBoolean("keyWordOnOff", false);
        boolean nearbyOn = prefs.getBoolean("nearbyOnOff", false);

        // ?????? ????????? null????????? ?????? ????????? ????????? ?????? ?????? ????????? ??????
        if (Auth.getCurrentUser() == null || (!keywordOn && !nearbyOn)) {
            stopSelf();
        }

        // ?????? ?????? ???????????? ?????? ??????
        if(nearbyOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // ??????????????? ?????? ??????????????????
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // ????????? ??????
                    stopSelf();
                }
            } else {
                // ?????? ?????? ?????? ??????????????????
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // ????????? ??????
                    stopSelf();
                }
            }
        }

        // Foreground ?????? ?????????
        initForegroundService();

        if (keywordOn) {
            boardUpdateListener =
                    Firestore.getPostData(2, null).addSnapshotListener((value, error) -> {
                        if (error != null) {
                            // Snapshot ?????? ?????? ??????
                            Log.w(LocalNotiService.this.getClass().getSimpleName(), "Snapshot Error!", error);
                        }

                        if (value != null) {
                            // ????????? ????????????
                            if (!isKeywordFirst) {
                                ArrayList<String> keywordList = prefs.getStringArrayList("keywordList", null);

                                if (keywordList != null && keywordList.size() > 0) {
                                    // ????????? ????????? ?????????
                                    List<DocumentChange> changeList = value.getDocumentChanges();
                                    // ?????? ????????? ?????? ??????
                                    for (DocumentChange change : changeList) {
                                        // Post??? ??????
                                        Post postData = change.getDocument().toObject(Post.class);
                                        postData.setPostId(change.getDocument().getId());

                                        // ??? ???????????? ????????? ????????????
                                        if (!postData.getWriterId().equals(Auth.getCurrentUser().getUid())) {
                                            // ????????? ????????????
                                            if (change.getType() == DocumentChange.Type.ADDED) {
                                                for (String keyword : keywordList) {
                                                    // ???????????? ????????? ???????????? ????????? ?????????
                                                    if (postData.getContent().trim().contains(keyword) || postData.getTitle().trim().contains(keyword)) {
                                                        // ?????? ??????
                                                        showKeywordNotification(keyword, postData);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // ???????????????
                            else {
                                // ???????????? ?????? ???????????? ??? ADDED?????? ????????? ?????? ?????? ??????
                                // ?????? ?????? ????????? false???
                                isKeywordFirst = false;
                            }
                        }
                        // Snapshot?????? ????????? ???????????? NULL?????????
                        else {
                            // ?????? ??????
                            Log.w(LocalNotiService.this.getClass().getSimpleName(), "Snapshot Data NULL!");
                        }
                    });
        }

        if (nearbyOn) {
            Log.d(LocalNotiService.this.getClass().getSimpleName(), "GPS Service start");
            String[] buildings = getResources().getStringArray(R.array.gachon_globalcampus_coordinate);
            addGeofence(buildings);

            // ???????????? ???????????????
            geofencingClient = LocationServices.getGeofencingClient(this);

            Log.d(LocalNotiService.this.getClass().getSimpleName(), "Geofence Size : " + geofenceList.size());

            geofencingClient.addGeofences(getGeofencingRequest(geofenceList), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(LocalNotiService.this.getClass().getSimpleName(), "Success add Geofences");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(LocalNotiService.this.getClass().getSimpleName(), "Fail add Geofences", e);
                }
            });
        }

        return START_STICKY;
    }

    /**
     * Foreground Service ???????????? ????????????
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
     * ????????? Noti ??????
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

    // Geofence list ??????
    private void addGeofence(String[] list){
        if(list == null || list.length == 0){
            Log.d(getClass().getName(), "list is empty");
            onDestroy();    // ???????????? ?????????, ????????? ??????
        }

        for (int i = 0; i < list.length - 1; i++) {
            String[] res = list[i].split(",");

            Log.d("Geofence", "Added " + Double.parseDouble(res[0]) + ", " + Double.parseDouble(res[1]));

            // ?????? ???????????? ??????
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(i)) // ????????? ????????? BroadcastReceiver?????? ????????? id (?????? ??????)
                    .setCircularRegion(Double.parseDouble(res[0]), Double.parseDouble(res[1]), RADIUS)  // ?????? ??? ??????(m)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)    // Geofence ?????? ??????
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER) // ????????? ?????????
                    .build());
        }
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(LocalNotiService.geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        } else {
            Intent intent = new Intent(this, GeofenceReceiver.class);
            geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return geofencePendingIntent;
    }

}
