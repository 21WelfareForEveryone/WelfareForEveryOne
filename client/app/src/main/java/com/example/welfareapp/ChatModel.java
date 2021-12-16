package com.example.welfareapp;

public class ChatModel {
    private String message;
    private String sender;
    private String[] welfareInfoTitleArray; // 폐기 예정
    private String[] welfareInfoSummaryArray; // 폐기 예정

    private String title;
    private String summary;
    private int welfare_id;
    private String token;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

//    public ChatModel(String message, String sender, String[] welfareInfoTitleArray, String[] welfareInfoSummaryArray) {
//        this.message = message;
//        this.sender = sender;
//        this.welfareInfoTitleArray = welfareInfoTitleArray;
//        this.welfareInfoSummaryArray = welfareInfoSummaryArray;
//    }

    public ChatModel(String message, String sender, String title, String summary, int welfare_id, String token){
        this.message = message;
        this.sender = sender;
        this.title = title;
        this.summary = summary;
        this.welfare_id = welfare_id;
        this.token = token;
    }

    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    public String[] getWelfareInfoTitleArray() {
        return welfareInfoTitleArray;
    }

    public void setWelfareInfoTitleArray(String[] welfareInfoTitleArray) {
        this.welfareInfoTitleArray = welfareInfoTitleArray;
    }

    public String[] getWelfareInfoSummaryArray() {
        return welfareInfoSummaryArray;
    }

    public void setWelfareInfoSummaryArray(String[] welfareInfoSummaryArray) {
        this.welfareInfoSummaryArray = welfareInfoSummaryArray;
    }

    // chatModel Variables for message_type = 1
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getWelfare_id() {
        return welfare_id;
    }

    public void setWelfare_id(int welfare_id) {
        this.welfare_id = welfare_id;
    }
}
