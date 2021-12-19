package com.example.welfareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    
    private EditText et_pwd;
    private EditText et_id;
    private static long back_pressed;

    // Firebase Push Notification
    private static final String TAG = "LoginActivity Firebase Push Token Process";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 이전 데이터 초기화
        clearApplicationData();

        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        et_id = findViewById(R.id.et_Id);
        et_pwd = findViewById(R.id.et_pwd);
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userLogin();

                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences sharedPreferences;
                                sharedPreferences= getSharedPreferences("user_info", MODE_PRIVATE);
                                Boolean isSuccess  = sharedPreferences.getBoolean("success", false);
                                String mToken = sharedPreferences.getString("token", "");
                                int statusCode = sharedPreferences.getInt("statusCode",0);
                                //String token_firebase = sharedPreferences.getString("token_firebase", "");

                                Log.v("sharedPreference  isSuccess", Boolean.toString(isSuccess));
                                Log.v("sharedPreference  mToken", mToken);

                                if(isSuccess){

                                    Log.v("Login Process", isSuccess.toString());
                                    Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("token", mToken);
                                    //bundle.putString("token_firebase", token_firebase);

                                    Intent intent = new Intent(LoginActivity.this, com.example.welfareapp.MainActivity.class);
                                    if(bundle!=null){
                                        intent.putExtras(bundle);
                                    }
                                    Log.v("intent change", "start!");
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다..", Toast.LENGTH_SHORT).show();
                                    Log.v("Login Process","failed");
                                }
                            }
                        },
                        1024
                );

            }
        });

        // register click method
        Button btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, com.example.welfareapp.RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // userLogin()
    public void userLogin(){

        final String id  = et_id.getText().toString();
        final String pwd = et_pwd.getText().toString();

        JSONObject params = new JSONObject();

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        try{
            params.put("user_id", id);
            params.put("user_password", pwd);
            Log.v("params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        // blank -> request info
        if(TextUtils.isEmpty(id)){
            et_id.setError("아이디를 입력해주세요.");
            et_id.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            et_pwd.setError("패스워드를 입력해주세요.");
            et_pwd.requestFocus();
            return;
        }

        /*
        // Firebase App Process to get token(11.27 added)
        FirebaseApp.initializeApp(getApplicationContext());
        try{
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                editor.putString("token_firebase", "");
                                editor.commit();
                                return;
                            }
                            String token = task.getResult();
                            String msg = getString(R.string.msg_token_fmt, token);
                            editor.putString("token_firebase", token);
                            editor.commit();
                            Log.d(TAG, msg);
                            Log.v(TAG, msg);
                        }
                    });
        }
        catch(Exception err){
            err.printStackTrace();
            editor.putString("token_firebase", "");
            editor.commit();
            Log.v("FirebaseApp error on LoginActivity", err.getMessage());
        }

         */

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, com.example.welfareapp.URLs.url_login, params, new Response.Listener<JSONObject>(){
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
                    editor.commit();
                    Log.v("on login response isSuccess", isSuccess.toString());
                    Log.v("on login response statusCode", Integer.toString(statusCode));
                    Log.v("on login response mToken", mToken);
                }
                catch(JSONException e){
                    e.printStackTrace();
                    Log.d("JSONException error", e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("login onResponse", "false");
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("Login onResponse Error", error.toString());
                    }
        });

        Log.v("jsonRequest", jsonRequest.toString());
        Log.v("jsonRequest url", jsonRequest.getUrl());
        AppHelper.requestQueue.add(jsonRequest);
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("Error", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
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
}