package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

public class WebLinkActivity extends AppCompatActivity {

    String qId;
    ApiConfig request;
    User user;
    String phoneNumber, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {

            Log.e("length", uri.toString().length() + "");

            qId = uri.toString().substring(47);
            getData_SharedPreferences();

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
                StaticFun.setLog((user == null) ? (phoneNumber != null && phoneNumber.length() > 0 ? phoneNumber.toString() : "-")
                        : (user.getPn().length() > 0 ? user.getPn() : (phoneNumber.length() > 0 ? phoneNumber.toString() : "-")), e.getMessage().toString(), "login");
            }
        } else {
            saveQID();
        }
    }

    private void login() {

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        String pass;


        SweetDialog.setSweetDialog(new SweetAlertDialog(WebLinkActivity.this, SweetAlertDialog.PROGRESS_TYPE), "در حال ورود", "لطفا منتظر باشید...");
        SweetDialog.getSweetAlertDialog().setCancelable(false);
        SweetDialog.startProgress();
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

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.getMessage();
                StaticFun.setLog(phoneNumber.toString(), t.getMessage().toString(), "Login - api");

            }
        });

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

    private void AnswerActivity() {
        finish();
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra("qId", qId);
        intent.putExtra("user-name", user.getName());
        intent.putExtra("user-id", user.getID());
        startActivity(intent);
    }
}