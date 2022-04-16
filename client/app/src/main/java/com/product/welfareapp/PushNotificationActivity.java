package com.product.welfareapp;

import static com.product.welfareapp.URLs.url_push_toggle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PushNotificationActivity extends AppCompatActivity {

    // back button listener
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification);

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");

        Switch toggle_notification = (Switch)findViewById(R.id.toggle_notification);
        toggle_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                requestPushNotification(token, b, new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        String toastText = b?"푸시 알림 설정을 On 하였습니다.":"푸시 알림 설정을 Off 하였습니다.";
                        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // btn_back button listener
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PushNotificationActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }
    public interface VolleyCallBack{
        void onSuccess();
    }

    public synchronized void requestPushNotification(String token, boolean isChecked, final VolleyCallBack volleyCallBack){
        String toggleCommand = isChecked? "On":"Off";
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            params.put("toggle_command", toggleCommand);
        }
        catch(JSONException err){
            err.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_push_toggle, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("PushNotificationActivity toggle response", "true");
                try{
                    int statusCode = response.getInt("statusCode");
                    boolean isSuccess = response.getBoolean("success");
                    Log.v("PushNotificationActivity toggle response statusCode", Integer.toString(statusCode));
                    Log.v("PushNotificationActivity toggle response isSuccess", Boolean.toString(isSuccess));
                    volleyCallBack.onSuccess();
                }
                catch(JSONException err){
                    err.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        AppHelper.requestQueue.add(jsonObjectRequest);
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