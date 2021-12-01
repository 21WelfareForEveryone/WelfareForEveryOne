package com.example.tave0915;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.tave0915.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Category RV list
    private MainRVAdapter mainRVAdapter;
    private ArrayList<MainCategoryCard> categoryList;

    // Recommended Welfare Info RV list
    private WelfareViewAdapter welfareViewAdapter;
    private ArrayList<WelfareInfoComponent> welfareInfoComponentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");

        RecyclerView RV_category = (RecyclerView)findViewById(R.id.RV_category);
        categoryList = new ArrayList<>();

        for(int i = 0; i<16; i++){
            String categoryName = "카테고리 " + Integer.toString(i+1);
            MainCategoryCard categoryCard = new MainCategoryCard(categoryName);
            categoryList.add(categoryCard);
        }

        mainRVAdapter = new MainRVAdapter(categoryList, getApplicationContext(), token);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV_category.setLayoutManager(linearLayoutManager);
        RV_category.setAdapter(mainRVAdapter);

        // Recommended welfare Info data loaded
        RecyclerView welfareRecommendedRV = (RecyclerView)findViewById(R.id.welfare_recommended_RV);
        welfareInfoComponentArrayList = new ArrayList<>();

        welfareViewAdapter = new WelfareViewAdapter(welfareInfoComponentArrayList);
        LinearLayoutManager welfareLinearLayoutManger = new LinearLayoutManager(this);

        welfareRecommendedRV.setLayoutManager(welfareLinearLayoutManger);
        welfareRecommendedRV.setAdapter(welfareViewAdapter);

        try{
            getRecommendedWelfareInfo(token);
            Log.v("MainActivity recommended welfare info load process","success");
        }
        catch(Exception err){
            Log.v("MainActivity recommended welfare info load process","failed");
        }

        // navigation onClickListener
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_1);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_1:
                        return true;
                    case R.id.navigation_2:
                        Intent intent2 = new Intent(MainActivity.this, ChatActivity.class);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        finish();
                        return true;
                    case R.id.navigation_3:
                        Intent intent3 = new Intent(MainActivity.this, MapActivity.class);
                        intent3.putExtras(bundle);
                        startActivity(intent3);
                        finish();
                        return true;
                    case R.id.navigation_4:
                        Intent intent4 = new Intent(MainActivity.this, MyProfileActivity.class);
                        intent4.putExtras(bundle);
                        startActivity(intent4);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void getRecommendedWelfareInfo(String token){

    }
}