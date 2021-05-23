package kr.ac.gachon.sw.gbro.map;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.FragmentMapBinding;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class MapFragment extends BaseFragment<FragmentMapBinding> implements OnMapReadyCallback{
    private ArrayList<Marker> markers = null;

    private com.naver.maps.map.MapFragment mapFragment;
    private ArrayList<Integer> path = null;
    private boolean isMain = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private double lat,lon;

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        });
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        if(locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults)){
            if(!locationSource.isActivated()){
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
    @Override
    protected FragmentMapBinding getBinding() {
        return FragmentMapBinding.inflate(getLayoutInflater());
    }

    public static MapFragment getPathInstance(ArrayList<Integer> path){
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("path", path);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    public static MapFragment getMainInstance() {
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putBoolean("isMain", true);
        mapFragment.setArguments(args);
        return mapFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationSource= new FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE);
        if (getArguments() != null) {
            path = getArguments().getIntegerArrayList("path");
            isMain = getArguments().getBoolean("isMain");
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setMap();
        return getBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.getMapAsync(naverMap -> {
            if (path != null && !path.isEmpty()) {
                Log.d("MapFragment", "Path Map");
                String firstPos = getResources().getStringArray(R.array.gachon_globalcampus_coordinate)[path.get(0)];
                String[] posArray = firstPos.split(",");
                naverMap.setCameraPosition(new CameraPosition(new LatLng(Double.parseDouble(posArray[0]), Double.parseDouble(posArray[1])), 14.5));
                drawPath(naverMap, path);
            }
        });
    }

    private void setMap() {
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (com.naver.maps.map.MapFragment) fragmentManager.findFragmentById(binding.map.getId());

        if(mapFragment == null) {
            NaverMapOptions options = new NaverMapOptions()
                    .camera(new CameraPosition(new LatLng(37.45199894842855, 127.13179114165393), 14))
                    .locationButtonEnabled(true)
                    .logoGravity(Gravity.START | Gravity.TOP)
                    .logoMargin(8, 8, 8, 8)
                    .locationButtonEnabled(false)
                    .compassEnabled(false)
                    .zoomControlEnabled(false);
            mapFragment = com.naver.maps.map.MapFragment.newInstance(options);
            fragmentManager.beginTransaction().add(binding.map.getId(), mapFragment).commit();

            mapFragment.getMapAsync(naverMap -> {
                naverMap.setExtent(new LatLngBounds(new LatLng(37.44792028734633, 127.12628356183701), new LatLng(37.4570968690434, 127.13723061921826)));
                naverMap.setMinZoom(14.0);
                naverMap.setMaxZoom(0.0);

                if(isMain) {
                    Log.d("MapFragment", "Main Map");
                    drawMarker(naverMap);
                    onMapReady(naverMap);
                }

            });
        }
    }

    /**
     * 지도에 Marker를 표시한다
     * @author Subin Kim, Minjae Seon
     * @param naverMap NaverMap Object
     */
    public void drawMarker(NaverMap naverMap) {
        markers = new ArrayList<>();
        String[] buildingCoordinate = getResources().getStringArray(R.array.gachon_globalcampus_coordinate);

        Firestore.getUnfinishedPost(0)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            HashMap<Integer, Integer> lostNumList = new HashMap<>();
                            HashMap<Integer, Integer> foundNumList = new HashMap<>();

                            for(DocumentSnapshot doc : task.getResult().getDocuments()) {
                                Post post = doc.toObject(Post.class);
                                if(post.getType() == 1) {
                                    if(lostNumList.get(post.getSummaryBuildingType()) != null) {
                                        lostNumList.put(post.getSummaryBuildingType(), lostNumList.get(post.getSummaryBuildingType()) + 1);
                                    }
                                    else {
                                        lostNumList.put(post.getSummaryBuildingType(), 1);
                                    }
                                }
                                else if(post.getType() == 2) {
                                    if(foundNumList.get(post.getSummaryBuildingType()) != null) {
                                        foundNumList.put(post.getSummaryBuildingType(), foundNumList.get(post.getSummaryBuildingType()) + 1);
                                    }
                                    else {
                                        foundNumList.put(post.getSummaryBuildingType(), 1);
                                    }
                                }
                            }

                            for(int i = 0; i < buildingCoordinate.length - 1; i++) {
                                String[] posArray = buildingCoordinate[i].split(",");

                                Marker marker = new Marker();
                                marker.setPosition(new LatLng(Double.parseDouble(posArray[0]), Double.parseDouble(posArray[1])));
                                marker.setMap(naverMap);
                                marker.setWidth(40);
                                marker.setHeight(60);
                                marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
                                setInfoWindow(marker, i,
                                        lostNumList.get(i) == null ? 0 : lostNumList.get(i),
                                        foundNumList.get(i) == null ? 0 : foundNumList.get(i));
                                markers.add(marker);
                            }
                        }
                        else {
                            Log.e("MapFragment", "Get Unfinished Post Error!", task.getException());
                        }
                    }
                });
    }

    /**
     * 지도에 Path를 표시한다
     * @author Suyeon Jung, Minjae Seon
     * @param naverMap NaverMap Object
     * @param pathArr 건물 번호 ArrayList
     */
    public void drawPath(NaverMap naverMap, ArrayList<Integer> pathArr){
        // Exception
        if(pathArr.size() > 5) {
            return;
        }

        String [] str_coordinate = getResources().getStringArray(R.array.gachon_globalcampus_coordinate);
        ArrayList<LatLng> coordinates = new ArrayList<>();

        for (int i : pathArr){
            String [] arr = str_coordinate[i].split(",");
            coordinates.add(new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1])));
        }

        PathOverlay path = new PathOverlay();
        path.setCoords(coordinates);

        path.setMap(naverMap);
    }

    /**
     * Marker의 InfoWindow 설정
     * @author Subin Kim, Minjae Seon
     * @param marker Marker
     * @param buildingNum 건물 번호
     */
    private void setInfoWindow(Marker marker, int buildingNum, int lost, int found) {
        String[] buildingName = getResources().getStringArray(R.array.gachon_globalcampus_building);

        InfoWindow infoWindow = new InfoWindow();
        marker.setOnClickListener(overlay -> {
            if (marker.getInfoWindow() == null) { // 마커를 클릭할 때 정보창을 엶
                // 다른 열린 마커 닫기
                closeOpenMarkers();
                infoWindow.open(marker);
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }
            return true;
        });

        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return getString(R.string.Info_building_name, buildingName[buildingNum])
                        +"\n"
                        +getString(R.string.Info_lost_cnt,lost)
                        +"\n"
                        +getString(R.string.Info_get_cnt,found);
            }
        });
    }

    /**
     * 모든 열린 마커들을 닫음
     * @author Minjae Seon
     */
    private void closeOpenMarkers() {
        for (Marker marker : markers) {
            InfoWindow infoWindow = marker.getInfoWindow();
            if (infoWindow != null) {
                infoWindow.close();
            }
        }
    }
}
