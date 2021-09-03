package com.mohammadkz.porsno_android.Notification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Activity.LoginActivity;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHandlerActivity extends AppCompatActivity {

    String phoneNumber, pwd;
    ApiConfig request;
    User user;
    String qId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notification_handler);

        if (!getIntent().getStringExtra("qId").isEmpty()) {
            qId = getIntent().getStringExtra("qId");
            getData_SharedPreferences();
        } else {
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }

    private void getData_SharedPreferences() {
        SharedPreferences sh = getSharedPreferences("userLogin_info", MODE_PRIVATE);
        String a = sh.getString("userLogin_info", "");
        if (!a.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(a);
                phoneNumber = (jsonObject.getString("pn"));
                pwd = (jsonObject.getString("pwd"));
                login();
            } catch (JSONException e) {
                e.printStackTrace();
                StaticFun.setLog(phoneNumber.toString(), e.getMessage().toString(), "push notification - get data");
            }
        } else {
            saveQID();
        }
    }

    private void login() {

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        String pass;

        pass = pwd.toString();

        Call<LoginResponse> get = request.loginResponse(phoneNumber.toString(), pass);

        get.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    user = new User();

                    user.setName(response.body().getUserName());
                    user.setID(response.body().getId());
                    AnswerActivity();
                } else {
                    saveQID();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.getMessage();
                StaticFun.setLog(phoneNumber.toString(), t.getMessage().toString(), "push notification - login");
                saveQID();
            }
        });

    }

    private void AnswerActivity() {
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra("qId", qId);
        intent.putExtra("user-name", user.getName());
        intent.putExtra("user-id", user.getID());
        finish();
        startActivity(intent);
    }

    private void saveQID() {
        SharedPreferences sh = getSharedPreferences("question-queue", MODE_PRIVATE);
        String a = sh.getString("queue", "");

        SharedPreferences.Editor editor = sh.edit();
        editor.clear();
        editor.commit();

        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.clear();
        myEdit.putString("queue", qId);
        myEdit.commit();

        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}