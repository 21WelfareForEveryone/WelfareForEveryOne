package com.example.welfareapp;

import static com.example.welfareapp.URLs.url_my_welfare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyProfileListActivity extends AppCompatActivity {

    private WelfareViewAdapter welfareViewAdapter;
    private ArrayList<com.example.welfareapp.WelfareInfoComponent> welfareInfoComponentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_list);

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");
        Log.v("MyProfileList bundle token", token);

        RecyclerView welfareRVList = (RecyclerView)findViewById(R.id.list_container);
        welfareInfoComponentArrayList = new ArrayList<>();
        welfareViewAdapter = new WelfareViewAdapter(welfareInfoComponentArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        welfareRVList.setLayoutManager(linearLayoutManager);
        welfareRVList.setAdapter(welfareViewAdapter);

        /* data loaded from server */
        try{
            getWelfareInfo(token);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileListActivity.this, MyProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getWelfareInfo(String token){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            Log.v("MyProfileList params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }

        SharedPreferences welfareInfoResponse = getSharedPreferences("MyProfileListWelfareResponse", MODE_PRIVATE);
        SharedPreferences.Editor editor= welfareInfoResponse.edit();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_my_welfare, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("MyProfileListActivity welfare response", "success");
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    String responseToken = response.getString("token");
                    JSONArray jar = response.getJSONArray("recommend_welfare_list");

                    Log.v("MyProfileList response isSuccess", isSuccess.toString());
                    Log.v("MyProfileList response statusCode", Integer.toString(statusCode));
                    Log.v("MyProfileList response jar length", Integer.toString(jar.length()));

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);
                    editor.putInt("totalNum", jar.length());


                    /* jar 로부터 복지정보 parsing */
                    if(jar.length() > 0){
                        for(int i = 0 ; i < jar.length() ; i++){
                            Log.v("MyProfileList for loop start",Integer.toString(i));
                            Log.v("MyProfileList jar", jar.toString());
                            Log.v("MyProfileList jar JSONObject", jar.getJSONObject(i).toString());
                            Log.v("MyProfileList jar obj", jar.get(i).toString());

                            int welfare_id = jar.getJSONObject(i).getInt("welfare_id");
                            String title = jar.getJSONObject(i).getString("title");
                            String summary = jar.getJSONObject(i).getString("summary");
                            String who = jar.getJSONObject(i).getString("who");
                            String criteria = jar.getJSONObject(i).getString("criteria");
                            String what = jar.getJSONObject(i).getString("what");
                            String how = jar.getJSONObject(i).getString("how");
                            String info_calls = jar.getJSONObject(i).getString("calls");
                            String sites  = jar.getJSONObject(i).getString("sites");
                            int category = jar.getJSONObject(i).getInt("category");
                            //Boolean isLiked = jar.getJSONObject(i).getBoolean("isLiked");

                            Log.v("MyProfileListActivity welfare_id", Integer.toString(welfare_id));

                            String key = "welfare_info_" + Integer.toString(i);
                            ArrayList<String> list = new ArrayList<String>();
                            list.add(Integer.toString(welfare_id));
                            list.add(title);
                            list.add(summary);
                            list.add(who);
                            list.add(criteria);
                            list.add(what);
                            list.add(how);
                            list.add(info_calls);
                            list.add(sites);
                            list.add(Integer.toString(category));
                            //list.add(Boolean.toString(isLiked));

                            JSONArray a = new JSONArray();
                            for (int j = 0; j < list.size(); j++) {
                                a.put(list.get(j));
                            }
                            if (!list.isEmpty()) {
                                editor.putString(key, a.toString());
                                Log.v("MyProfileListActivity json array", a.toString());
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
                Log.v("MyProfileListActivity welfare response", "failed");
                editor.putBoolean("success", false);
                editor.commit();
            }
        });

        Log.v("MyProfileList jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("MyProfileList jsonObjectRequest url", jsonObjectRequest.getUrl());

        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(welfareInfoResponse.getBoolean("success", false)){

                            for(int i = 0; i < welfareInfoResponse.getInt("totalNum", 0); i++){

                                String key = "welfare_info_" + Integer.toString(i);
                                String json = welfareInfoResponse.getString(key, null);
                                Log.v("MyProfileList JSON string type loaded", json.toString());
                                ArrayList<String> decode_list  = new ArrayList<String>();
                                if (json != null) {
                                    try {
                                        JSONArray a = new JSONArray(json);
                                        for (int j = 0; j < a.length(); j++) {
                                            String str = a.optString(j);
                                            Log.v("MyProfileList JSON string parsing", str);
                                            decode_list.add(str);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                int category = Integer.parseInt(decode_list.get(9));
                                welfareInfoComponentArrayList.add(new com.example.welfareapp.WelfareInfoComponent(
                                        Integer.parseInt(decode_list.get(0)),
                                        decode_list.get(1),
                                        decode_list.get(2),
                                        decode_list.get(3),
                                        decode_list.get(4),
                                        decode_list.get(5),
                                        decode_list.get(6),
                                        decode_list.get(7),
                                        decode_list.get(8),
                                        category,
                                        token,
                                        true
                                ));
                                welfareViewAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                },
                1024
        );
    };
}