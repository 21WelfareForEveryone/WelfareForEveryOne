package com.example.welfareapp;

import static com.example.welfareapp.URLs.url_welfare_recommend;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainViewAllActivity extends AppCompatActivity {

    // Recommended Welfare Info RV list
    private WelfareViewAdapter welfareViewAdapter;
    private ArrayList<WelfareInfoComponent> welfareInfoComponentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view_all);
        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");

        // Recommended welfare Info data loaded
        RecyclerView welfareRecommendedRV = (RecyclerView)findViewById(R.id.list_container);
        welfareInfoComponentArrayList = new ArrayList<>();

        welfareViewAdapter = new WelfareViewAdapter(welfareInfoComponentArrayList);
        LinearLayoutManager welfareLinearLayoutManger = new LinearLayoutManager(this);

        welfareRecommendedRV.setLayoutManager(welfareLinearLayoutManger);
        welfareRecommendedRV.setAdapter(welfareViewAdapter);

        try{
            getRecommendWelfareInfo(token);
            Log.v("MainViewAllActivity recommended welfare info load process","success");
        }
        catch(Exception err){
            Log.v("MainViewAllActivity recommended welfare info load process","failed");
        }


        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainViewAllActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getRecommendWelfareInfo(String token){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            Log.v("MainViewAllActivity getRecommendWelfareInfo  params complete", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }
        SharedPreferences recommendWelfareInfo = getSharedPreferences("recommendWelfareInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = recommendWelfareInfo.edit();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_welfare_recommend, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("MainActivity welfare  response", "success");
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    String responseToken = response.getString("token");
                    JSONArray jar = response.getJSONArray("recommend_welfare_list");

                    Log.v("MainViewAllActivity response isSuccess", isSuccess.toString());
                    Log.v("MainViewAllActivity response statusCode", Integer.toString(statusCode));
                    Log.v("MainViewAllActivity number of welfare_list", Integer.toString(jar.length()));
                    Log.v("MainViewAllActivity jar", jar.toString());

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);
                    editor.putInt("totalNum", jar.length());

                    if(jar.length() > 0){
                        for(int i = 0; i < jar.length();i++){
                            Log.v("MainViewAllActivity for loop start",Integer.toString(i));
                            Log.v("MainViewAllActivity jar", jar.toString());
                            Log.v("MainViewAllActivity jar JSONObject", jar.getJSONObject(i).toString());
                            Log.v("MainViewAllActivity jar obj", jar.get(i).toString());

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
                            Boolean isLiked = jar.getJSONObject(i).getBoolean("isLiked");

                            Log.v("MainViewAllActivity welfare_id", Integer.toString(welfare_id));

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

                            if(isLiked){
                                list.add(Integer.toString(1));
                            }
                            else{
                                list.add(Integer.toString(0));
                            }

                            JSONArray a = new JSONArray();
                            for (int j = 0; j < list.size(); j++) {
                                a.put(list.get(j));
                            }
                            if (!list.isEmpty()) {
                                editor.putString(key, a.toString());
                                Log.v("MainViewAllActivity json array", a.toString());
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
                Log.v("MainViewAllActivity welfare response", "failed");
                editor.putBoolean("success", false);
                editor.commit();
            }
        });

        Log.v("MainViewAllActivity jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("MainViewAllActivity jsonObjectRequest url", jsonObjectRequest.getUrl());
        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(recommendWelfareInfo.getBoolean("success",false)){

                            for(int i = 0; i < recommendWelfareInfo.getInt("totalNum", 0); i++){

                                String key = "welfare_info_" + Integer.toString(i);
                                String json = recommendWelfareInfo.getString(key, null);
                                Log.v("MainViewAllActivity JSON string type loaded", json.toString());
                                ArrayList<String> decode_list  = new ArrayList<String>();
                                if (json != null) {
                                    try {
                                        JSONArray a = new JSONArray(json);
                                        for (int j = 0; j < a.length(); j++) {
                                            String str = a.optString(j);
                                            Log.v("MyProfile JSON string parsing", str);
                                            decode_list.add(str);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                try{
                                    if(Integer.parseInt(decode_list.get(10)) == 1){
                                        Boolean isLiked = true;
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
                                                Integer.parseInt(decode_list.get(9)),
                                                token,
                                                isLiked
                                        ));
                                    }
                                    else{
                                        Boolean isLiked = false;
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
                                                Integer.parseInt(decode_list.get(9)),
                                                token,
                                                isLiked
                                        ));
                                    }
                                }
                                catch(Exception error){
                                    error.printStackTrace();
                                    Log.v("MainViewAllActivity welfare isLiked loaded","failed");
                                    Boolean isLiked = false;
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
                                            Integer.parseInt(decode_list.get(9)),
                                            token,
                                            isLiked
                                    ));
                                }
                                welfareViewAdapter.notifyDataSetChanged();
                            }
                        }
                        else{
                        }

                    }
                },
                1024
        );


    }

}