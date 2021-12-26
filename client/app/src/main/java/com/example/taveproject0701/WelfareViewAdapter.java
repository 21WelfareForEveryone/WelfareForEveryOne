package com.example.tave0915;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WelfareViewAdapter extends RecyclerView.Adapter<WelfareViewAdapter.ViewHolder> {

    private ArrayList<WelfareInfoComponent> CardList = null;
//    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_summary;

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_summary = itemView.findViewById(R.id.tv_summary);
        }
    }

    public WelfareViewAdapter(ArrayList<WelfareInfoComponent> list) {
        this.CardList = list ;
    }

    @Override
    public WelfareViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.card_component, parent, false) ;
        WelfareViewAdapter.ViewHolder vh = new WelfareViewAdapter.ViewHolder(view) ;
        return vh ;
    }

    @Override
    public void onBindViewHolder(WelfareViewAdapter.ViewHolder holder, int position) {

        WelfareInfoComponent info = CardList.get(position);
        holder.tv_title.setText(info.getTitle());
        holder.tv_summary.setText(info.getSummary());
    }

    @Override
    public int getItemCount() {
        return CardList.size();
    }
}
