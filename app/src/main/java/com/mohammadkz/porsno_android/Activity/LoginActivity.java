package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText pwd, phoneNumber;
    TextView signUp, forgotPWD;
    Button login;
    ApiConfig request;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        initViews();
        controllerViews();
    }

    private void initViews() {
        pwd = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phoneNumber);
        signUp = findViewById(R.id.signUp);
        forgotPWD = findViewById(R.id.forgotPWD);
        login = findViewById(R.id.login);
    }

    private void controllerViews() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValue())
                    login();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        forgotPWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(getApplicationContext(), "forgot pwd clicked", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkValue() {
        if (phoneNumber.getText().length() > 0 && pwd.getText().length() > 0)
            return true;
        else
            return false;
    }

    private void login() {
        String pass = StaticFun.md5(pwd.getText().toString());
        Call<LoginResponse> get = request.loginResponse(phoneNumber.getText().toString(), pass);

        get.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    autoLogin(pass);
                    setUser(response.body());
                    Intent intent = new Intent();
                    transferData(intent);
                    start(intent);
                } else {
                    StaticFun.alertDialog_connectionFail(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.getMessage();
                StaticFun.alertDialog_connectionFail(getApplicationContext());
            }
        });

    }

    private void autoLogin(String pass) {
        SharedPreferences sh = getSharedPreferences("userLogin", MODE_PRIVATE);
        Gson gson = new Gson();
        User user = new User();
        user.setPwd(pass);
        user.setPn(phoneNumber.getText().toString());
        String userToSave = gson.toJson(user);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.clear();
        myEdit.putString("userLogin", userToSave);
        myEdit.commit();
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

//        if (loginResponse.getAccountLevel().equals(""))
//            user.setAccountLevel();
    }

    private void start(Intent intent) {
        startActivity(intent);
    }

}