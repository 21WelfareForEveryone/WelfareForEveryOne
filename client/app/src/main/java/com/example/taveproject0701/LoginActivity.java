package com.example.tave0915;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tave0915.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    
    private EditText et_pwd;
    private EditText et_id;

    UserLoginInfo userLoginInfo = new UserLoginInfo();
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        et_id = findViewById(R.id.et_Id);
        et_pwd = findViewById(R.id.et_pwd);
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userLogin();
                SharedPreferences sharedPreferences= getSharedPreferences("user_info", MODE_PRIVATE);
                Boolean isSuccess  = sharedPreferences.getBoolean("success", false);
                String mToken = sharedPreferences.getString("token", "");
                int statusCode = sharedPreferences.getInt("statusCode",0);
                Log.v("sharedPreference  isSuccess", Boolean.toString(isSuccess));
                Log.v("sharedPreference  mToken", mToken);

                if(isSuccess){
                    Log.v("Login Process", isSuccess.toString());

                    Bundle bundle = new Bundle();
                    bundle.putString("token", mToken);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    if(bundle!=null){
                        intent.putExtras(bundle);
                    }
                    Log.v("intent change", "start!");
                    startActivity(intent);
                    finish();
                }
                else{
                    Log.v("Login Process","failed");
                }
            }
        });

        // register click method
        Button btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.url_login, params, new Response.Listener<JSONObject>(){
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
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("login onResponse", "false");
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", id);
                params.put("user_password", pwd);
                return params;
            }
        };

        Log.v("jsonRequest", jsonRequest.toString());
        Log.v("jsonRequest url", jsonRequest.getUrl());

        if(sharedPreferences.getBoolean("success", false)){
            VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);
            Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다..", Toast.LENGTH_SHORT).show();
        }
    }
}