package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        SweetDialog.setSweetDialog(new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE));

        initViews();
        controllerViews();

        if (autoLogin()) {
            getData_SharedPreferences();
        } else {
//            SweetDialog.stopProgress();
            root.setVisibility(View.VISIBLE);
        }

    }

    private void initViews() {
        pwd = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phoneNumber);
        signUp = findViewById(R.id.signUp);
        forgotPWD = findViewById(R.id.forgotPWD);
        login = findViewById(R.id.login);
        root = findViewById(R.id.root);
    }

    private void controllerViews() {
//        phoneNumber.setText("09388209270");
//        pwd.setText("m9873110");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetDialog.setSweetDialog(new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE), "در حال ورود", "لطفا منتظر باشید...");
                SweetDialog.startProgress();
                if (StaticFun.isNetworkAvailable(getApplicationContext())) {
                    if (checkValue()) {
                        login(false);
                    } else {
                        SweetDialog.startProgress();
                        SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "", "تمامی موارد خواسته شده را وارد نمایید!");
                        Toasty.error(getApplicationContext(), "تمامی موارد خواسته شده را وارد نمایید!", Toasty.LENGTH_LONG).show();
                    }
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "", "لطفا ارتباط خود با اینترنت را بررسی نمایید.");
                }

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

        SweetDialog.getSweetAlertDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                root.setVisibility(View.VISIBLE);

            }
        });
    }

    private boolean checkValue() {
        if (phoneNumber.getText().length() > 0 && pwd.getText().length() > 0)
            return true;
        else
            return false;
    }

    private void login(boolean shared) {

        String pass;

        if (shared) {
            SweetDialog.setSweetDialog(new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE), "در حال ورود", "لطفا منتظر باشید...");
            SweetDialog.getSweetAlertDialog().setCancelable(false);
            SweetDialog.startProgress();
            pass = pwd.getText().toString();
        } else {
            pass = StaticFun.md5(pwd.getText().toString());
        }

        Call<LoginResponse> get = request.loginResponse(phoneNumber.getText().toString(), pass);

        get.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getStatus_code().equals("200")) {

                    if (!shared) {
                        setData_SharedPreferences(pass);
                    }

                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "خوش آمدید", "ورود با موفقیت انجام شد");
                    setUser(response.body());
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    transferData(intent);
                    start(intent);
                } else {
                    if (!shared) {
                        SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در ورود", "نام کاربری یا رمز عبور اشتباه است");
                    } else {
                        SweetDialog.stopProgress();
                        root.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (shared) {
                    t.getMessage();
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
                    root.setVisibility(View.VISIBLE);
                } else {
                    root.setVisibility(View.VISIBLE);
                }

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
                phoneNumber.setText(jsonObject.getString("pn"));
                pwd.setText(jsonObject.getString("pwd"));
                login(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setData_SharedPreferences(String pass) {
        SharedPreferences sh = getSharedPreferences("userLogin_info", MODE_PRIVATE);
        Gson gson = new Gson();
        User user = new User();
        user.setPwd(pass);
        user.setPn(phoneNumber.getText().toString());
        String userToSave = gson.toJson(user);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.clear();
        myEdit.putString("userLogin_info", userToSave);
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
        SweetDialog.stopProgress();
        startActivity(intent);
        finish();
    }


}