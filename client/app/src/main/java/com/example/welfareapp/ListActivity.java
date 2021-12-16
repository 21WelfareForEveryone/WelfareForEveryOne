package com.example.welfareapp;

import static com.example.welfareapp.URLs.url_welfare_search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private RecyclerView recyclerView;
    private ArrayList<com.example.welfareapp.WelfareInfoComponent> welfareInfoComponentArrayList;
    private WelfareViewAdapter welfareViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");
        int category = bundle.getInt("category");

        // recycler view adapter
        RecyclerView recyclerView = findViewById(R.id.list_container) ;
        welfareInfoComponentArrayList = new ArrayList<>();
        welfareViewAdapter = new WelfareViewAdapter(welfareInfoComponentArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(welfareViewAdapter) ;

        // load data from recycler view
        try{
            getCategoricalWelfareInfo(token, category);
            Log.v("ListActivity load welfareInfo process","success");
        }
        catch(Exception err){
            Log.v("ListActivity load welfareInfo process","failed");
        }

        // btn_back button listener
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }

    // 해당 정보를 호출하는 dataFetch 함수
    private void getCategoricalWelfareInfo(String token, int category){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            params.put("welfare_category", category);
            Log.v("ListActivity params complete", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }

        SharedPreferences categoricalWelfareInfo = getSharedPreferences("categoricalWelfareInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor= categoricalWelfareInfo.edit();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_welfare_search, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("ListActivity welfare  response", "success");
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    JSONArray jar = response.getJSONArray("welfare_list");

                    Log.v("ListActivity response isSuccess", isSuccess.toString());
                    Log.v("ListActivity response statusCode", Integer.toString(statusCode));
                    Log.v("ListActivity number of welfare_list", Integer.toString(jar.length()));

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);
                    editor.putInt("totalNum", jar.length());

                    if(jar.length() > 0){
                        for(int i = 0 ; i < jar.length() ; i++){
                            int welfare_id = jar.getJSONObject(i).getInt("welfare_id");
                            String title = jar.getJSONObject(i).getString("title");
                            String summary = jar.getJSONObject(i).getString("summary");
                            String who = jar.getJSONObject(i).getString("who");
                            String criteria = jar.getJSONObject(i).getString("criteria");
                            String what = jar.getJSONObject(i).getString("what");
                            String how = jar.getJSONObject(i).getString("how");
                            String info_calls = jar.getJSONObject(i).getString("calls");
                            String sites  = jar.getJSONObject(i).getString("sites");
                            Boolean isLiked = jar.getJSONObject(i).getBoolean("isLiked");

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
                                Log.v("ListActivity json array", a.toString());
                            } else {
                                editor.putString(key, null);
                            }
                        }
                        editor.commit();
                    }
                    else{
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
                Log.v("ListActivity welfare response", "failed");
                editor.putBoolean("success", false);
                editor.commit();
            }
        });

        Log.v("ListActivity jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("ListActivity jsonObjectRequest url", jsonObjectRequest.getUrl());

        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(categoricalWelfareInfo.getBoolean("success",false)){
                            Log.v("ListActivity categoricalWelfareInfo loaded", "true");
                            Log.v("ListActivity categoricalWelfareInfo num of info", Integer.toString(categoricalWelfareInfo.getInt("totalNum",0)));
                            for(int i = 0; i < categoricalWelfareInfo.getInt("totalNum",0); i++){
                                String key = "welfare_info_" + Integer.toString(i);
                                String json = categoricalWelfareInfo.getString(key, null);
                                Log.v("ListActivity JSON string type loaded", json.toString());
                                ArrayList<String> decode_list  = new ArrayList<String>();
                                if (json != null) {
                                    try {
                                        JSONArray a = new JSONArray(json);
                                        for (int j = 0; j < a.length(); j++) {
                                            String str = a.optString(j);
                                            Log.v("ListActivity JSON string parsing", str);
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
                                                category,
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
                                                category,
                                                token,
                                                isLiked
                                        ));
                                    }
                                }
                                catch(Exception error){
                                    error.printStackTrace();
                                    Log.v("ListActivity welfare isLiked loaded","failed");
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
                                            category,
                                            token,
                                            isLiked
                                    ));
                                }
                                welfareViewAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                },
                1024
        );
    };
}