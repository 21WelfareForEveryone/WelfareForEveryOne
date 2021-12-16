package com.example.welfareapp;

public class PushNotificationComponent {
    private String tv_notify_title;
    private String tv_notify_summary;
    private String notice_day;
    private String token;
    private int welfareId;


    public PushNotificationComponent(int welfareId, String tv_notify_title, String tv_notify_summary, String notice_day, String token){
        this.welfareId = welfareId;
        this.tv_notify_summary = tv_notify_summary;
        this.tv_notify_title = tv_notify_title;
        this.notice_day = notice_day;
        this.token = token;
    }

    public int getWelfareId(){
        return welfareId;
    }
    public void setWelfareId(int welfareId){
        this.welfareId = welfareId;
    }
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    public String getTv_notify_summary() {
        return tv_notify_summary;
    }

    public void setTv_notify_summary(String tv_notify_summary) {
        this.tv_notify_summary = tv_notify_summary;
    }

    public String getTv_notify_title() {
        return tv_notify_title;
    }

    public void setTv_notify_title(String tv_notify_title) {
        this.tv_notify_title = tv_notify_title;
    }

    public String getNotice_day() {
        return notice_day;
    }

    public void setNotice_day(String notice_day) {
        this.notice_day = notice_day;
    }
}
