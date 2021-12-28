package com.example.welfareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class IntroActivity extends AppCompatActivity {

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

        /*
        // 기존 IntroActivity : 2s 기다린 후 handler 내 run 메서드에서 intent 전환
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
         */

        requestLoginStatus(token, token_firebase, new VolleyCallBack() {
            @Override
            public void onSuccess() {
                if(sharedPreferences.getBoolean("success",false)){
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
                else{
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public interface VolleyCallBack {
        void onSuccess();
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

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, com.example.welfareapp.URLs.url_session, params, new Response.Listener<JSONObject>(){
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
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("IntroActivity Post process isSuccess?", "false");
                error.printStackTrace();
                Log.v("IntroActivty requestLoginStatus onResponse", error.toString());
            }
        });
        AppHelper.requestQueue.add(jsonRequest);
    }
}