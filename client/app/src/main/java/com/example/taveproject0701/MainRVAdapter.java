package com.example.tave0915;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainRVAdapter extends RecyclerView.Adapter<MainRVAdapter.ViewHolder> {

    private ArrayList<MainCategoryCard> CardList = null;
    private Context mContext;
    String token;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        CardView cardView;
        ViewHolder(View itemView) {
            super(itemView) ;
            tv_title = itemView.findViewById(R.id.title_category);
            cardView = itemView.findViewById(R.id.CV_category);
        }
    }

    public MainRVAdapter(ArrayList<MainCategoryCard> list, Context mContext, String token) {
        this.CardList = list ;
        this.mContext = mContext;
        this.token = token;
    }


    @NonNull
    @Override
    public MainRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.category_select_component, parent, false) ;
        MainRVAdapter.ViewHolder vh = new MainRVAdapter.ViewHolder(view) ;
        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull MainRVAdapter.ViewHolder holder, int position) {
        MainCategoryCard categoryCard = CardList.get(position);
        holder.tv_title.setText(categoryCard.getCategory_title());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int category_num = Integer.parseInt(categoryCard.getCategory_title().replaceAll("\\D+",""));
                Bundle bundle = new Bundle();
                bundle.putString("token", token);
                bundle.putInt("category",category_num);
                Context context = v.getContext();
                try{
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
                catch(Exception err){
                    Log.v("Category to ListActivity intent process","error");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return CardList.size();
    }

    public String getToken(){return this.token;}
}
