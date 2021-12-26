package com.example.tave0915;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tave0915.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_id;
    EditText et_pwd;

    String[] income_items = {"1분위", "2분위", "3분위", "4분위", "5분위", "6분위", "7분위", "8분위","9분위"};
    int Income;

    String[] city_items = {"서울특별시"};
    String[] local_items = {"강북구","강서구", "강동구", "강남구", "마포구", "영등포구", "관악구", "종로구", "노원구"};
    String City;
    String Local;
    String Address;

    // boolean type variables
    RadioGroup RG_gender;
    RadioButton rb_male;
    RadioButton rb_female;
    int Gender;

    // spinner type variables
    Spinner spinner_income;

    Spinner spinner_address_city;
    Spinner spinner_address_gu;

    String[] life_cycle_items = {"영유아","아동, 청소년", "청년", "중,장년", "노년"};
    Spinner spinner_life_cycle;
    int life_cycle;

    RadioGroup RG_is_multicultural;
    RadioButton rb_is_multicultural_true;
    RadioButton rb_is_multicultural_false;
    int is_multicultural;

    RadioGroup RG_family_state;
    RadioButton rb_family_state_true;
    RadioButton rb_family_state_false;
    int family_state;

    RadioGroup RG_is_disabled;
    RadioButton rb_is_disabled_true;
    RadioButton rb_is_disabled_false;
    int is_disabled;

    RadioGroup RG_interest;
    RadioButton rb_interest0;
    RadioButton rb_interest1;
    RadioButton rb_interest2;
    RadioButton rb_interest3;
    RadioButton rb_interest4;
    RadioButton rb_interest5;
    RadioButton rb_interest6;

    int interest;

    // photo upload and edit
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CROP = 1;
    ImageView img_view;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI = null;
    Boolean album;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = (Bundle) getIntent().getExtras();
        String token = bundle.getString("token");
        Log.v("token from bundle in profileActivity", token);

        // username, id, password
        et_username = (EditText)findViewById(R.id.et_username);
        et_id = (EditText)findViewById(R.id.et_id);
        et_pwd = (EditText)findViewById(R.id.et_pwd);

        // gender
        rb_male =  (RadioButton)findViewById(R.id.btn_male);
        rb_female = (RadioButton)findViewById(R.id.btn_female);
        RG_gender = (RadioGroup)findViewById(R.id.genderRadioGroup);

        // spinner(소득분위)
        spinner_income = (Spinner)findViewById(R.id.spinner_income);

        // spinner(주소)
        spinner_address_city = (Spinner)findViewById(R.id.spinner_address_city);
        spinner_address_gu = (Spinner)findViewById(R.id.spinner_address_gu);

        // income preprocessing
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, income_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_income.setAdapter(adapter);
        spinner_income.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Income = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Income = -1;
            }
        });

        // address preprocessing
        ArrayAdapter<String> adapter_address_city = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, city_items);
        adapter_address_city.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_address_city.setAdapter(adapter_address_city);
        spinner_address_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City = city_items[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                City = city_items[0];
            }
        });

        ArrayAdapter<String> adapter_address_gu = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, local_items);
        adapter_address_gu.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_address_gu.setAdapter(adapter_address_gu);
        spinner_address_gu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Local = (String) local_items[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Local = local_items[0];
            }
        });

        RG_is_multicultural = (RadioGroup)findViewById(R.id.isMultiCultureRadioGroup);
        rb_is_multicultural_true = (RadioButton)findViewById(R.id.btn_isMultiCulture_true);
        rb_is_multicultural_false = (RadioButton)findViewById(R.id.btn_isMultiCulture_false);

        RG_is_disabled = (RadioGroup)findViewById(R.id.isDisabledRadioGroup);
        rb_is_disabled_true = (RadioButton)findViewById(R.id.btn_isDisabled_true);
        rb_is_disabled_false = (RadioButton)findViewById(R.id.btn_isDisabled_false);

        RG_family_state = (RadioGroup)findViewById(R.id.familyStateRadioGroup);
        rb_family_state_true = (RadioButton)findViewById(R.id.btn_family_state_true);
        rb_family_state_false = (RadioButton)findViewById(R.id.btn_family_state_false);

        RG_interest = (RadioGroup)findViewById(R.id.interestRadioGroup);
        rb_interest0 = (RadioButton)findViewById(R.id.rb_interest0);
        rb_interest1 = (RadioButton)findViewById(R.id.rb_interest1);
        rb_interest2 = (RadioButton)findViewById(R.id.rb_interest2);
        rb_interest3 = (RadioButton)findViewById(R.id.rb_interest3);
        rb_interest4 = (RadioButton)findViewById(R.id.rb_interest4);
        rb_interest5 = (RadioButton)findViewById(R.id.rb_interest5);
        rb_interest6 = (RadioButton)findViewById(R.id.rb_interest6);

        spinner_life_cycle = (Spinner)findViewById(R.id.spinner_life_cycle);

        // life_cycle with spinner
        ArrayAdapter<String> life_cycle_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, life_cycle_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_life_cycle.setAdapter(life_cycle_adapter);
        spinner_life_cycle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                life_cycle = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                life_cycle = -1;
            }
        });

        try{
            fetchData(token);
        }
        catch(Exception err){
            Log.v("Exception :toke / fetchData Error", err.getMessage());
        }

        // btn_back button listener
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        // photo uploaded
//        img_view = (ImageView)findViewById(R.id.img_view);
//        img_view.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });

        // transfer user data to EditText data
        Button btn_edit = (Button)findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String user_name = et_username.getText().toString().trim();
                String user_id = et_id.getText().toString().trim();
                String user_password = et_pwd.getText().toString().trim();

                // gender preprocessing
                if(RG_gender.getCheckedRadioButtonId() == rb_male.getId()){
                    Log.v("rb_male is selected? : ", "true");
                    Gender = 1;
                }
                else if(RG_gender.getCheckedRadioButtonId() == rb_female.getId()) {
                    Log.v("rb_female is selected? : ", "true");
                    Gender = 0;
                }

                int user_gender = Gender;

                Income = spinner_income.getSelectedItemPosition();

                int user_income = Income;

                // address
                City = spinner_address_city.getSelectedItem().toString();
                Local = spinner_address_gu.getSelectedItem().toString();
                Address = City +"-"+ Local;

                String user_address = Address;

                if(RG_is_multicultural.getCheckedRadioButtonId() == rb_is_multicultural_true.getId()){
                    is_multicultural = 1;
                }
                else if(RG_is_multicultural.getCheckedRadioButtonId() == rb_is_multicultural_false.getId()){
                    is_multicultural = 0;
                }

                int user_is_multicultural = is_multicultural;

                if(RG_is_disabled.getCheckedRadioButtonId() == rb_is_disabled_true.getId()){
                    is_disabled = 1;
                }
                else if(RG_is_disabled.getCheckedRadioButtonId() == rb_is_disabled_false.getId()) {
                    is_disabled = 0;
                }

                int user_is_disabled = is_disabled;

                if(RG_family_state.getCheckedRadioButtonId() == rb_family_state_true.getId()){
                    family_state = 1;
                }
                else if(RG_family_state.getCheckedRadioButtonId() == rb_family_state_false.getId()) {
                    family_state = 0;
                }

                int user_is_one_parent = family_state;

                life_cycle = spinner_life_cycle.getSelectedItemPosition();

                int user_life_cycle = life_cycle;

                if(RG_interest.getCheckedRadioButtonId() == rb_interest0.getId()){
                    interest = 0;
                }
                else if(RG_interest.getCheckedRadioButtonId() == rb_interest1.getId()){
                    interest = 1;
                }
                else if(RG_interest.getCheckedRadioButtonId() == rb_interest2.getId()){
                    interest = 2;
                }
                else if(RG_interest.getCheckedRadioButtonId() == rb_interest3.getId()){
                    interest = 3;
                }
                else if(RG_interest.getCheckedRadioButtonId() == rb_interest4.getId()){
                    interest = 4;
                }
                else if(RG_interest.getCheckedRadioButtonId() == rb_interest5.getId()){
                    interest = 5;
                }
                else if(RG_interest.getCheckedRadioButtonId() == rb_interest6.getId()){
                    interest = 6;
                }

                int user_interest = interest;

                editUserInfo(user_name, user_id, user_password, user_gender,
                        user_income, user_address, user_life_cycle, user_is_multicultural,
                        user_is_one_parent, user_is_disabled, user_interest);

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchData(String token){

        // read user data
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        JSONObject params = new JSONObject();

        try{
            params.put("token", token);
            Log.v("ProfileActivity params complete: ", "true");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.url_read, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.v("fetchData response",  "true");
                try{
                    Log.v("fetchData response", response.toString());

                    String token = response.getString("token");
                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");

                    Log.v("fetchData response token", token);
                    Log.v("fetchData response isSuccess", isSuccess.toString());
                    Log.v("fetchData response statusCode", Integer.toString(statusCode));

                    String user_name = response.getString("user_name");
                    String user_id = response.getString("user_id");
                    String user_password = response.getString("user_password");
                    int user_gender = response.getInt("user_gender");
                    int user_income = response.getInt("user_income");
                    String user_address = response.getString("user_address");
                    int user_life_cycle = response.getInt("user_life_cycle");
                    int user_is_multicultural = response.getInt("user_is_multicultural");
                    int user_is_one_parent = response.getInt("user_is_one_parent");
                    int user_is_disabled = response.getInt("user_is_disabled");
                    int user_interest = response.getInt("user_interest");

                    et_username.setText(user_name);
                    et_id.setText(user_id);
                    et_pwd.setText(user_password);

//                    if(user_gender == 1){
//                        rb_male.setChecked(true);
//                    }
//                    else{
//                        rb_female.setChecked(true);
//                    }
//                    spinner_income.setSelection(user_income, true);
//
//                    spinner_life_cycle.setSelection(user_life_cycle, true);
//                    spinner_address_city.setSelection(0, true);
//                    spinner_address_city.setSelection(0, true);
//
//                    if(user_is_multicultural == 1){
//                        rb_is_multicultural_true.setChecked(true);
//                    }
//                    else{
//                        rb_is_multicultural_false.setChecked(true);
//                    }
//
//                    if(user_is_one_parent == 1){
//                        rb_family_state_true.setChecked(true);
//                    }
//                    else{
//                        rb_family_state_false.setChecked(true);
//                    }
//                    if(user_is_disabled == 1){
//                        rb_is_disabled_true.setChecked(true);
//                    }
//                    else{
//                        rb_is_disabled_false.setChecked(true);
//                    }

                }
                catch(JSONException e){
                    e.printStackTrace();
                    Log.v("JSONException :", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.v("fetchData request error", error.getMessage());
                    //Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        Log.v("jsonRequest", jsonRequest.toString());
        Log.v("jsonRequest url", jsonRequest.getUrl());

        VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);

    };

    // function for edit button click
    private void editUserInfo(String user_name, String user_id, String user_password, int user_gender,
                              int user_income, String user_address, int user_life_cycle, int user_is_multicultural,
                              int user_is_one_parent, int user_is_disabled, int user_interest){

        Bundle bundle = (Bundle) getIntent().getExtras();
        String token = bundle.getString("token");

        // log list for variable request check
        Log.v("user_name_check", "user_name: " + user_name);
        Log.v("user_id_check","user_id: " + user_id);
        Log.v("user_password_check","user_password: " + user_password);
        Log.v("user_gender_check", "user_gender: " + user_gender);
        Log.v("user_income_check", "user_income: " + user_income);
        Log.v("user_address_check", "user_address: " + user_address);
        Log.v("user_life_cycle_check", "user_life_cycle: " + user_life_cycle);
        Log.v("user_is_multicultural_check", "user_is_multicultural: " + user_is_multicultural);
        Log.v("user_is_one_parent_check", "user_is_one_parent: " + user_is_one_parent);
        Log.v("user_is_disabled_check", "user_is_disabled: " + user_is_disabled);
        Log.v("user_interest_check", "user_interest: " + user_interest);

        // Register Request
        JSONObject params = new JSONObject();

        try{
            params.put("user_id", user_id);
            params.put("user_password", user_password);
            params.put("user_name", user_name);
            params.put("user_gender", user_gender);
            params.put("user_income", user_income);
            params.put("user_address",user_address);
            params.put("user_life_cycle", user_life_cycle);
            params.put("user_is_multicultural", user_is_multicultural);
            params.put("user_is_one_parent", user_is_one_parent);
            params.put("user_is_disabled", user_is_disabled);
            params.put("user_interest", user_interest);
            params.put("token", token);
            params.put("token_firebase", "");
            Log.v("editUserInfo params added","success");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, URLs.url_update, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_SHORT).show();

                    Boolean isSuccess = response.getBoolean("success");
                    int statusCode = response.getInt("statusCode");
                    String token_response = response.getString("token");

                    Log.v("on edit  response isSuccess", isSuccess.toString());
                    Log.v("on edit  response statusCode", Integer.toString(statusCode));
                    Log.v("on edit  response token", token_response);

                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);
        Toast.makeText(getApplicationContext(), "개인 정보수정에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
    };

//    private void takeAlbumAction(){
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
//    };
//
//    private void cropImage(){
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//        cropIntent.setDataAndType(photoURI, "image/*");
//        cropIntent.putExtra("scale", true);
//        if(!album){
//            cropIntent.putExtra("output", photoURI);
//        }
//        else if(album){
//            cropIntent.putExtra("output", albumURI);
//        }
//        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
//    }
}