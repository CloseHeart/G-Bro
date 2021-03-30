package kr.ac.gachon.sw.gbro;

import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;


import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.widget.LocationButtonView;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardFragment;
import kr.ac.gachon.sw.gbro.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnMapReadyCallback {
    @Override
    protected ActivityMainBinding getBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMap();
        setFragment();
    }

    private void setMap() {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fl_map);

        if(mapFragment == null) {
            NaverMapOptions options = new NaverMapOptions()
                    .camera(new CameraPosition(new LatLng(37.45082183610419, 127.12877229523757), 16))
                    .logoGravity(Gravity.START|Gravity.TOP)
                    .logoMargin(8, 8, 8, 8)
                    .locationButtonEnabled(false)
                    .compassEnabled(false)
                    .zoomControlEnabled(false);
            mapFragment = MapFragment.newInstance(options);
            getSupportFragmentManager().beginTransaction().add(R.id.fl_map, mapFragment).commit();
        }

        mapFragment.getMapAsync(naverMap -> {
            LocationButtonView locationButtonView = binding.mapLocation;
            locationButtonView.setMap(naverMap);
        });
    }


    private void setFragment() {
        BoardFragment boardFragment = new BoardFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_board, boardFragment).commit();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

    }
}