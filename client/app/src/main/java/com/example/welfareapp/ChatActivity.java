package com.example.welfareapp;

import static android.speech.tts.TextToSpeech.ERROR;
import static com.example.welfareapp.URLs.url_chatbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AsyncRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChatActivity extends AppCompatActivity {

    private ChatRVAdapter chatRVAdapter;
    private ArrayList<ChatModel>chatModelArrayList;

    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private final String BOT_INFO_KEY = "bot-welfare-info";
    private final String BOT_WAITING_KEY = "bot-wait-response";

    // tts 기능 추가
    private TextToSpeech tts;

    // back button listener
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");
        Log.v("chatbot bundle token", token);

        // keyboard disappeared
        EditText et_message = (EditText)findViewById(R.id.et_message);
        et_message.setShowSoftInputOnFocus(true);

        //RecyclerView Event Listener
        RecyclerView chatRVList = (RecyclerView)findViewById(R.id.chatList);
        chatModelArrayList = new ArrayList<>();
        chatRVAdapter =  new ChatRVAdapter(chatModelArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatRVList.setLayoutManager(manager);
        chatRVList.setAdapter(chatRVAdapter);


        // chatbot default response 추가
        String initMessage = "안녕하세요, 복덩이입니다.\n" +
                "오늘 기분은 어떠신가요?\n" +
                "“나한테 맞는 복지 추천해줘”를 입력하시면 알맞은 복지를 추천해 드립니다.\n" +
                "관심 있는 복지 정보를 알고 싶다면 관련 “아동”과 같이 키워드 또는 “아이를 돌봐줄 곳이 필요해”와 같이 문장을 적어주시면 가장 유사한 복지를 추천해 드립니다.";

        chatModelArrayList.add(new ChatModel(initMessage, BOT_KEY, null, null, 0, token));
        chatRVAdapter.notifyDataSetChanged();

        // push button event listener
        Button btn_transfer = (Button)findViewById(R.id.btn_transfer);
        btn_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_message.getText().toString().isEmpty()){
                    Toast.makeText(ChatActivity.this, "메세지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    //getResponse(et_message.getText().toString(), token);
                    //et_message.setText("");

                    // method2 synchronized
                    DialogResponse dialogResponse = new DialogResponse();
                    dialogResponse.request(et_message.getText().toString(), token, new VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            dialogResponse.response(et_message.getText().toString(), token);
                        }
                    });
                }
            }
        });

        // header btn_back onCLickListener
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, com.example.welfareapp.MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        // tts initiate
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        // btn_TTS onClickListener
        ImageButton btn_TTS = (ImageButton)findViewById(R.id.btn_TTS);
        btn_TTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "챗봇 메세지를 음성으로 변환합니다", Toast.LENGTH_SHORT).show();

                try{
                    int pos = chatModelArrayList.size();
                    String msg = chatModelArrayList.get(pos-1).getMessage().toString();
                    String msgType = chatModelArrayList.get(pos-1).getSender().toString();

                    if(msgType==BOT_KEY){
                        // 대화형 메세지에 대한 tts 기능
                        tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if(msgType==BOT_INFO_KEY){
                        // 복지 정보 형태의 메세지에 대한 tts 기능
                        String title1 = chatModelArrayList.get(pos-3).getTitle();
                        String summary1 = chatModelArrayList.get(pos-3).getSummary();

                        String title2 = chatModelArrayList.get(pos-2).getTitle();
                        String summary2 = chatModelArrayList.get(pos-2).getSummary();

                        String title3 = chatModelArrayList.get(pos-1).getTitle();
                        String summary3 = chatModelArrayList.get(pos-1).getSummary();

                        String text = "검색 결과 적합한 복지 정보 3건을 불러왔습니다\n"+
                                "첫번째 복지 정보는 " + title1 +" 입니다\n" +
                                "두번째 복지 정보는 " + title2 +" 입니다\n" +
                                "세번째 복지 정보는 " + title3 +" 입니다\n" +
                                "혹시나 더 궁금하시다면 제 대화창을 눌러주시면 복지 정보 상세보기로 넘어갑니다.";

                        tts.speak(text,TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else{
                        tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null);
                    }
                    Log.v("ChatActivity TTS isSuccess", "true");
                    Log.v("ChatActivity TTS message",msg);
                }
                catch(Exception err){
                    err.printStackTrace();
                    Log.v("ChatActivity TTS Error", err.getMessage());
                }
            }
        });

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_2);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_1:
                        Intent intent = new Intent(ChatActivity.this, com.example.welfareapp.MainActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigation_2:
                        return true;
                    case R.id.navigation_3:
                        Intent intent3 = new Intent(ChatActivity.this, com.example.welfareapp.MapActivity.class);
                        intent3.putExtras(bundle);
                        startActivity(intent3);
                        finish();
                        return true;
                    case R.id.navigation_4:
                        Intent intent4 = new Intent(ChatActivity.this, MyProfileActivity.class);
                        intent4.putExtras(bundle);
                        startActivity(intent4);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    // get & post api
    private void getResponse(String text, String token){

        // user message to chatRVAdapter
        chatModelArrayList.add(new ChatModel(text, USER_KEY, null, null, 0, token));
        chatRVAdapter.notifyDataSetChanged();

        // params for post api
        JSONObject params = new JSONObject();
        try{
            params.put("token", token);
            params.put("chat_message", text);
            Log.v("chatbot params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
            return;
        }

        // response waiting
        chatModelArrayList.add(new ChatModel("", BOT_WAITING_KEY, null, null, 0, token));
        chatRVAdapter.notifyDataSetChanged();

        SharedPreferences chatResponse = getSharedPreferences("chatResponse", MODE_PRIVATE);
        SharedPreferences.Editor editor= chatResponse.edit();

        //String url_test = "http://34.64.176.63/chatbot/getresponse/dummy0"; // dummy 데이터를 모아놓은 임시 url
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_chatbot, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.v("ChatActivity chatbot response", "true");
                try{
                    int message_type = response.getInt("message_type");
                    if(message_type == 0){
                        Boolean isSuccess = response.getBoolean("success");
                        int statusCode = response.getInt("statusCode");
                        String message_content = response.getString("message_content");

                        editor.putBoolean("success", isSuccess);
                        editor.putInt("statusCode", statusCode);
                        editor.putInt("message_type", message_type);
                        editor.putString("message_content", message_content);

                        Log.v("chatbot message response isSuccess", isSuccess.toString());
                        Log.v("chatbot message response statusCode", Integer.toString(statusCode));
                        Log.v("chatbot message response message_type", Integer.toString(message_type));
                        Log.v("chatbot message response message_content", message_content);

                        editor.commit();
                    }
                    else if(message_type == 1){
                        Boolean isSuccess = response.getBoolean("success");
                        int statusCode = response.getInt("statusCode");
                        String message_content = response.getString("message_content");
                        JSONArray welfare_info = response.getJSONArray("welfare_info");

                        int jar_len = welfare_info.length();
                        if(jar_len >0){
                            for(int i = 0; i < jar_len; i++){

                                Log.v("ChatActivity for loop start",Integer.toString(i));
                                Log.v("ChatActivity jar", welfare_info.toString());
                                Log.v("ChatActivity jar JSONObject", welfare_info.getJSONObject(i).toString());
                                Log.v("ChatActivity jar obj", welfare_info.get(i).toString());

                                // using JSONObject
                                JSONObject obj = welfare_info.getJSONObject(i);

                                String titleName = "welfare_title" + Integer.toString(i+1);
                                String summaryName = "welfare_summary" + Integer.toString(i+1);

                                int welfare_id = obj.getInt("welfare_id");
                                String title = obj.getString("title");
                                String summary = obj.getString("summary");

                                String key = "welfare_info_" + Integer.toString(i);
                                ArrayList<String> list = new ArrayList<String>();
                                list.add(Integer.toString(welfare_id));
                                list.add(title);
                                list.add(summary);

                                JSONArray a = new JSONArray();
                                for (int j = 0; j < list.size(); j++) {
                                    a.put(list.get(j));
                                }
                                if (!list.isEmpty()) {
                                    editor.putString(key, a.toString());
                                    Log.v("ChatActivity json array", a.toString());
                                } else {
                                    editor.putString(key, null);
                                }
                            }
                        }

                        editor.putInt("num_info", jar_len);
                        editor.putBoolean("success", isSuccess);
                        editor.putInt("statusCode", statusCode);
                        editor.putInt("message_type", message_type);
                        editor.putString("message_content", message_content);

                        Log.v("chatbot message response isSuccess", isSuccess.toString());
                        Log.v("chatbot message response statusCode", Integer.toString(statusCode));
                        Log.v("chatbot message response message_type", Integer.toString(message_type));
                        Log.v("chatbot message response message_content", message_content);
                        Log.v("chatbot message response num_info", Integer.toString(jar_len));

                        editor.commit();
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                    Log.v("chatbot JSONException", e.toString());
                    Log.v("chatbot message response error", e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                    Log.v("chatbot Server Error", "response denied");
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Timeout error", Toast.LENGTH_LONG).show();
                }

                Log.v("chatbot onErrorResponse", error.toString());
                error.printStackTrace();
                editor.putBoolean("success", false);
                editor.putInt("statusCode", 500);
                editor.commit();
            }
        });

        Log.v("jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("jsonObjectRequest url", jsonObjectRequest.getUrl());

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 20000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 20000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
                Log.v("chatbot setRetryPolicy error", error.toString());
            }
        });
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        jsonObjectRequest.setShouldCache(false);
        AppHelper.requestQueue.add(jsonObjectRequest);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(chatResponse.getBoolean("success",false)){
                            Log.v("chatResponse  isSuccess","true");
                            if(chatResponse.getInt("message_type",0)==0){

                                int arrayLength = chatModelArrayList.size();
                                chatModelArrayList.remove(arrayLength -1);

                                Log.v("ChatBot response with message_type", Integer.toString(chatResponse.getInt("message_type",0)));
                                String chat_response = chatResponse.getString("message_content", "");
                                chatModelArrayList.add(new ChatModel(chat_response, BOT_KEY, null, null, 0, token));
                                chatRVAdapter.notifyDataSetChanged();
                            }
                            else if(chatResponse.getInt("message_type",0)==1){

                                int arrayLength = chatModelArrayList.size();
                                chatModelArrayList.remove(arrayLength -1);

                                Log.v("ChatBot response with message_type", Integer.toString(chatResponse.getInt("message_type",0)));
                                for(int i = 0; i < chatResponse.getInt("num_info",0); i++){
                                    String key = "welfare_info_" + Integer.toString(i);
                                    String json = chatResponse.getString(key, null);

                                    Log.v("ChatActivity JSON string type loaded", json.toString());
                                    ArrayList<String> decode_list  = new ArrayList<String>();
                                    if (json != null) {
                                        try {
                                            JSONArray a = new JSONArray(json);
                                            for (int j = 0; j < a.length(); j++) {
                                                String str = a.optString(j);
                                                Log.v("ChatActivity JSON string parsing", str);
                                                decode_list.add(str);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    int welfare_id = Integer.parseInt(decode_list.get(0));
                                    String title = decode_list.get(1);
                                    String summary = decode_list.get(2);

                                    chatModelArrayList.add(new ChatModel("", BOT_INFO_KEY, title, summary, welfare_id, token));
                                    chatRVAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        else{
                            int arrayLength = chatModelArrayList.size();
                            chatModelArrayList.remove(arrayLength -1);

                            chatModelArrayList.add(new ChatModel("Server Error!", BOT_KEY, null, null, 0, token));
                            chatRVAdapter.notifyDataSetChanged();
                        }
                    }
                },
                9000
        );
    };

    public interface VolleyCallBack {
        void onSuccess();
    }

    public class DialogResponse{
        public synchronized void request(String text, String token, final VolleyCallBack volleyCallBack){
            chatModelArrayList.add(new ChatModel(text, USER_KEY, null, null, 0, token));
            chatRVAdapter.notifyDataSetChanged();

            RecyclerView chatRVList = (RecyclerView)findViewById(R.id.chatList);
            chatRVList.scrollToPosition(chatRVList.getAdapter().getItemCount() - 1);

            JSONObject params = new JSONObject();
            try{
                params.put("token", token);
                params.put("chat_message", text);
            }
            catch(JSONException e){
                e.printStackTrace();
                return;
            }
            chatModelArrayList.add(new ChatModel("", BOT_WAITING_KEY, null, null, 0, token));
            chatRVAdapter.notifyDataSetChanged();
            chatRVList.scrollToPosition(chatRVList.getAdapter().getItemCount() - 1);

            EditText et_message = (EditText)findViewById(R.id.et_message);
            et_message.setText("");

            SharedPreferences chatResponse = getSharedPreferences("chatResponse", MODE_PRIVATE);
            SharedPreferences.Editor editor= chatResponse.edit();

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_chatbot, params, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("ChatActivity chatbot response", "true");
                    try{
                        int message_type = response.getInt("message_type");
                        if(message_type == 0){
                            Boolean isSuccess = response.getBoolean("success");
                            int statusCode = response.getInt("statusCode");
                            String message_content = response.getString("message_content");

                            editor.putBoolean("success", isSuccess);
                            editor.putInt("statusCode", statusCode);
                            editor.putInt("message_type", message_type);
                            editor.putString("message_content", message_content);
                            editor.commit();
                        }
                        else if(message_type == 1){
                            Boolean isSuccess = response.getBoolean("success");
                            int statusCode = response.getInt("statusCode");
                            String message_content = response.getString("message_content");
                            JSONArray welfare_info = response.getJSONArray("welfare_info");

                            int jar_len = welfare_info.length();
                            if(jar_len >0){
                                for(int i = 0; i < jar_len; i++){
                                    // using JSONObject
                                    JSONObject obj = welfare_info.getJSONObject(i);

                                    String titleName = "welfare_title" + Integer.toString(i+1);
                                    String summaryName = "welfare_summary" + Integer.toString(i+1);

                                    int welfare_id = obj.getInt("welfare_id");
                                    String title = obj.getString("title");
                                    String summary = obj.getString("summary");

                                    String key = "welfare_info_" + Integer.toString(i);
                                    ArrayList<String> list = new ArrayList<String>();
                                    list.add(Integer.toString(welfare_id));
                                    list.add(title);
                                    list.add(summary);

                                    JSONArray a = new JSONArray();
                                    for (int j = 0; j < list.size(); j++) {
                                        a.put(list.get(j));
                                    }
                                    if (!list.isEmpty()) {
                                        editor.putString(key, a.toString());
                                        Log.v("ChatActivity json array", a.toString());
                                    } else {
                                        editor.putString(key, null);
                                    }
                                }
                            }

                            editor.putInt("num_info", jar_len);
                            editor.putBoolean("success", isSuccess);
                            editor.putInt("statusCode", statusCode);
                            editor.putInt("message_type", message_type);
                            editor.putString("message_content", message_content);
                            editor.commit();
                        }
                        volleyCallBack.onSuccess();
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error instanceof NetworkError) {
                    } else if (error instanceof ServerError) {
                        Log.v("chatbot Server Error", "response denied");
                    } else if (error instanceof AuthFailureError) {
                    } else if (error instanceof ParseError) {
                    } else if (error instanceof NoConnectionError) {
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(getApplicationContext(), "Timeout error", Toast.LENGTH_LONG).show();
                    }

                    Log.v("chatbot onErrorResponse", error.toString());
                    error.printStackTrace();
                    editor.putBoolean("success", false);
                    editor.putInt("statusCode", 500);
                    editor.commit();
                }
            });
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 20000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 20000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    error.printStackTrace();
                    Log.v("chatbot setRetryPolicy error", error.toString());
                }
            });

            if(AppHelper.requestQueue == null){
                AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
            }
            jsonObjectRequest.setShouldCache(false);
            AppHelper.requestQueue.add(jsonObjectRequest);
        }

        public synchronized void response(String text, String token){
            SharedPreferences chatResponse = getSharedPreferences("chatResponse", MODE_PRIVATE);

            if(chatResponse.getBoolean("success",false)){
                Log.v("chatResponse  isSuccess","true");
                if(chatResponse.getInt("message_type",0)==0){

                    int arrayLength = chatModelArrayList.size();
                    chatModelArrayList.remove(arrayLength -1);

                    Log.v("ChatBot response with message_type", Integer.toString(chatResponse.getInt("message_type",0)));
                    String chat_response = chatResponse.getString("message_content", "");
                    chatModelArrayList.add(new ChatModel(chat_response, BOT_KEY, null, null, 0, token));
                    chatRVAdapter.notifyDataSetChanged();
                    RecyclerView chatRVList = (RecyclerView)findViewById(R.id.chatList);
                    chatRVList.scrollToPosition(chatRVList.getAdapter().getItemCount() - 1);
                }
                else if(chatResponse.getInt("message_type",0)==1){

                    int arrayLength = chatModelArrayList.size();
                    chatModelArrayList.remove(arrayLength -1);

                    Log.v("ChatBot response with message_type", Integer.toString(chatResponse.getInt("message_type",0)));
                    for(int i = 0; i < chatResponse.getInt("num_info",0); i++){
                        String key = "welfare_info_" + Integer.toString(i);
                        String json = chatResponse.getString(key, null);

                        Log.v("ChatActivity JSON string type loaded", json.toString());
                        ArrayList<String> decode_list  = new ArrayList<String>();
                        if (json != null) {
                            try {
                                JSONArray a = new JSONArray(json);
                                for (int j = 0; j < a.length(); j++) {
                                    String str = a.optString(j);
                                    Log.v("ChatActivity JSON string parsing", str);
                                    decode_list.add(str);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        int welfare_id = Integer.parseInt(decode_list.get(0));
                        String title = decode_list.get(1);
                        String summary = decode_list.get(2);

                        chatModelArrayList.add(new ChatModel("", BOT_INFO_KEY, title, summary, welfare_id, token));
                        chatRVAdapter.notifyDataSetChanged();
                        RecyclerView chatRVList = (RecyclerView)findViewById(R.id.chatList);
                        chatRVList.scrollToPosition(chatRVList.getAdapter().getItemCount() - 1);
                    }
                }
            }
            else{
                int arrayLength = chatModelArrayList.size();
                chatModelArrayList.remove(arrayLength -1);
                chatModelArrayList.add(new ChatModel("Server Error!", BOT_KEY, null, null, 0, token));
                chatRVAdapter.notifyDataSetChanged();
                RecyclerView chatRVList = (RecyclerView)findViewById(R.id.chatList);
                chatRVList.scrollToPosition(chatRVList.getAdapter().getItemCount() - 1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
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