package com.example.welfareapp;

import static com.example.welfareapp.URLs.url_my_welfare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity {

    private WelfareViewAdapter welfareViewAdapter;
    private ArrayList<com.example.welfareapp.WelfareInfoComponent> welfareInfoComponentArrayList;

    private static long back_pressed;
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
            getUserName(token);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        // edit button onClickListener : enter profile activity
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

        // 관심복지 리스트 전체 보기 실행
        Button btn_view_more = (Button)findViewById(R.id.btn_view_more);
        btn_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, MyProfileListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        // logout button onClickListener
        ImageButton btn_logout = (ImageButton)findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, LoginActivity.class);
                bundle.clear();
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                clearApplicationData();
                finish();
            }
        });

        // push button onClickListener
        ImageButton btn_push = (ImageButton)findViewById(R.id.btn_push);
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "푸시 알림 기능 ON(아직 개발중입니다..)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyProfileActivity.this, PushActivity.class);
                intent.putExtras(bundle);
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
                        Intent intent1 = new Intent(MyProfileActivity.this, com.example.welfareapp.MainActivity.class);
                        intent1.putExtras(bundle);
                        startActivity(intent1);
                        finish();
                        return true;
                    case R.id.navigation_2:
                        Intent intent2 = new Intent(MyProfileActivity.this, com.example.welfareapp.ChatActivity.class);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        finish();
                        return true;
                    case R.id.navigation_3:
                        Intent intent3 = new Intent(MyProfileActivity.this, com.example.welfareapp.MapActivity.class);
                        intent3.putExtras(bundle);
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

        SharedPreferences welfareInfoResponse = getSharedPreferences("myProfileWelfareResponse", MODE_PRIVATE);
        SharedPreferences.Editor editor= welfareInfoResponse.edit();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_my_welfare, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("MyProfileActivity welfare response", "success");
                Log.v("MyProfileActivity response",response.toString());
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    String responseToken = response.getString("token");
                    JSONArray jar = response.getJSONArray("recommend_welfare_list");


                    Log.v("MyProfile response isSuccess", isSuccess.toString());
                    Log.v("MyProfile response statusCode", Integer.toString(statusCode));
                    Log.v("MyProfile response jar length", Integer.toString(jar.length()));
                    Log.v("MyProfile response",response.toString());

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);
                    editor.putInt("totalNum", jar.length());


                    /* jar 로부터 복지정보 parsing */
                    if(jar.length() > 0){
                        for(int i = 0 ; i < jar.length() ; i++){
                            Log.v("MyProfile for loop start",Integer.toString(i));
                            Log.v("MyProfile jar", jar.toString());
                            Log.v("MyProfile jar JSONObject", jar.getJSONObject(i).toString());
                            Log.v("MyProfile jar obj", jar.get(i).toString());

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

                            Log.v("MyProfileActivity welfare_id", Integer.toString(welfare_id));

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
                                Log.v("MyProfileActivity json array", a.toString());
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

        Log.v("MyProfile jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("MyProfile jsonObjectRequest url", jsonObjectRequest.getUrl());
        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(welfareInfoResponse.getBoolean("success", false)){

                            TextView tv_num_list = (TextView)findViewById(R.id.text_num_list);
                            String text = "총 " + Integer.toString(welfareInfoResponse.getInt("totalNum",0)) + "개의 복지가 있습니다.";
                            tv_num_list.setText(text);

                            int totalNum = welfareInfoResponse.getInt("totalNum", 0);

                            if(totalNum < 2){

                            }
                            else if(totalNum >= 2){
                                totalNum = 2;
                            }

                            for(int i = 0; i < totalNum; i++){

                                String key = "welfare_info_" + Integer.toString(i);
                                String json = welfareInfoResponse.getString(key, null);
                                Log.v("MyProfile JSON string type loaded", json.toString());
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

                                int category = Integer.parseInt(decode_list.get(9));
                                welfareInfoComponentArrayList.add(new WelfareInfoComponent(
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
                                        //Boolean.parseBoolean(decode_list.get(10))
                                ));
                                welfareViewAdapter.notifyDataSetChanged();
                            }
                        }
                        else{
                            String text = "총 0개의 복지가 있습니다.";
                            TextView tv_num_list = (TextView)findViewById(R.id.text_num_list);
                            tv_num_list.setText(text);
                        }
                    }
                },
                1024
        );
    };

    private void getUserName(String token){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            Log.v("MyProfile params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.url_read, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("MyProfileActivity user name loaded", "true");
                try{
                    String username = response.getString("user_name");
                    String text = username + "님";
                    TextView tv_user_name = (TextView)findViewById(R.id.tv_user_name);
                    tv_user_name.setText(text);
                }
                catch(JSONException err){
                    err.printStackTrace();
                    Log.v("MyProfileActivity user name load process", "error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("MyProfileActivity onErrorResponse", "error");
            }
        });

        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
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