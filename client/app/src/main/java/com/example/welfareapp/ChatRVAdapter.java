package com.example.welfareapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatRVAdapter extends RecyclerView.Adapter{
    private ArrayList<ChatModel> chatModelArrayList;
    private Context context;

    public ChatRVAdapter(ArrayList<ChatModel> chatsModelArrayList, Context context){
        this.chatModelArrayList = chatsModelArrayList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg_toolbox, parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg_toolbox, parent, false);
                return new BotViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_info_toolbox, parent, false);
                return new BotInfoViewHolder(view);
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg_toolbox2, parent, false);
                return new BotViewHolder2(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chatModel = chatModelArrayList.get(position);
        String token = chatModel.getToken();
        switch(chatModel.getSender()){
            case "user":
                ((UserViewHolder)holder).userTV.setText(chatModel.getMessage());
                break;
            case "bot":
                ((BotViewHolder)holder).botMsgTV.setText(chatModel.getMessage());
                break;
            case "bot-welfare-info":
                /* bot_msg_toolbox2 component editor */
                /*
                ((BotViewHolder2)holder).botMsgTV_title1.setText(chatModel.getWelfareInfoTitleArray()[0]);
                ((BotViewHolder2)holder).botMsgTV_title2.setText(chatModel.getWelfareInfoTitleArray()[1]);
                ((BotViewHolder2)holder).botMsgTV_title3.setText(chatModel.getWelfareInfoTitleArray()[2]);
                ((BotViewHolder2)holder).botMsgTV_summary1.setText(chatModel.getWelfareInfoSummaryArray()[0]);
                ((BotViewHolder2)holder).botMsgTV_summary2.setText(chatModel.getWelfareInfoSummaryArray()[1]);
                ((BotViewHolder2)holder).botMsgTV_summary3.setText(chatModel.getWelfareInfoSummaryArray()[2]);
                break;
                 */
                ((BotInfoViewHolder)holder).botMsgTV_title.setText(chatModel.getTitle());
                ((BotInfoViewHolder)holder).botMsgTV_summary.setText(chatModel.getSummary());
                ((BotInfoViewHolder)holder).btn_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("ChatActivity btn_detail click!", "true");
                        int welfare_id = chatModel.getWelfare_id();
                        Bundle bundle = new Bundle();
                        bundle.putInt("welfare_id", welfare_id);
                        bundle.putString("token", token);
                        Context context = v.getContext();
                        try{
                            Intent intent = new Intent(context, DetailActivity.class);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                        catch(Exception err){
                            Log.v("ChatModel to DetailActivity intent process Error",err.getMessage());
                        }
                    }
                });
        }
    }

    @Override
    public int getItemViewType(int position){
        switch(chatModelArrayList.get(position).getSender()){
            case "user":
                return 0;
            case "bot":
                return 1;
            case "bot-welfare-info": // bot_msg_toolbox2을 위해 추가..
                return 2;
            case "bot-wait-response":
                return 3;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return chatModelArrayList.size();
    }


    // user and  bot view holder
    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView userTV;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.idTVUser);
        }
    }

    //chatbot dialog
    public static class BotViewHolder extends RecyclerView.ViewHolder{
        TextView botMsgTV;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botMsgTV = itemView.findViewById(R.id.idTVBot);
        }
    }

    //chatbot dialog2 : detail info
    public static class BotViewHolder2 extends RecyclerView.ViewHolder{

        public BotViewHolder2(@NonNull View itemView){
            super(itemView);

        }
    }

    // new version of BotViewHolder2
    public static class BotInfoViewHolder extends RecyclerView.ViewHolder{
        TextView botMsgTV_title;
        TextView botMsgTV_summary;
        Button btn_detail;

        public BotInfoViewHolder(@NonNull View itemView){
            super(itemView);
            botMsgTV_title = itemView.findViewById(R.id.idTVBot_title);
            botMsgTV_summary = itemView.findViewById(R.id.idTVBot_summary);
            btn_detail = itemView.findViewById(R.id.btn_detail);
        }
    }
}
