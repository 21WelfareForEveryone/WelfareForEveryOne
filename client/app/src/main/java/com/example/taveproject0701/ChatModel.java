package com.example.tave0915;

public class ChatModel {
    private String message;
    private String sender;
    private String[] welfareInfoTitleArray;
    private String[] welfareInfoSummaryArray;

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

    public ChatModel(String message, String sender, String[] welfareInfoTitleArray, String[] welfareInfoSummaryArray) {
        this.message = message;
        this.sender = sender;
        this.welfareInfoTitleArray = welfareInfoTitleArray;
        this.welfareInfoSummaryArray = welfareInfoSummaryArray;
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
}
