package com.example.welfareapp;

import static com.example.welfareapp.URLs.url_my_welfare;
import static com.example.welfareapp.URLs.url_push_getInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PushActivity extends AppCompatActivity {

    private PushViewAdapter pushViewAdapter;
    private ArrayList<PushNotificationComponent> pushNotificationComponentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");

        // Recommended welfare Info data loaded
        RecyclerView pushWelfareRV = (RecyclerView)findViewById(R.id.push_container);
        pushNotificationComponentArrayList = new ArrayList<>();

        pushViewAdapter = new PushViewAdapter(pushNotificationComponentArrayList);
        LinearLayoutManager pushLinearLayoutManger = new LinearLayoutManager(this);

        pushWelfareRV.setLayoutManager(pushLinearLayoutManger);
        pushWelfareRV.setAdapter(pushViewAdapter);

        try{
            getPushNotification(token);
            Log.v("PushActivity load process","pass");
        }
        catch(Exception err){
            Log.v("PushActivity load process","failed");
        }

        // btn_back button listener
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PushActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    public void getPushNotification(String token){

        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            Log.v("PushActivity params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }

        SharedPreferences pushResponse = getSharedPreferences("pushResponse", MODE_PRIVATE);
        SharedPreferences.Editor editor= pushResponse.edit();

        // url1 : url_my_welfare
        // url2 : url_push_getInfo

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_push_getInfo, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("PushActivity welfare loading  response", "success");
                try{
                    Log.v("PushActivity response", response.toString());
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    String responseToken = response.getString("token");

                    //JSONArray jar = response.getJSONArray("dibs_welfare_list");
                    JSONArray jar = response.getJSONArray("welfare_list");
                    Log.v("PushActivity response isSuccess", isSuccess.toString());
                    Log.v("PushActivity response statusCode", Integer.toString(statusCode));
                    Log.v("PushActivity response jar length", Integer.toString(jar.length()));

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);
                    editor.putInt("totalNum", jar.length());

                    if(jar.length() > 0){
                        for(int i = 0 ; i < jar.length() ; i++){

                            int welfare_id = jar.getJSONObject(i).getInt("welfare_id");
                            String title = jar.getJSONObject(i).getString("title");
                            Log.v("PushActivity JSONObject", jar.getJSONObject(i).toString());
                            int d_day = jar.getJSONObject(i).getInt("d_day");
                            String summary = "기한까지 " + Integer.toString(d_day) + "일 남았습니다.";
                            String notice_day = "D-" + Integer.toString(d_day);

                            Log.v("PushActivity welfare_id", Integer.toString(welfare_id));

                            String key = "welfare_info_" + Integer.toString(i);
                            ArrayList<String> list = new ArrayList<String>();
                            list.add(Integer.toString(welfare_id));
                            list.add(title);
                            list.add(summary);
                            list.add(notice_day);

                            JSONArray a = new JSONArray();
                            for (int j = 0; j < list.size(); j++) {
                                a.put(list.get(j));
                            }
                            if (!list.isEmpty()) {
                                editor.putString(key, a.toString());
                                Log.v("PushActivity json array", a.toString());
                            } else {
                                editor.putString(key, null);
                            }
                        }
                        editor.commit();
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("MyProfileActivity welfare response", "failed");
                editor.putBoolean("success", false);
                editor.commit();
            }
        });

        Log.v("Push jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("Push jsonObjectRequest url", jsonObjectRequest.getUrl());

        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(pushResponse.getBoolean("success", false)){

                            for(int i = 0; i < pushResponse.getInt("totalNum", 0); i++){

                                String key = "welfare_info_" + Integer.toString(i);
                                String json = pushResponse.getString(key, null);
                                Log.v("PushActivity JSON string type loaded", json.toString());
                                ArrayList<String> decode_list  = new ArrayList<String>();
                                if (json != null) {
                                    try {
                                        JSONArray a = new JSONArray(json);
                                        for (int j = 0; j < a.length(); j++) {
                                            String str = a.optString(j);
                                            Log.v("PushActivity JSON string parsing", str);
                                            decode_list.add(str);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                pushNotificationComponentArrayList.add(new PushNotificationComponent(
                                        Integer.parseInt(decode_list.get(0)),
                                        decode_list.get(1),
                                        decode_list.get(2),
                                        decode_list.get(3),
                                        token
                                ));
                                pushViewAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }, 1024 * 2
        );

    }
}