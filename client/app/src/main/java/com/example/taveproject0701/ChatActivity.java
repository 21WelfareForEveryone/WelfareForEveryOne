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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ChatRVAdapter chatRVAdapter;
    private ArrayList<ChatModel>chatModelArrayList;

    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private final String BOT_INFO_KEY = "bot-welfare-info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");
        Log.v("chatbot bundle token", token);

        // keyboard disappeared
        EditText et_message = (EditText)findViewById(R.id.et_message);
        et_message.setShowSoftInputOnFocus(false);

        //RecyclerView Event Listener
        RecyclerView chatRVList = (RecyclerView)findViewById(R.id.chatList);
        chatModelArrayList = new ArrayList<>();
        chatRVAdapter =  new ChatRVAdapter(chatModelArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);

        chatRVList.setLayoutManager(manager);
        chatRVList.setAdapter(chatRVAdapter);

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
                    getResponse(et_message.getText().toString(), token);
                    et_message.setText("");
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_2);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_1:
                        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigation_2:
                        return true;
                    case R.id.navigation_3:
                        Intent intent3 = new Intent(ChatActivity.this, MapActivity.class);
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
        chatModelArrayList.add(new ChatModel(text, USER_KEY, null, null));
        chatRVAdapter.notifyDataSetChanged();

        // params : post에 필요한 변수
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

        SharedPreferences chatResponse = getSharedPreferences("chatResponse", MODE_PRIVATE);
        SharedPreferences.Editor editor= chatResponse.edit();

        String url_test = "http://34.64.177.178/chatbot/getresponse/dummy1";
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_test, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.v("chatbot response", response.toString());
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
                        Log.v("chatbot message  response statusCode", Integer.toString(statusCode));
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
                        for(int i = 0; i < jar_len; i++){
                            JSONObject obj = welfare_info.getJSONObject(i);

                            String titleName = "welfare_title" + Integer.toString(i+1);
                            String summaryName = "welfare_summary" + Integer.toString(i+1);

                            editor.putString(titleName, obj.getString("title"));
                            editor.putString(summaryName, obj.getString("summary"));
                            Log.v("chatbot message response titleName", obj.getString("title"));
                            Log.v("chatbot message response summary", obj.getString("summary"));
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
                    Log.v("chatbot message response error", e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("chatbot Server Error", "response denied");
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        Log.v("jsonObjectRequest", jsonObjectRequest.toString());
        Log.v("jsonObjectRequest url", jsonObjectRequest.getUrl());

        if(chatResponse.getBoolean("success",false)){

            if(chatResponse.getInt("message_type",0)==0){
                String chat_response = chatResponse.getString("message_content", "");
                chatModelArrayList.add(new ChatModel(chat_response, BOT_KEY, null, null));
                chatRVAdapter.notifyDataSetChanged();
            }
            else if(chatResponse.getInt("message_type",0)==1){
                int jar_len = chatResponse.getInt("num_info",0);
                String[] welfareInfoTitle = new String[jar_len];
                String[] welfareInfoSummary= new String[jar_len];
                for(int i = 0; i < jar_len; i++){
                    String titleName = "welfare_title" + Integer.toString(i+1);
                    String summaryName = "welfare_summary" + Integer.toString(i+1);
                    welfareInfoTitle[i] = chatResponse.getString(titleName, "");
                    welfareInfoSummary[i] = chatResponse.getString(summaryName, "");
                }

                chatModelArrayList.add(new ChatModel("", BOT_INFO_KEY, welfareInfoTitle, welfareInfoSummary));
                chatRVAdapter.notifyDataSetChanged();
            }
            else{

            }
        }
        else{
            chatModelArrayList.add(new ChatModel("Server Error!", BOT_KEY, null, null));
            chatRVAdapter.notifyDataSetChanged();
        }

        jsonObjectRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    };
}