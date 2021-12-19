package com.example.welfareapp;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, PlacesListener {

    private FragmentManager fragmentManager;
    private MapFragment mapFragment;

    // back button listener
    private static long back_pressed;

    private GoogleMap mMap;
    private CameraPosition cameraPosition;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location and default zoom to use when location permission is not granted
    private final LatLng defaultLocation = new LatLng(37.513055, 127.059765); // 서울 강남구 위치를 디폴트 값으로 설정
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;

    // Used for place api
    private List<Marker> previous_marker = null;

    // Place API key
    private final String placeAPIKey = "AIzaSyDmaHqwSUJSjaS07Hod_L81DUynQBeV8m4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 최근 항목(위치, 카메라) 불러오기
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), placeAPIKey);
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // bundle with token
        Bundle bundle = getIntent().getExtras();

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateUserLocation();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_3);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_1:
                        Intent intent = new Intent(MapActivity.this, MainActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigation_2:
                        Intent intent2 = new Intent(MapActivity.this, ChatActivity.class);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        finish();
                        return true;
                    case R.id.navigation_3:
                        return true;
                    case R.id.navigation_4:
                        Intent intent4 = new Intent(MapActivity.this, MyProfileActivity.class);
                        intent4.putExtras(bundle);
                        startActivity(intent4);
                        finish();
                        return true;
                }
                return false;
            }
        });

        // 펼치기 + 숨기기 버튼 onClickListener
        ToggleButton toggle_display = findViewById(R.id.toggle_display);
        toggle_display.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.map_button_1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.map_button_2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.map_button_3).setVisibility(View.INVISIBLE);
                    findViewById(R.id.map_button_4).setVisibility(View.INVISIBLE);
                    findViewById(R.id.map_button_5).setVisibility(View.INVISIBLE);
                    findViewById(R.id.map_button_6).setVisibility(View.INVISIBLE);
                    findViewById(R.id.map_button_7).setVisibility(View.INVISIBLE);
                    findViewById(R.id.map_btn_container).getLayoutParams().height = 48;
                    findViewById(R.id.map_btn_container).getBackground().setAlpha(128);
                    findViewById(R.id.map_btn_container).requestLayout();
                }
                else{
                    findViewById(R.id.map_button_1).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_button_2).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_button_3).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_button_4).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_button_5).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_button_6).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_button_7).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_btn_container).getLayoutParams().height = 225;
                    findViewById(R.id.map_btn_container).getBackground().setAlpha(255);
                    findViewById(R.id.map_btn_container).requestLayout();
                }
            }
        });

        // 현재 위치 업데이트
        ImageButton btn_current_position = findViewById(R.id.btn_current_position);
        btn_current_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationPermissionGranted){
                    Log.v("MapActivity update current position", "Event start");
                    updateUserLocation();
                }
                else{
                    Log.v("MapActivity get location permission", "Event start");
                    getLocationPermission();
                }
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void getDeviceLocation(){
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                /*
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                 */
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        } else {
                            Log.d("MapActivity Error", "Current location is null. Using defaults.");
                            Log.e("MapActivity Error", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void updateUserLocation(){
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions.title("현재위치");
                makerOptions.snippet("코엑스");
                makerOptions.position(defaultLocation);
                mMap.addMarker(makerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                mMap.getUiSettings().setMyLocationButtonEnabled(false); // 현재 위치 update 버튼을 활성화시킨다
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateUserLocation();
    }

    public void showPlaceInformation(LatLng location, String placeType) {
        mMap.clear();
        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어
        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key(placeAPIKey)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(500) //500 미터 내에서 검색
                .type(placeType) // .type(PlaceType.LOCAL_GOVERNMENT_OFFICE) // (ex)  지역구청
                .build()
                .execute();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        /* ToDo: 버튼 클릭 시 대응되는 복지시설에 대한 marker 활성화 */
        /* Step 1 : getCurrentLocation */
        /* Step 2 : using place api to get place information list including (x,y) */
        /* Step 3 : 위치 정보 리스트 생성 및 버튼 클릭시 대응되는 기관에 대해 모두 marker option 추가 */

        mMap = googleMap;
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.title("현재 위치");
        makerOptions.position(defaultLocation);
        mMap.addMarker(makerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,DEFAULT_ZOOM));

        // 버튼 6개중 하나 선택시 보여지는 위치
        findViewById(R.id.map_button_1).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LatLng location = new LatLng(37.5162958434477, 127.05196608896408);
                        MarkerOptions makerOptions = new MarkerOptions();
                        makerOptions.title("강남구노인복지회관");
                        makerOptions.snippet("복지회관");
                        makerOptions.position(location);
                        mMap.addMarker(makerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,DEFAULT_ZOOM));
                    }
                }
        );
        findViewById(R.id.map_button_2).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LatLng location = new LatLng(37.517040387414724, 127.04190521599091);
                        MarkerOptions makerOptions = new MarkerOptions();
                        makerOptions.title("강남구보건소");
                        makerOptions.snippet("보건소");
                        makerOptions.position(location);
                        mMap.addMarker(makerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,DEFAULT_ZOOM));

                    }
                }
        );
        findViewById(R.id.map_button_3).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LatLng location = new LatLng(37.510557754240494, 127.05192267858015);
                        MarkerOptions makerOptions = new MarkerOptions();
                        makerOptions.title("봉전경로당");
                        makerOptions.snippet("경로당");
                        makerOptions.position(location);
                        mMap.addMarker(makerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,DEFAULT_ZOOM));
                    }
                }
        );
        findViewById(R.id.map_button_4).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LatLng location = new LatLng(37.51488034442946, 127.06286608908715);
                        MarkerOptions makerOptions = new MarkerOptions();
                        makerOptions.title("삼성1동주민센터");
                        makerOptions.snippet("주민센터");
                        makerOptions.position(location);
                        mMap.addMarker(makerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,DEFAULT_ZOOM));
                    }
                }
        );
        findViewById(R.id.map_button_5).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LatLng location = new LatLng(37.51860243936226, 127.04699425501457);
                        MarkerOptions makerOptions = new MarkerOptions();
                        makerOptions.title("강남구청");
                        makerOptions.snippet("구청");
                        makerOptions.position(location);
                        mMap.addMarker(makerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,DEFAULT_ZOOM));

                    }
                }
        );
        findViewById(R.id.map_button_6).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LatLng location = new LatLng(37.51974664860443, 127.04977690600703);
                        MarkerOptions makerOptions = new MarkerOptions();
                        makerOptions.title("우리들병원");
                        makerOptions.snippet("병원");
                        makerOptions.position(location);
                        mMap.addMarker(makerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,DEFAULT_ZOOM));
                    }
                }
        );

        findViewById(R.id.map_button_7).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LatLng location = new LatLng(37.513055, 127.059765);
                        MarkerOptions makerOptions = new MarkerOptions();
                        makerOptions.snippet("현재 위치");
                        makerOptions.position(location);
                        mMap.addMarker(makerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,14));
                    }
                }
        );
    }
    public String getCurrentAddress(LatLng latlng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        }
        catch (IOException ioException) {
            Toast.makeText(this, "Geocoder service not available", Toast.LENGTH_LONG).show();
            return "Geocoder service not available";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "Wrong GPS coordinate", Toast.LENGTH_LONG).show();
            return "Wrong GPS coordinate";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "Address not detected", Toast.LENGTH_LONG).show();
            return "Address not detected";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else {
            Toast.makeText(getBaseContext(), "뒤로가기 버튼을 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                    String markerSnippet = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);
                    Marker item = mMap.addMarker(markerOptions);
                    previous_marker.add(item);

                }
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }
}