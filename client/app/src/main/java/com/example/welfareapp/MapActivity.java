package com.example.welfareapp;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.vo.Field;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
    int CURRENT_ZOOM = 15;
    int ZOOM_MAX = 17;
    int ZOOM_MIN = 13;
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

    private int searchRadius = 500;

    // 검색 목록 저장 array
    private List<HashMap<String, String>> placeList = new ArrayList<>();

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

        // initialize previous_marker array
        previous_marker = new ArrayList<Marker>();

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
                    previous_marker.clear();
                    updateUserLocation();
                }
                else{
                    Log.v("MapActivity get location permission", "Event start");
                    getLocationPermission();
                }
            }
        });

        /* SeekBar를 이용한 검색 반경 조정 */
        SeekBar seekBar = findViewById(R.id.map_radius_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView txt_search_radius = findViewById(R.id.txt_search_radius);
                txt_search_radius.setText(String.format("검색반경:%d(m)", seekBar.getProgress()));
                searchRadius = (int)seekBar.getProgress();
                zoomControl(searchRadius);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        DownloadUrl downloadUrl = new DownloadUrl();
      
        findViewById(R.id.map_button_1).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        previous_marker.clear();
                        String url = downloadUrl.getUrl("복지회관", 5000, placeAPIKey, defaultLocation);
                        downloadUrl.getPlaceDataFromUrl(url, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                updatePlaceListOnMapUI(placeList);
                            }
                        });
                    }
                }
        );
        findViewById(R.id.map_button_2).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        previous_marker.clear();
                        String url = downloadUrl.getUrl("보건소", searchRadius, placeAPIKey, defaultLocation);
                        downloadUrl.getPlaceDataFromUrl(url, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                updatePlaceListOnMapUI(placeList);
                            }
                        });
                    }
                }
        );
        findViewById(R.id.map_button_3).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        previous_marker.clear();
                        String url = downloadUrl.getUrl("경로당", searchRadius, placeAPIKey, defaultLocation);
                        downloadUrl.getPlaceDataFromUrl(url, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                updatePlaceListOnMapUI(placeList);
                            }
                        });
                    }
                }
        );
        findViewById(R.id.map_button_4).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        previous_marker.clear();
                        String url = downloadUrl.getUrl("주민센터", searchRadius, placeAPIKey, defaultLocation);
                        downloadUrl.getPlaceDataFromUrl(url, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                updatePlaceListOnMapUI(placeList);
                            }
                        });
                    }
                }
        );
        findViewById(R.id.map_button_5).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //showPlaceInformation(defaultLocation, PlaceType.LOCAL_GOVERNMENT_OFFICE, searchRadius);

                        previous_marker.clear();
                        String url = downloadUrl.getUrl("구청|시청", searchRadius, placeAPIKey, defaultLocation);
                        downloadUrl.getPlaceDataFromUrl(url, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                updatePlaceListOnMapUI(placeList);
                            }
                        });
                    }
                }
        );
        findViewById(R.id.map_button_6).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //showPlaceInformation(defaultLocation, PlaceType.HOSPITAL, searchRadius);
                        previous_marker.clear();
                        String url = downloadUrl.getUrl("병원|의원", searchRadius, placeAPIKey, defaultLocation);
                        downloadUrl.getPlaceDataFromUrl(url, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                updatePlaceListOnMapUI(placeList);
                            }
                        });
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

    private void zoomControl(int searchRadius){
        CURRENT_ZOOM = (int) (ZOOM_MIN + (ZOOM_MAX - ZOOM_MIN) * (1000 - searchRadius) / 1000);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(CURRENT_ZOOM), 512, null);
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
            Log.v("MapActivity getLocationPermission","activated / permission succeed");
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
                            lastKnownLocation = task.getResult(); // 현재 null object 형태로 생성됨
                            if (lastKnownLocation != null) {
                                // 현재는 구글 본사를 현재 위치로 인식하고 있음
                                // 공기계 혹은 다른 임베디드 환경에서 어떤 식으로 위치를 인식하는지 미리 파악할 필요 있음
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
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
            getLocationPermission();
        }
    }

    public void updateUserLocation(){
        if (mMap == null) {
            return;
        }
        else{
            mMap.clear();
        }
        if (previous_marker != null){
            previous_marker.clear();
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

    public void showPlaceInformation(LatLng location, String placeType, int searchRadius) {
        mMap.clear();
        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어
        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key(placeAPIKey)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(searchRadius) //500 미터 내에서 검색
                .type(placeType) // .type(PlaceType.LOCAL_GOVERNMENT_OFFICE) // (ex)  지역구청
                .build()
                .execute();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("현재 위치");
        markerOptions.position(defaultLocation);
        markerOptions.visible(true);
        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,DEFAULT_ZOOM));
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
                    try{
                        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                        String markerSnippet = getCurrentAddress(latLng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(place.getName());
                        markerOptions.snippet(markerSnippet);
                        Marker item = mMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Log.e("MapActivity onPlacesSuccess run Exception Error",e.getMessage());
                    }
                }
                try{
                    HashSet<Marker> hashSet = new HashSet<Marker>();
                    hashSet.addAll(previous_marker);
                    previous_marker.clear();
                    previous_marker.addAll(hashSet);
                }
                catch(Exception err){
                    Log.e("MapActivity onPlaceSuccess run Error Message", err.getMessage());
                }

                if(previous_marker.size() == 1 && previous_marker != null){
                    LatLng focusLatLng = previous_marker.get(0).getPosition();
                    previous_marker.get(0).showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(focusLatLng,DEFAULT_ZOOM));
                }
                else if(previous_marker.size() == 0){
                    Toast.makeText(getApplicationContext(), "검색반경 내 장소를 찾을 수 없습니다", Toast.LENGTH_LONG);
                }
                else if(previous_marker.size() > 1){
                    double latitude = 0;
                    double longitude = 0;
                    for(int i = 0; i < previous_marker.size(); i++){
                        latitude += previous_marker.get(i).getPosition().latitude;
                        longitude += previous_marker.get(i).getPosition().longitude;
                    }
                    latitude /= previous_marker.size();
                    longitude /= previous_marker.size();

                    LatLng centerLatLng = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, DEFAULT_ZOOM));
                }
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    public void updatePlaceListOnMapUI(List<HashMap<String, String>> placeList){
        mMap.clear();
        if(previous_marker != null){
            previous_marker.clear();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < placeList.size(); i ++){
                    try{
                        JSONObject jsonObject = new JSONObject(placeList.get(i));
                        Log.v("MapActivity jsonObject", jsonObject.toString());
                        LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("lng")));
                        String markerSnippet = getCurrentAddress(latLng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(jsonObject.getString("name"));
                        markerOptions.snippet(markerSnippet);
                        Log.v("MapActivity jsonObject lat", jsonObject.getString("lat"));
                        Log.v("MapActivity jsonObject lng", jsonObject.getString("lng"));
                        Log.v("MapActivity jsonObject name", jsonObject.getString("name"));
                        Marker item = mMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }
                    catch(JSONException err){
                        err.printStackTrace();
                        Log.e("MapActivity onPlacesSuccess run Exception Error",err.getMessage());
                    }
                }

                try{
                    HashSet<Marker> hashSet = new HashSet<Marker>();
                    hashSet.addAll(previous_marker);
                    previous_marker.clear();
                    previous_marker.addAll(hashSet);
                }
                catch(Exception err){
                    Log.e("MapActivity onPlaceSuccess run Error Message", err.getMessage());
                }

                Log.v("MapActivity placeList size", Integer.toString(placeList.size()));

                if(placeList.size() == 1){
                    LatLng focusLatLng = previous_marker.get(0).getPosition();
                    previous_marker.get(0).showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(focusLatLng,DEFAULT_ZOOM));
                }
                else if(placeList.size() == 0 || previous_marker == null){
                    Toast.makeText(getApplicationContext(), "검색반경 내 장소를 찾을 수 없습니다", Toast.LENGTH_LONG).show();
                }
                else if(placeList.size() > 1){
                    double latitude = 0;
                    double longitude = 0;
                    for(int i = 0; i < previous_marker.size(); i++){
                        latitude += previous_marker.get(i).getPosition().latitude;
                        longitude += previous_marker.get(i).getPosition().longitude;
                    }
                    latitude /= previous_marker.size();
                    longitude /= previous_marker.size();

                    LatLng centerLatLng = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, CURRENT_ZOOM));
                }
            }
        });
    }
    public class DownloadUrl{
        public String getUrl(String placeType, int searchRadius, String placeAPIKey, LatLng position){
            StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

            double latitide = position.latitude;
            double longitude = position.longitude;
            googleURL.append("location=" + latitide + "," + longitude);
            googleURL.append("&radius=" + searchRadius);
            googleURL.append("&name=" + placeType);
            googleURL.append("&sensor=true");
            if(placeType == "병원|의원"){
                googleURL.append("&type=hospital");
            }
            if(placeType == "구청|시청"){
                googleURL.append("&type=local_government_office");
            }
            googleURL.append("&key=" + placeAPIKey);
            Log.d("MapActivity DownloadUrl class", "url = " + googleURL.toString());
            return googleURL.toString();
        }
        public synchronized void getPlaceDataFromUrl(String url, final VolleyCallBack volleyCallBack){
            if(placeList !=null){
                placeList.clear();
            }
            JsonObjectRequest urlData = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        JSONArray jsonArray = response.getJSONArray("results");
                        Log.v("MapActivity getPlaceDataFromUrl function results", jsonArray.toString());
                        //List<HashMap<String, String>> placeList = getListFromJsonObject(jsonArray);
                        for(int i = 0; i < jsonArray.length(); i++){
                            HashMap<String, String> googlePlace = getSingleDataFromJsonObject((JSONObject)jsonArray.get(i));
                            Log.v("MapActivity getPlaceDataFromUrl googlePlace", googlePlace.toString());
                            placeList.add(googlePlace);
                        }
                        volleyCallBack.onSuccess();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Log.v("MapActivity getPlaceDataFromUrl Exception error", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.v("MapActivity getPlaceDataFromUrl Volley Error", error.toString());
                }
            });
            urlData.setShouldCache(false);
            AppHelper.requestQueue.add(urlData);
        }
        public HashMap<String, String> getSingleDataFromJsonObject(JSONObject jsonObject){
            HashMap<String, String> googlePlace = new HashMap<>();
            try{
                String latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
                String longitude =  jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
                String name = jsonObject.getString("name");
                String place_id = jsonObject.getString("place_id");
                String vicinity = jsonObject.getString("vicinity");

                googlePlace.put("lat", latitude);
                googlePlace.put("lng", longitude);
                googlePlace.put("name", name);
                googlePlace.put("place_id", place_id);
                googlePlace.put("vicinity", vicinity);
                return googlePlace;
            }
            catch(JSONException err){
                err.printStackTrace();
            }
            return googlePlace;
        }
        private List<HashMap<String, String>> getListFromJsonObject(JSONArray jsonArray){
            int totalNum = jsonArray.length();
            List <HashMap<String, String>> googlePlacesList = new ArrayList<>();
            HashMap<String, String> googlePlace = new HashMap<>();

            for(int i = 0; i < totalNum; i++){
                try{
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    String longitude =  jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
                    String name = jsonObject.getString("name");
                    String place_id = jsonObject.getString("place_id");
                    String vicinity = jsonObject.getString("vicinity");

                    googlePlace.put("lat", latitude);
                    googlePlace.put("lng", longitude);
                    googlePlace.put("name", name);
                    googlePlace.put("place_id", place_id);
                    googlePlace.put("vicinity", vicinity);
                    googlePlacesList.add(googlePlace);
                }
                catch(JSONException err){
                    err.printStackTrace();
                    Log.e("MapActivity getListFromJsonObject parsing error", err.getMessage());
                }
            }
            return googlePlacesList;
        }
    }
    public interface VolleyCallBack {
        void onSuccess();
    }

}