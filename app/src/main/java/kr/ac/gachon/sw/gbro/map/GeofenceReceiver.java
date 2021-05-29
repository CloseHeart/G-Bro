package kr.ac.gachon.sw.gbro.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.sw.gbro.util.Firestore;

public class GeofenceReceiver extends BroadcastReceiver {

    private Context context;
    private static String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.d(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // 건물 내에 내에 1분 이내로 머문 경우
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // geofencingEvent.getTriggeringLocation();

            // TODO :여러 건물의 반경 안에 있는 경우, 알림 여러번?  or 한 번에 다 보여줌?
            // TODO : NOTIFICATION("건물 이름"에 "분실물 갯수"개의 분실물이 있습니다.)
            for(Geofence currentGeofence : triggeringGeofences){
                int postCnt = getPostCount(Integer.parseInt(currentGeofence.getRequestId()));
                if(postCnt == 0){
                    Log.d(getClass().getName(), "No Post in " + currentGeofence.getRequestId());
                }
                else{
                    Log.d(getClass().getName(), "Post : " + postCnt + " in + " + currentGeofence.getRequestId());
                }

            }
            // TODO : 현재 건물에 분실물 몇 개인지 알려줘야함
        }
        else{

        }
    }

    private int getPostCount(int buildingType){
        final int[] count = {0};
        Firestore.getBuildingPost(buildingType).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(getClass().getName(), "Success Get Post");
                    count[0] = task.getResult().size();
                }
                else{
                    Log.d(getClass().getName(), "Fail Get Post");
                }
            }
        });
        return count[0];
    }

}