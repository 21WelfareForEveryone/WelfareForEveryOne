package com.example.welfareapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

public class WelfareViewAdapter extends RecyclerView.Adapter<WelfareViewAdapter.ViewHolder> {

    private ArrayList<com.example.welfareapp.WelfareInfoComponent> CardList = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_summary;
        CardView cv_welfare_info;
        ImageView welfare_img;
        ToggleButton toggle_favorite;

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_summary = itemView.findViewById(R.id.tv_summary);
            cv_welfare_info = itemView.findViewById(R.id.CV_welfare_info);
            welfare_img = itemView.findViewById(R.id.welfare_img);
            toggle_favorite = itemView.findViewById(R.id.toggle_favorite);
        }
    }

    public WelfareViewAdapter(ArrayList<com.example.welfareapp.WelfareInfoComponent> list) {
        this.CardList = list ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.card_component, parent, false) ;
        ViewHolder vh = new ViewHolder(view) ;
        return vh ;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        com.example.welfareapp.WelfareInfoComponent info = CardList.get(position);
        int welfare_id = info.getWelfare_id();
        Log.v("welfareInfo welfare_id", Integer.toString(welfare_id));
        int category_num = info.getCategory();
        Log.v("welfareInfo category_num", Integer.toString(category_num));
        String token = info.getToken();
        Log.v("welfareInfo token", token);
        Boolean favorite = info.getFavorite();
        Log.v("welfareInfo favorite", favorite.toString());

        try{
            switch(category_num){
                case 0:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_00);
                    break;
                case 1:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_01);
                    break;
                case 2:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_02);
                    break;
                case 3:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_03);
                    break;
                case 4:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_04);
                    break;
                case 5:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_05);
                    break;
                case 6:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_06);
                    break;
                case 7:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_07);
                    break;
                case 8:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_08);
                    break;
                case 9:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_09);
                    break;
                case 10:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_10);
                    break;
                case 11:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_11);
                    break;
                case 12:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_12);
                    break;
                case 13:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_13);
                    break;
                case 14:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_14);
                    break;
                case 15:
                    holder.welfare_img.setImageResource(R.drawable.img_icon_15);
                    break;
                default:
                    break;
            }
        }
        catch(Exception err){
            holder.welfare_img.setImageResource(R.drawable.ic_user_profile);
            err.printStackTrace();
            Log.v("WelfareViewAdapter Exception Error", err.getMessage());
        }
        holder.tv_title.setText(info.getTitle());
        holder.tv_summary.setText(info.getSummary());
        holder.cv_welfare_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Welfare CardView click!","true");
                Bundle bundle = new Bundle();
                bundle.putInt("welfare_id", welfare_id);
                bundle.putString("token",token);
                bundle.putBoolean("is_liked", favorite);
                Context context = v.getContext();
                try{
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
                catch(Exception err){
                    Log.v("WelfareInfo to DetailActivity intent process","error");
                }
            }
        });

        // toggle button
        holder.toggle_favorite.setChecked(favorite);
        holder.toggle_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                                Log.v("welfareInfo create favorite info", "true");
                                Log.v("welfareInfo create favorite, isSuccess", isSuccess.toString());
                                Log.v("welfareInfo create favorite, statusCode", Integer.toString(statusCode));
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
                                Log.v("welfareInfo delete favorite info", "true");
                                Log.v("welfareInfo delete favorite, isSuccess", isSuccess.toString());
                                Log.v("welfareInfo delete favorite, statusCode", Integer.toString(statusCode));
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

    @Override
    public int getItemCount() {
        return CardList.size();
    }
}
