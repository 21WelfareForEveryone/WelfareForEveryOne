package com.example.welfareapp;

import static com.example.welfareapp.URLs.url_welfare_read;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        Bundle bundle = getIntent().getExtras();
        int welfare_id = bundle.getInt("welfare_id",0);
        String token = bundle.getString("token","");
        Boolean is_liked = bundle.getBoolean("is_liked", false);

        try{
            getDetailInfo(welfare_id, token);
            Log.v("DetailActivity bundle data to text view","start");
        }
        catch(Exception err){
            err.printStackTrace();
            Log.v("DetailActivity bundle data to text view","error");
        }

        // 뒤로 가기 버튼 동작 구현
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed(); // 뒤로 가기 기능
            }
        });

        // 신청하기 버튼 동작 구현
        Button btn_sign = (Button)findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_sites =  (TextView) findViewById(R.id.tv_sites);
                String url = tv_sites.getText().toString();
                bundle.putString("url",url);
                Intent intent = new Intent(DetailActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });

        // 찜 버튼 구현
        // setChecked(false)를 이후에 외부에서 isFavorite 형태의 boolean 변수로 받아올 것
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggle_favorite);

        SharedPreferences detailInfo = getSharedPreferences("detailInfo", MODE_PRIVATE);
        toggleButton.setChecked(is_liked);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    JSONObject params = new JSONObject();
                    try{
                        params.put("token", token);
                        params.put("welfare_id", welfare_id);
                    }
                    catch(JSONException err)
                    {
                        err.printStackTrace();
                        return;
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.url_favorite_create, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                Boolean isSuccess = response.getBoolean("success");
                                int statusCode = response.getInt("statusCode");
                                Log.v("DetailActivity welfareInfo create favorite info", "true");
                                Log.v("DetailActivity welfareInfo create favorite, isSuccess", isSuccess.toString());
                                Log.v("DetailActivity welfareInfo create favorite, statusCode", Integer.toString(statusCode));
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
                    Toast.makeText(buttonView.getContext(), "내 관심복지 리스트에 등록되었습니다.", Toast.LENGTH_LONG).show();
                    VolleySingleton.getInstance(buttonView.getContext()).addToRequestQueue(jsonObjectRequest);
                }
                else{
                    JSONObject params = new JSONObject();
                    try{
                        params.put("token", token);
                        params.put("welfare_id", welfare_id);
                    }
                    catch(JSONException err)
                    {
                        err.printStackTrace();
                        return;
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.url_favorite_delete, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                Boolean isSuccess = response.getBoolean("success");
                                int statusCode = response.getInt("statusCode");
                                Log.v("DetailActivity welfareInfo delete favorite info", "true");
                                Log.v("DetailActivity welfareInfo delete favorite, isSuccess", isSuccess.toString());
                                Log.v("DetailActivity welfareInfo delete favorite, statusCode", Integer.toString(statusCode));
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
                    Toast.makeText(buttonView.getContext(), "내 관심복지 리스트에서 삭제되었습니다.", Toast.LENGTH_LONG).show();
                    VolleySingleton.getInstance(buttonView.getContext()).addToRequestQueue(jsonObjectRequest);
                }
            }
        });

    }

    private void getDetailInfo(int welfare_id, String token){
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            params.put("welfare_id", welfare_id);
            Log.v("DetailActivity getDetailInfo  params complete", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }

        SharedPreferences detailInfo = getSharedPreferences("detailInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = detailInfo.edit();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_welfare_read, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("DetailActivity welfare response", "success");
                try{
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    int welfare_id = response.getInt("welfare_id");
                    String title = response.getString("title");
                    String summary = response.getString("summary");
                    String who = response.getString("who");
                    String criteria = response.getString("criteria");
                    String how = response.getString("how");
                    String calls = response.getString("calls");
                    String sites = response.getString("sites");
                    //int like_count = response.getInt("like_count");
                    int category = response.getInt("category");
                    Boolean isLiked = response.getBoolean("isLiked");
                    Log.v("DetailActivity welfare isLiked", isLiked.toString());

                    editor.putBoolean("success", isSuccess);
                    editor.putInt("statusCode", statusCode);
                    editor.putInt("welfare_id", welfare_id);
                    editor.putString("title", title);
                    editor.putString("summary", summary);
                    editor.putString("who", who);
                    editor.putString("criteria", criteria);
                    editor.putString("how", how);
                    editor.putString("calls", calls);
                    editor.putString("sites", sites);
                    editor.putInt("category", category);
                    editor.putBoolean("isLiked", isLiked);
                    editor.commit();
                }
                catch(JSONException err){
                    err.printStackTrace();
                    Log.v("JSONException error", err.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("DetailActivity welfare response", "failed");
                Log.v("DetailActivity welfare response error", error.toString());
                editor.putBoolean("success", false);
                editor.commit();
            }
        });

        Log.v("DetailActivity jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("DetailActivity jsonObjectRequest url", jsonObjectRequest.getUrl());

        jsonObjectRequest.setShouldCache(false);
        //VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        AppHelper.requestQueue.add(jsonObjectRequest);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(detailInfo.getBoolean("success", false)){
                            TextView tv_title = (TextView) findViewById(R.id.tv_title);
                            TextView tv_detail = (TextView) findViewById(R.id.tv_detail);
                            TextView tv_who = (TextView) findViewById(R.id.tv_who);
                            TextView tv_criteria = (TextView) findViewById(R.id.tv_criteria);
                            TextView tv_how =  (TextView) findViewById(R.id.tv_how);
                            TextView tv_calls =  (TextView) findViewById(R.id.tv_calls);
                            TextView tv_sites =  (TextView) findViewById(R.id.tv_sites);

                            tv_title.setText(detailInfo.getString("title",""));
                            tv_detail.setText(detailInfo.getString("summary",""));
                            tv_who.setText(detailInfo.getString("who",""));
                            tv_criteria.setText(detailInfo.getString("criteria",""));
                            tv_how.setText(detailInfo.getString("how",""));
                            tv_calls.setText(detailInfo.getString("calls",""));
                            tv_sites.setText(detailInfo.getString("sites",""));

                            ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggle_favorite);
                            toggleButton.setChecked(detailInfo.getBoolean("isLiked", false));

                            ImageView welfare_info_img = (ImageView)findViewById(R.id.welfare_info_img);

                            // category 별로 이미지 사진 첨부
                            // post 요청시 category 값을 받아온다면 바로 호출 가능..

                            int category_num  = detailInfo.getInt("category",0);
                            Log.v("DetailActivity category_num", Integer.toString(category_num));

                            switch(category_num){
                                case 0:
                                    welfare_info_img.setImageResource(R.drawable.img_category_00);
                                    break;
                                case 1:
                                    welfare_info_img.setImageResource(R.drawable.img_category_01);
                                    break;
                                case 2:
                                    welfare_info_img.setImageResource(R.drawable.img_category_02);
                                    break;
                                case 3:
                                    welfare_info_img.setImageResource(R.drawable.img_category_03);
                                    break;
                                case 4:
                                    welfare_info_img.setImageResource(R.drawable.img_category_04);
                                    break;
                                case 5:
                                    welfare_info_img.setImageResource(R.drawable.img_category_05);
                                    break;
                                case 6:
                                    welfare_info_img.setImageResource(R.drawable.img_category_06);
                                    break;
                                case 7:
                                    welfare_info_img.setImageResource(R.drawable.img_category_07);
                                    break;
                                case 8:
                                    welfare_info_img.setImageResource(R.drawable.img_category_08);
                                    break;
                                case 9:
                                    welfare_info_img.setImageResource(R.drawable.img_category_09);
                                    break;
                                case 10:
                                    welfare_info_img.setImageResource(R.drawable.img_category_10);
                                    break;
                                case 11:
                                    welfare_info_img.setImageResource(R.drawable.img_category_11);
                                    break;
                                case 12:
                                    welfare_info_img.setImageResource(R.drawable.img_category_12);
                                    break;
                                case 13:
                                    welfare_info_img.setImageResource(R.drawable.img_category_13);
                                    break;
                                case 14:
                                    welfare_info_img.setImageResource(R.drawable.img_category_14);
                                    break;
                                case 15:
                                    welfare_info_img.setImageResource(R.drawable.img_category_15);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                },
                1000
        );
    }

    // 여기서 requestQueue 및 나머지 캐시를 삭제해야 함
    // 현재 detail activity 들어갈 경우, 이전 데이터 기록을 참조해서 반영됨
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(AppHelper.requestQueue != null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        clearApplicationData();
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
}