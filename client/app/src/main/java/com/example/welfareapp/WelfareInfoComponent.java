package com.example.welfareapp;

public class WelfareInfoComponent {
    private int welfare_id;
    private String title;
    private String summary;
    private String who;
    private String criteria;
    private String what;
    private String how;
    private String info_calls;
    private String sites;
    private int category;
    private String token;
    private boolean favorite;

    public WelfareInfoComponent(int welfare_id, String title, String summary, String who, String criteria,
                                String what, String how, String info_calls, String sites, int category, String token, boolean favorite){
        this.welfare_id = welfare_id;
        this.title = title;
        this.summary = summary;
        this.who = who;
        this.criteria = criteria;
        this.what = what;
        this.how = how;
        this.info_calls = info_calls;
        this.sites = sites;
        this.token = token;
        this.category = category;
        this.favorite = favorite;
    };
    public void setWelfare_id(int welfare_id){
        this.welfare_id = welfare_id;
    };
    public int getWelfare_id(){
        return welfare_id;
    };
    public String getTitle() {
        return title;
    };
    public void setTitle(String title) {
        this.title = title;
    };
    public void setSummary(String summary) {
        this.summary = summary;
    };
    public String getSummary(){
        return summary;
    };
    public void setWho(String who){
        this.who = who;
    };
    public String getWho(){
        return who;
    };
    public void setCriteria(String criteria){
        this.criteria = criteria;
    };
    public String getCriteria(){
        return criteria;
    };
    public void setWhat(String what){
        this.what = what;
    };
    public String getWhat(){
        return what;
    };
    public void setHow(String how){
        this.how = how;
    };
    public String getHow(){
        return how;
    };
    public void setInfo_calls(String info_calls){
        this.info_calls = info_calls;
    };
    public String getInfo_calls(){
        return info_calls;
    };
    public void setCategory(int category){
        this.category = category;
    };
    public int  getCategory(){
        return category;
    };
    public void setSites(String sites){
        this.sites = sites;
    };
    public String getSites(){
        return sites;
    };
    public String getToken(){return token;};
    public void setToken(String token){this.token = token;};

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
