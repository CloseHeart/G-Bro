package kr.ac.gachon.sw.gbro.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.map.GeofenceReceiver;

public class GPSService extends Service {

    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private static final int RADIUS = 100;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String[] buildings = getResources().getStringArray(R.array.gachon_globalcampus_coordinate);

        addGeofecne(buildings);
    }

    // Geofence list 추가
    private void addGeofecne(String[] list){
        if(list == null || list.length == 0){
            Log.d(getClass().getName(), "list is empty");
            onDestroy();    // 빌딩들이 없으니, 서비스 종료
        }
        for (int i = 0; i < list.length; i++) {
            String[] res = list[i].split(",");

            // 빌딩 지오펜스 추가
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(i)) // 이벤트 발생시 BroadcastReceiver에서 구분할 id (빌딩 타입)
                    .setCircularRegion(Double.parseDouble(res[0]), Double.parseDouble(res[1]), RADIUS)  // 위치 및 반경(m)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)    // Geofence 만료 시간
                    .setLoiteringDelay(1000 * 60)   // 1분 머물기 시간
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL) // 머물기 감지시
                    .build());
        }
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL); // 1분 머물면 지오펜스 이벤트
        builder.addGeofences(this.geofenceList);
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

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 지오펜싱 클라이언트
        geofencingClient = LocationServices.getGeofencingClient(this);

        // 권한 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
            return 0;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "ACCESS_BACKGROUND_LOCATION 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
            return 0;
        }

        // 지오펜싱 추가
        geofencingClient.addGeofences(getGeofencingRequest(geofenceList), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(getClass().getName(), "Success add Geofences");
                Toast.makeText(getApplicationContext(), "지오펜싱 추가 성공", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(getClass().getName(), "Fail add Geofences");
                Toast.makeText(getApplicationContext(), "지오펜싱 추가 실패", Toast.LENGTH_SHORT).show();
            }
        });

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}