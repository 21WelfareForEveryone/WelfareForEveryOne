package com.product.welfareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

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

    int img_idx;

    // Firebase Push Notification
    private static final String TAG = "RegisterActivity Firebase Push Token Process";

    // User ImageView
    ImageView user_img_view;

    // back button listener
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase App Process to get token(11.27 added)
        FirebaseApp.initializeApp(getApplicationContext());
        try{
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            String token = task.getResult();
                            String msg = getString(R.string.msg_token_fmt, token);

                            SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                            SharedPreferences.Editor editor= sharedPreferences.edit();
                            editor.putString("token_firebase", token);
                            editor.commit();

                            Log.d(TAG, msg);
                            Log.v(TAG, msg);
                        }
                    });
        }
        catch(Exception err){
            err.printStackTrace();
            Log.v("FirebaseApp error on RegisterActivity", err.getMessage());
        }

        // User Image Selection
        // User Image Dialog
        user_img_view = (ImageView)findViewById(R.id.iv_user_img);
        ImageView iv_photo_add = (ImageView)findViewById(R.id.iv_photo_add);
        iv_photo_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        RegisterActivity.this, R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.user_img_select_dialog,
                                (LinearLayout)findViewById(R.id.profileImageContainer)
                        );

                RoundedImageView image_baby = bottomSheetView.findViewById(R.id.image_baby);
                image_baby.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 0;
                        user_img_view.setImageResource(R.drawable.baby);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_child = bottomSheetView.findViewById(R.id.image_child);
                image_child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 1;
                        user_img_view.setImageResource(R.drawable.children);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_woman = bottomSheetView.findViewById(R.id.image_woman);
                image_woman.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 2;
                        user_img_view.setImageResource(R.drawable.woman);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_man = bottomSheetView.findViewById(R.id.image_man);
                image_man.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 3;
                        user_img_view.setImageResource(R.drawable.man);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_old_woman = bottomSheetView.findViewById(R.id.image_old_woman);
                image_old_woman.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 4;
                        user_img_view.setImageResource(R.drawable.old_woman);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_old_man = bottomSheetView.findViewById(R.id.image_old_man);
                image_old_man.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 5;
                        user_img_view.setImageResource(R.drawable.old_man);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_pregnant = bottomSheetView.findViewById(R.id.image_pregnant);
                image_pregnant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 6;
                        user_img_view.setImageResource(R.drawable.pregnant);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_multicultural = bottomSheetView.findViewById(R.id.image_multicultural);
                image_multicultural.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 7;
                        user_img_view.setImageResource(R.drawable.multicultural);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                RoundedImageView image_disabled = bottomSheetView.findViewById(R.id.image_disabled);
                image_disabled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RegisterActivity.this, "선택한 이미지를 프로필로 설정합니다", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        img_idx = 8;
                        user_img_view.setImageResource(R.drawable.disabled);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });


        // agreement
        //전체동의
        CheckBox checkBox=findViewById(R.id.checkBox);
        CheckBox checkBox2=findViewById(R.id.checkBox2);
        CheckBox checkBox3=findViewById(R.id.checkBox3);
        CheckBox checkBox4=findViewById(R.id.checkBox4);

        TextView checkBoxTv2 = findViewById(R.id.checkBox2Tv);
        TextView checkBoxTv3 = findViewById(R.id.checkBox3Tv);
        TextView checkBoxTv4 = findViewById(R.id.checkBox4Tv);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox4.setChecked(true);
                }else {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                }
            }
        });

        checkBoxTv2.setText(Html.fromHtml( "<a href='https://welfareforeveryone.notion.site/12cfd3e97bbc43419596de64138192c3'>[필수] 서비스 이용 약관 </a>"));
        checkBoxTv2.setClickable(true);
        checkBoxTv2.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //만약 전체 클릭이 true 라면 false로 변경
                v.cancelPendingInputEvents();
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                    //각 체크박스 체크 여부 확인해서  전체동의 체크박스 변경
                }else if(checkBox2.isChecked()&&checkBox3.isChecked()&&checkBox4.isChecked()){
                    checkBox.setChecked(true);
                }
            }
        });

        checkBoxTv3.setText(Html.fromHtml( "<a href='https://welfareforeveryone.notion.site/c2797e0445e947808369acf5702cffa0'>[필수] 개인정보 처리방침</a>"));
        checkBoxTv3.setClickable(true);
        checkBoxTv3.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.cancelPendingInputEvents();
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                }else if(checkBox2.isChecked()&&checkBox3.isChecked()&&checkBox4.isChecked()){
                    checkBox.setChecked(true);
                }
            }
        });


        checkBoxTv4.setText(Html.fromHtml( "<a href='https://welfareforeveryone.notion.site/5011d8fa8f9441bfb22fc85d95f45178 '>[필수] 위치정보 이용약관</a>"));
        checkBoxTv4.setClickable(true);
        checkBoxTv4.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                }else if(checkBox2.isChecked()&&checkBox3.isChecked()&&checkBox4.isChecked()){
                    checkBox.setChecked(true);
                }
            }
        });

        // manage user variable
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

        Button btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
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

                /* 파이어베이스 토큰 추가 (11.27) */
                SharedPreferences sharedPreferences= getSharedPreferences("user_info", MODE_PRIVATE);
                String token_firebase = sharedPreferences.getString("token_firebase", "");
                Log.v("RegisterActivity token_firebase", token_firebase);

                /* 유효성 검사 */
                if(!checkBox.isChecked()){
                    Toast.makeText(getApplicationContext(), "체크리스트에 모두 동의해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(user_name, user_id, user_password, user_gender,
                        user_income, user_address, user_life_cycle, user_is_multicultural,
                        user_is_one_parent, user_is_disabled, user_interest, token_firebase, img_idx, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                SharedPreferences sharedPreferences= getSharedPreferences("user_info", MODE_PRIVATE);
                                Boolean isSuccess  = sharedPreferences.getBoolean("success", false);
                                String mToken = sharedPreferences.getString("token", "");
                                int statusCode = sharedPreferences.getInt("statusCode",0);

                                if(isSuccess){
                                    Log.v("Register success", isSuccess.toString());
                                    Toast.makeText(getApplicationContext(), "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("token", mToken);
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    if(bundle!=null){
                                        intent.putExtras(bundle);
                                    }
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다..", Toast.LENGTH_SHORT).show();
                                    Log.v("Register Process","failed");
                                }
                            }
                        });
            }
        });

        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public interface VolleyCallBack {
        void onSuccess();
    }

    private void registerUser(String user_name, String user_id, String user_password, int user_gender,
                              int user_income, String user_address, int user_life_cycle, int user_is_multicultural,
                              int user_is_one_parent, int user_is_disabled, int user_interest, String token_firebase, int img_idx, final VolleyCallBack volleyCallBack){

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
        Log.v("user_token_firebase", "token_firebase: " + token_firebase);

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
            params.put("token_firebase", token_firebase);
            params.put("img_idx", img_idx);

            Log.v("Register params input process","success");
        }
        catch(JSONException e){
            e.printStackTrace();
            Log.v("Register params input process","failed");
            return;
        }

        Log.v("RegisterActivity params", params.toString());

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.url_register, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("register onResponse", "true");
                try{

                    Boolean isSuccess = response.getBoolean("success");
                    String token = response.getString("token");
                    int statusCode =  response.getInt("statusCode");

                    editor.putString("token", token);
                    editor.putInt("statusCode", statusCode);
                    editor.putBoolean("success", isSuccess);
                    editor.commit();
                    volleyCallBack.onSuccess();

                    Log.v("on register response isSuccess: ", isSuccess.toString());
                    Log.v("on register response statusCode : ", Integer.toString(statusCode));
                    Log.v("on register response mToken: ", token);

                }
                catch(JSONException e){
                    Log.v("register - response error : ", e.getMessage());
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("Register onResponse", "false");
                        Log.v("Register onResponse Error", error.toString());
                    }
                }){
        };

        Log.v("register process -- jsonRequest", jsonRequest.toString());
        Log.v("register process -- jsonRequest url: ", jsonRequest.getUrl());
        AppHelper.requestQueue.add(jsonRequest);
    }
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else {
            Toast.makeText(getBaseContext(), "뒤로가기 버튼을 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}