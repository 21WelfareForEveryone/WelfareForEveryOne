package com.example.tave0915;

import static com.example.tave0915.URLs.url_my_welfare;
import static com.example.tave0915.URLs.url_welfare_search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

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
    private ArrayList<WelfareInfoComponent> welfareInfoComponentArrayList;
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
            //params.put("token", token); // 따로 token 값이 필요 없다
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
                    String responseToken = response.getString("token");
                    JSONArray jar = response.getJSONArray("welfare_list");

                    Log.v("ListActivity response isSuccess", isSuccess.toString());
                    Log.v("ListActivity response statusCode", Integer.toString(statusCode));
                    Log.v("ListActivity number of welfare_list", Integer.toString(jar.length()));

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);

                    if(jar.length() != 0){
                        // test : index 0 information loaded
                        int welfare_id = jar.getJSONObject(0).getInt("welfare_id");
                        String title = jar.getJSONObject(0).getString("title");
                        String summary = jar.getJSONObject(0).getString("summary");

                        editor.putInt("welfare_id", welfare_id);
                        editor.putString("title", title);
                        editor.putString("summary", summary);
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

        if(categoricalWelfareInfo.getBoolean("success",false)){
            welfareInfoComponentArrayList.add(new WelfareInfoComponent(
                    categoricalWelfareInfo.getInt("welfare_id",0),
                    categoricalWelfareInfo.getString("title", ""),
                    categoricalWelfareInfo.getString("summary", ""),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0
            ));
            welfareViewAdapter.notifyDataSetChanged();
        }
        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    };
}