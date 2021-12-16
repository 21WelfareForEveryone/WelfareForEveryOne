package com.example.welfareapp;

import static com.example.welfareapp.URLs.url_push_toggle;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PushViewAdapter extends RecyclerView.Adapter<PushViewAdapter.ViewHolder>{
    private ArrayList<PushNotificationComponent> CardList = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_notify_title;
        TextView tv_notify_summary;
        TextView notice_day;
        Switch toggle_notification;

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            tv_notify_title = itemView.findViewById(R.id.tv_notify_title);
            tv_notify_summary = itemView.findViewById(R.id.tv_notify_summary);
            notice_day = itemView.findViewById(R.id.notice_day);
            toggle_notification = itemView.findViewById(R.id.toggle_notification);
        }
    }
    public PushViewAdapter(ArrayList<PushNotificationComponent> list) {
        this.CardList = list ;
    }

    @Override
    public PushViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.card_component_notification, parent, false) ;
        PushViewAdapter.ViewHolder vh = new ViewHolder(view) ;
        return vh ;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        PushNotificationComponent info = CardList.get(position);
        holder.tv_notify_title.setText(info.getTv_notify_title());
        holder.tv_notify_summary.setText(info.getTv_notify_summary());
        holder.notice_day.setText(info.getNotice_day());

        String token = info.getToken();
        int welfareId = info.getWelfareId();

        // 토글 스위치에 대한 리스너
        // on / off => server request
        // push notification on => setChecked : true
        // push notification off => setChecked : false
        holder.toggle_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // API 추가
                // toggle request : url_push_toggle
                // params : token, welfare_id, toggle_command
                // isChecked convert to String "On" / "Off"

                JSONObject params = new JSONObject();

                if(isChecked){
                    // command : On
                    try{
                        params.put("token", token);
                        params.put("welfare_id", welfareId);
                        params.put("toggle_command", "On");
                    }
                    catch(Exception err){
                        err.printStackTrace();
                        Log.v("PushViewAdapter params error",err.getMessage());
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_push_toggle, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("PushViewAdapter toggle response", "true");
                            try{
                                int statusCode = response.getInt("statusCode");
                                boolean isSuccess = response.getBoolean("success");
                                Log.v("PushViewAdapter toggle response statusCode", Integer.toString(statusCode));
                                Log.v("PushViewAdapter toggle response isSuccess", Boolean.toString(isSuccess));
                            }
                            catch(JSONException err){
                                err.printStackTrace();
                                Log.v("PushViewAdapter toggle response error", err.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Log.v("PushViewAdapter toggle response error(Volley)", error.toString());
                        }
                    });

                    Toast.makeText(buttonView.getContext(), "푸시 알림 설정을 On 하였습니다.", Toast.LENGTH_LONG).show();
                    VolleySingleton.getInstance(buttonView.getContext()).addToRequestQueue(jsonObjectRequest);
                }
                else{
                    // command : Off
                    try{
                        params.put("token", token);
                        params.put("welfare_id", welfareId);
                        params.put("toggle_command", "Off");
                    }
                    catch(Exception err){
                        err.printStackTrace();
                        Log.v("PushViewAdapter params error",err.getMessage());
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_push_toggle, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("PushViewAdapter toggle response", "true");
                            try{
                                int statusCode = response.getInt("statusCode");
                                boolean isSuccess = response.getBoolean("success");
                                Log.v("PushViewAdapter toggle response statusCode", Integer.toString(statusCode));
                                Log.v("PushViewAdapter toggle response isSuccess", Boolean.toString(isSuccess));
                            }
                            catch(JSONException err){
                                err.printStackTrace();
                                Log.v("PushViewAdapter toggle response error", err.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Log.v("PushViewAdapter toggle response error(Volley)", error.toString());
                        }
                    });

                    Toast.makeText(buttonView.getContext(), "푸시 알림 설정을 Off 하였습니다.", Toast.LENGTH_LONG).show();
                    VolleySingleton.getInstance(buttonView.getContext()).addToRequestQueue(jsonObjectRequest);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return CardList.size();
    }
}
