package com.example.tave0915;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg_toolbox2, parent, false);
                return new BotViewHolder2(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chatModel = chatModelArrayList.get(position);
        switch(chatModel.getSender()){
            case "user":
                ((UserViewHolder)holder).userTV.setText(chatModel.getMessage());
                break;
            case "bot":
                ((BotViewHolder)holder).botMsgTV.setText(chatModel.getMessage());
                break;
            case "bot-welfare-info":
                /* bot_msg_toolbox2 component editor */
                ((BotViewHolder2)holder).botMsgTV_title1.setText(chatModel.getWelfareInfoTitleArray()[0]);
                ((BotViewHolder2)holder).botMsgTV_title2.setText(chatModel.getWelfareInfoTitleArray()[1]);
                ((BotViewHolder2)holder).botMsgTV_title3.setText(chatModel.getWelfareInfoTitleArray()[2]);
                ((BotViewHolder2)holder).botMsgTV_summary1.setText(chatModel.getWelfareInfoSummaryArray()[0]);
                ((BotViewHolder2)holder).botMsgTV_summary2.setText(chatModel.getWelfareInfoSummaryArray()[1]);
                ((BotViewHolder2)holder).botMsgTV_summary3.setText(chatModel.getWelfareInfoSummaryArray()[2]);
                break;
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
        TextView botMsgTV_title1;
        TextView botMsgTV_summary1;
        TextView botMsgTV_title2;
        TextView botMsgTV_summary2;
        TextView botMsgTV_title3;
        TextView botMsgTV_summary3;

        public BotViewHolder2(@NonNull View itemView){
            super(itemView);
            botMsgTV_title1 = itemView.findViewById(R.id.idTVBot_title1);
            botMsgTV_title2 = itemView.findViewById(R.id.idTVBot_title2);
            botMsgTV_title3 = itemView.findViewById(R.id.idTVBot_title3);
            botMsgTV_summary1 = itemView.findViewById(R.id.idTVBot_summary1);
            botMsgTV_summary2 = itemView.findViewById(R.id.idTVBot_summary2);
            botMsgTV_summary3 = itemView.findViewById(R.id.idTVBot_summary3);
        }
    }


}
