package com.product.welfareapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class IntroActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // AppHelper 초기화
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        //hide actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String token_firebase = sharedPreferences.getString("token_firebase","");

        /**
         * GPS Permission Allowed => Login process
         * GPS Permission Denied => Exit whole process
         */

        // initialize locationPermissionGranted
        locationPermissionGranted = false;

        checkLocationPermission(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                Log.v("IntroActivity checkLocationPermission", "success");
                requestLoginStatus(token, token_firebase, new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        if(sharedPreferences.getBoolean("success",false)){
                            Log.v("IntroActivity Login Status", "remained");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.v("IntroActivity token",  sharedPreferences.getString("token",""));
                                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("token", sharedPreferences.getString("token",""));
                                    bundle.putString("token_firebase", sharedPreferences.getString("token_firebase",""));
                                    bundle.putInt("statusCode", sharedPreferences.getInt("statusCode",500));
                                    bundle.putBoolean("success", sharedPreferences.getBoolean("success",false));
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1024);
                        }
                        else{
                            Log.v("IntroActivity Login Status", "not remained");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1024);
                        }
                    }
                    @Override
                    public void onFailure() {
                        Log.v("IntroActivity Login Status", "process failed");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 1024);
                    }
                });
            }
            @Override
            public void onFailure() {
                requestLocationPermission();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
    }

    public interface VolleyCallBack {
        void onSuccess();
        void onFailure();
    }

    private synchronized void requestLoginStatus(String token, String token_firebase, final VolleyCallBack volleyCallBack){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            params.put("token_firebase", token_firebase);
            Log.v("params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, com.product.welfareapp.URLs.url_session, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.v("login onResponse", "true");
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    String mToken = response.getString("token");
                    int statusCode =  response.getInt("statusCode");
                    editor.putString("token", mToken);
                    editor.putInt("statusCode", statusCode);
                    editor.putBoolean("success", isSuccess);
                    editor.putString("token_firebase", token_firebase);
                    editor.commit();
                    volleyCallBack.onSuccess();
                }
                catch(JSONException e){
                    e.printStackTrace();
                    Log.d("JSONException error", e.getMessage());
                    volleyCallBack.onFailure();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("IntroActivity Post process isSuccess?", "false");
                error.printStackTrace();
                Log.v("IntroActivty requestLoginStatus onResponse", error.toString());
                volleyCallBack.onFailure();
            }
        });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                1024 * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        AppHelper.requestQueue.add(jsonRequest);
    }

    private void checkLocationPermission(VolleyCallBack volleyCallBack){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            volleyCallBack.onSuccess();
        }
        else{
            locationPermissionGranted = false;
            volleyCallBack.onFailure();
        }
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                },
                PERMISSIONS_REQUEST_LOCATION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", "");
                    String token_firebase = sharedPreferences.getString("token_firebase", "");
                    requestLoginStatus(token, token_firebase, new VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            if (sharedPreferences.getBoolean("success", false)) {
                                Log.v("IntroActivity Login Status", "remained");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.v("IntroActivity token", sharedPreferences.getString("token", ""));
                                        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("token", sharedPreferences.getString("token", ""));
                                        bundle.putString("token_firebase", sharedPreferences.getString("token_firebase", ""));
                                        bundle.putInt("statusCode", sharedPreferences.getInt("statusCode", 500));
                                        bundle.putBoolean("success", sharedPreferences.getBoolean("success", false));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1024);
                            } else {
                                Log.v("IntroActivity Login Status", "not remained");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1024);
                            }
                        }
                        @Override
                        public void onFailure() {
                            Log.v("IntroActivity Login Status", "process failed");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1024);
                        }
                    });
                } else {
                    Toast.makeText(this, "개인 위치 정보 제공에 동의하셔야 앱 사용이 가능합니다", Toast.LENGTH_LONG).show();
                    finishAndRemoveTask();
                }
                return;
        }
    }
}