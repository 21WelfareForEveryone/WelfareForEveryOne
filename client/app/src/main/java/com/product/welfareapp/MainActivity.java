package com.product.welfareapp;

import static com.product.welfareapp.URLs.url_welfare_recommend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Category RV list
    private MainRVAdapter mainRVAdapter;
    private ArrayList<MainCategoryCard> categoryList;

    // Recommended Welfare Info RV list
    private WelfareViewAdapter welfareViewAdapter;
    private ArrayList<com.product.welfareapp.WelfareInfoComponent> welfareInfoComponentArrayList;

    // Firebase Push Notification
    private static final String TAG = "MainActivity Firebase Push Token Process";

    // back button listener
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bundle loaded

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");

        Log.v("MainActivity bundle", bundle.toString());

        RecyclerView RV_category = (RecyclerView)findViewById(R.id.RV_category);
        categoryList = new ArrayList<>();

        String[] categoryItems = {"영유아","아동, 청소년", "청년", "중,장년", "노년", "장애인", "한부모", "다문화"
                ,"저소득층", "교육", "고용", "주거", "건강","서민금융","문화","임신/출산"};

        for(int i = 0; i<16; i++){
            //String categoryName = "카테고리 " + Integer.toString(i+1);
            String categoryName = categoryItems[i];
            MainCategoryCard categoryCard = new MainCategoryCard(categoryName, i);
            categoryList.add(categoryCard);
        }

        mainRVAdapter = new MainRVAdapter(categoryList, getApplicationContext(), token);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV_category.setLayoutManager(linearLayoutManager);
        RV_category.setAdapter(mainRVAdapter);

        // Recommended welfare Info data loaded
        RecyclerView welfareRecommendedRV = (RecyclerView)findViewById(R.id.welfare_recommended_RV);
        welfareInfoComponentArrayList = new ArrayList<>();

        welfareViewAdapter = new WelfareViewAdapter(welfareInfoComponentArrayList);
        LinearLayoutManager welfareLinearLayoutManger = new LinearLayoutManager(this);

        welfareRecommendedRV.setLayoutManager(welfareLinearLayoutManger);
        welfareRecommendedRV.setAdapter(welfareViewAdapter);

        try{
           // getRecommendWelfareInfo(token);
            requestRecommendWelfareInfo(token, new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    updateRecommendWelfareInfoOnUi(token);
                }
            });
            Log.v("MainActivity recommended welfare info load process","pass");
        }
        catch(Exception err){
            Log.v("MainActivity recommended welfare info load process","failed");
        }

        // 추천복지 리스트 더보기 기능
        Button btn_view_more = (Button)findViewById(R.id.btn_view_more);
        btn_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainViewAllActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                // finish();
            }
        });

        // navigation onClickListener
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_1);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_1:
                        return true;
                    case R.id.navigation_2:
                        Intent intent2 = new Intent(MainActivity.this, ChatActivity.class);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        finish();
                        return true;
                    case R.id.navigation_3:
                        Intent intent3 = new Intent(MainActivity.this, com.product.welfareapp.MapActivity.class);
                        intent3.putExtras(bundle);
                        startActivity(intent3);
                        finish();
                        return true;
                    case R.id.navigation_4:
                        Intent intent4 = new Intent(MainActivity.this, MyProfileActivity.class);
                        intent4.putExtras(bundle);
                        startActivity(intent4);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    public interface VolleyCallBack {
        void onSuccess();
    }

    private synchronized void requestRecommendWelfareInfo(String token, final VolleyCallBack volleyCallBack){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
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
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    String responseToken = response.getString("token");
                    JSONArray jar = response.getJSONArray("recommend_welfare_list");

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);
                    editor.putInt("totalNum", jar.length());

                    if(jar.length() > 0){
                        for(int i = 0; i < jar.length();i++){

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
                                Log.v("MainActivity json array", a.toString());
                            } else {
                                editor.putString(key, null);
                            }
                        }
                        editor.commit();
                        volleyCallBack.onSuccess();
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                editor.putBoolean("success", false);
                editor.commit();
            }
        });
        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void updateRecommendWelfareInfoOnUi(String token){
        SharedPreferences recommendWelfareInfo = getSharedPreferences("recommendWelfareInfo", MODE_PRIVATE);
        if(recommendWelfareInfo.getBoolean("success",false)){

            String text = "총 "+ Integer.toString(recommendWelfareInfo.getInt("totalNum",0)) + "개의 복지가 있습니다.";
            TextView tv_num_list = (TextView)findViewById(R.id.sub_title);
            tv_num_list.setText(text);

            int totalNum =  recommendWelfareInfo.getInt("totalNum", 0);

            if(totalNum >= 2){
                totalNum = 2;
            }

            for(int i = 0; i < totalNum; i++){

                String key = "welfare_info_" + Integer.toString(i);
                String json = recommendWelfareInfo.getString(key, null);
                ArrayList<String> decode_list  = new ArrayList<String>();
                if (json != null) {
                    try {
                        JSONArray a = new JSONArray(json);
                        for (int j = 0; j < a.length(); j++) {
                            String str = a.optString(j);
                            Log.v("MainActivity JSON string parsing", str);
                            decode_list.add(str);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try{

                    if(Integer.parseInt(decode_list.get(10)) == 1){
                        Boolean isLiked = true;
                        welfareInfoComponentArrayList.add(new com.product.welfareapp.WelfareInfoComponent(
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
                        welfareInfoComponentArrayList.add(new com.product.welfareapp.WelfareInfoComponent(
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
                    welfareInfoComponentArrayList.add(new com.product.welfareapp.WelfareInfoComponent(
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
                            false
                    ));
                }
                welfareViewAdapter.notifyDataSetChanged();
            }
        }
        else{
            String text = "총 0개의 복지가 있습니다.";
            TextView tv_num_list = (TextView)findViewById(R.id.sub_title);
            tv_num_list.setText(text);
        }
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