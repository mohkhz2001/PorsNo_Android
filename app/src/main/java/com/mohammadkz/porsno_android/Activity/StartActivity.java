package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    Button login, signUp;
    RelativeLayout root;
    String pn, pwd;
    ApiConfig request;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);

            initViews();
            controllerViews();

            if (autoLogin()) {
                request = AppConfig.getRetrofit().create(ApiConfig.class);
                getData_SharedPreferences();
            } else {
                login.setVisibility(View.VISIBLE);
                root.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            StaticFun.setLog((user == null) ? (pn != null && pn.length() > 0 ? pn : "-")
                    : (user.getPn().length() > 0 ? user.getPn() : (pn.length() > 0 ? pn.toString() : "-")), e.getMessage().toString(), "login - create");
        }
    }

    private void initViews() {
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        root = findViewById(R.id.root);
    }

    private void controllerViews() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private boolean autoLogin() {
        SharedPreferences sh = getSharedPreferences("userLogin_info", MODE_PRIVATE);
        String a = sh.getString("userLogin_info", "");

        if (a.length() > 0) {
            return true;
        } else {
            return false;
        }

    }

    private void getData_SharedPreferences() {
        SharedPreferences sh = getSharedPreferences("userLogin_info", MODE_PRIVATE);
        String a = sh.getString("userLogin_info", "");
        if (!a.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(a);
                pn = jsonObject.getString("pn");
                pwd = jsonObject.getString("pwd");
                login();
            } catch (JSONException e) {
                e.printStackTrace();
                StaticFun.setLog(pn != null ? pn : "-", e.getMessage().toString(), "start activity-get data");
                root.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
            }
        }
    }

    private void login() {

        Call<LoginResponse> get = request.loginResponse(pn, pwd);

        get.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getStatus_code().equals("200")) {

                    setUser(response.body());
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    transferData(intent);
                    start(intent);

                } else {
                    root.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.getMessage();
                root.setVisibility(View.GONE);
                StaticFun.setLog(pn, t.getMessage().toString(), "start activity - api / on fail");

            }
        });

    }

    private void transferData(Intent intent) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        intent.putExtra("userInfo", json);

    }

    private void setUser(LoginResponse loginResponse) {

        user = new User();
        user.setName(loginResponse.getUserName());
        user.setPn(loginResponse.getPhoneNumber());
        user.setCreatedTime(loginResponse.getCreated());
        user.setEndTime(loginResponse.getEnd());
        user.setID(loginResponse.getId());
        user.setQuestionRemaining(loginResponse.getQuestionRemaining());

        if (loginResponse.getBirthday() != null) {
            user.setBirthdayDate(loginResponse.getBirthday());
        }

        if (loginResponse.getAccountLevel().equals("bronze")) {
            user.setAccountLevel(StaticFun.account.Bronze);
        } else if (loginResponse.getAccountLevel().equals("steel")) {
            user.setAccountLevel(StaticFun.account.Steel);
        } else if (loginResponse.getAccountLevel().equals("gold")) {
            user.setAccountLevel(StaticFun.account.Gold);
        } else if (loginResponse.getAccountLevel().equals("diamond")) {
            user.setAccountLevel(StaticFun.account.Diamond);
        }

    }

    private void start(Intent intent) {
        startActivity(intent);
        finish();
    }
}