package com.example.welfareapp;

public class MainCategoryCard {
    private String category_title;
    private int category_num;
    public MainCategoryCard(String category_title, int category_num){
        this.category_title = category_title;
        this.category_num = category_num;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }
    public int getCategory_num(){
        return category_num;
    }
    public void setCategory_num(int category_num){
        this.category_num = category_num;
    }
}
