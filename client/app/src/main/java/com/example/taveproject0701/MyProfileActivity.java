package com.example.tave0915;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.tave0915.URLs.url_my_welfare;

public class MyProfileActivity extends AppCompatActivity {

    private WelfareViewAdapter welfareViewAdapter;
    private ArrayList<WelfareInfoComponent> welfareInfoComponentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Bundle bundle = (Bundle) getIntent().getExtras();
        String token = bundle.getString("token");
        Log.v("MyProfile bundle token", token);

        RecyclerView welfareRVList = (RecyclerView)findViewById(R.id.RV_welfare_list);
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

        Button btn_edit = (Button)findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, ProfileActivity.class);
                if(bundle !=null){
                    Log.v("bundle not empty", "loc : MyProfileActivity");
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_4);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_1:
                        Intent intent1 = new Intent(MyProfileActivity.this, MainActivity.class);
                        startActivity(intent1);
                        finish();
                        return true;
                    case R.id.navigation_2:
                        Intent intent2 = new Intent(MyProfileActivity.this, ChatActivity.class);
                        startActivity(intent2);
                        finish();
                        return true;
                    case R.id.navigation_3:
                        Intent intent3 = new Intent(MyProfileActivity.this, MapActivity.class);
                        startActivity(intent3);
                        finish();
                        return true;
                    case R.id.navigation_4:
                        return true;
                }
                return false;
            }
        });

    }

    private void getWelfareInfo(String token){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            Log.v("MyProfile params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }

        SharedPreferences welfareInfoResponse = getSharedPreferences("chatResponse", MODE_PRIVATE);
        SharedPreferences.Editor editor= welfareInfoResponse.edit();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_my_welfare, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("MyProfileActivity welfare response", "success");
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    String responseToken = response.getString("token");
                    JSONArray jar = response.getJSONArray("dibs_welfare_list");

                    Log.v("MyProfile response isSuccess", isSuccess.toString());
                    Log.v("MyProfile response statusCode", Integer.toString(statusCode));

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);

                    /* jar 로부터 복지정보 parsing */
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

        Log.v("MyProfile jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("MyProfile jsonObjectRequest url", jsonObjectRequest.getUrl());

        if(welfareInfoResponse.getBoolean("success", false)){
            welfareInfoComponentArrayList.add(new WelfareInfoComponent(
                    welfareInfoResponse.getInt("welfare_id",0),
                    welfareInfoResponse.getString("title", ""),
                    welfareInfoResponse.getString("summary", ""),
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