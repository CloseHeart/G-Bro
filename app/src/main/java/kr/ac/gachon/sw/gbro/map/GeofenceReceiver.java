package kr.ac.gachon.sw.gbro.map;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import kr.ac.gachon.sw.gbro.MainActivity;
import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.service.LocalNotiService;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Util;

public class GeofenceReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.w(GeofenceReceiver.this.getClass().getSimpleName(), errorMessage);
            return;
        }

        Log.d(GeofenceReceiver.this.getClass().getSimpleName(), "Geofence No Error");
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d(GeofenceReceiver.this.getClass().getSimpleName(), "geofenceTransition : " + geofenceTransition);

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            Log.d(GeofenceReceiver.this.getClass().getSimpleName(), "geofenceTransition = " + geofenceTransition);
            Log.d(GeofenceReceiver.this.getClass().getSimpleName(), "triggeringGeofences : " + triggeringGeofences);

            for(Geofence currentGeofence : triggeringGeofences){
                int buildingType = Integer.parseInt(currentGeofence.getRequestId());

                Firestore.getBuildingPost(buildingType).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(getClass().getName(), "Success Get Post");
                            int count = task.getResult().size();

                            if(count == 0){
                                Log.d(GeofenceReceiver.this.getClass().getSimpleName(), "No Post in " + buildingType);
                            }
                            else {
                                String buildingName = context.getResources().getStringArray(R.array.gachon_globalcampus_building2)[buildingType];
                                Log.d(GeofenceReceiver.this.getClass().getSimpleName(), "Post : " + count + " in + " + buildingType);

                                showNearbyNotification(buildingName, count);
                            }
                        }
                        else{
                            Log.d(getClass().getName(), "Fail Get Post");
                        }
                    }
                });
            }
        }

    }

    private void showNearbyNotification(String buildingName, int postCnt){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, mainIntent, 0);

            NotificationChannel notiChannel = new NotificationChannel(Util.NEARBY_ID, context.getResources().getString(R.string.nearbynoti_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context, Util.NEARBY_ID)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(context.getResources().getString(R.string.nearbynoti_title))
                    .setContentText(context.getResources().getString(R.string.nearbynoti_msg, buildingName, postCnt))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notiChannel);
            notificationManager.notify(101, notiBuilder.build());
        }
    }
}