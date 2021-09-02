package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.Response.CheckPhoneResponse;
import com.mohammadkz.porsno_android.Model.Response.SMSResponse;
import com.mohammadkz.porsno_android.Model.Response.SignUpResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPhoneNumberActivity extends AppCompatActivity {
    EditText code1, code2, code3, code4;
    TextView reSend, second, minute;
    User user;
    Button complete;
    String codeToConfirm;
    int timer = 120, m = 0;

    ApiConfig request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_confirm_phone_number);

            SweetDialog.setSweetDialog(new SweetAlertDialog(ConfirmPhoneNumberActivity.this, SweetAlertDialog.PROGRESS_TYPE));

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            initViews();
            controllerViews();
            flipTimer();
            getDate();
        } catch (Exception e) {
            Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "Answer Activity - create");
        }

    }

    private void initViews() {
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        reSend = findViewById(R.id.reSend);
        reSend.setEnabled(false);
        minute = findViewById(R.id.minute);
        second = findViewById(R.id.second);
        complete = findViewById(R.id.complete);
    }

    private void controllerViews() {
        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(getApplicationContext() , (int)s)
                if (code1.length() == 1) {
                    code1.clearFocus();
                    code2.requestFocus();
                    code2.setCursorVisible(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code1.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //on backspace
                code1.setText("");

            }
            return false;
        });

        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (code2.length() == 1) {
                    code2.clearFocus();
                    code3.requestFocus();
                    code3.setCursorVisible(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code2.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //on backspace
                if (code2.length() == 0) {
                    code2.clearFocus();
                    code1.requestFocus();
                    code1.setCursorVisible(true);
                } else {
                    code2.setText(null);
                }

            }

            return false;
        });

        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (code3.length() == 1) {
                    code3.clearFocus();
                    code4.requestFocus();
                    code4.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code3.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //on backspace
                if (code3.length() == 0) {
                    code3.clearFocus();
                    code2.requestFocus();
                    code2.setCursorVisible(true);
                } else {
                    code3.setText(null);
                }

            }

            return false;
        });

        code4.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //on backspace
                if (code4.length() == 0) {
                    code4.clearFocus();
                    code3.requestFocus();
                    code3.setCursorVisible(true);
                } else {
                    code4.setText(null);
                }

            }

            return false;
        });

        reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetDialog.setSweetDialog(new SweetAlertDialog(ConfirmPhoneNumberActivity.this, SweetAlertDialog.PROGRESS_TYPE));
                SweetDialog.startProgress();
                flipTimer();
                requestCode();
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetDialog.setSweetDialog(new SweetAlertDialog(ConfirmPhoneNumberActivity.this, SweetAlertDialog.PROGRESS_TYPE), "لطفا منتظر باشید...", "");
                SweetDialog.startProgress();
                String code = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + "";

                if (StaticFun.isNetworkAvailable(ConfirmPhoneNumberActivity.this)) {
                    if (codeToConfirm.equals(code)) {

                        user.setCreatedTime(setCreatedTime());
                        completeRegister();

                    } else {
                        SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "", "کد وارد شده صحیح نمی باشد.");
                    }
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "", "لطفا ارتباط خود با اینترنت را بررسی نمایید.");
                }

            }
        });
    }

    // get data from sign up page(intent)
    private void getDate() {
        String data = getIntent().getStringExtra("signUp");
        Log.e("user", " " + data);
        if (data != null)
            try {
                JSONObject jsonObject = new JSONObject(data);

                user = new User();
                user.setPwd(jsonObject.getString("pwd"));
                user.setPn(jsonObject.getString("pn"));
                user.setName(jsonObject.getString("name"));

                requestCode();
            } catch (Exception e) {
                e.printStackTrace();
                Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "confirm phone Activity - get data");
            }
    }

    private void requestCode() {
        SweetDialog.startProgress();
        Call<SMSResponse> get = request.SendSMS(user.getPn());

        get.enqueue(new Callback<SMSResponse>() {
            @Override
            public void onResponse(Call<SMSResponse> call, Response<SMSResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    codeToConfirm = response.body().getCode();
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "پیامک حاوی کد تایید با موفقیت ارسال شد.", "");
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
                }
            }

            @Override
            public void onFailure(Call<SMSResponse> call, Throwable t) {
                t.getMessage();
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
            }
        });
    }

    private void completeRegister() {
        user.setPwd(StaticFun.md5(user.getPwd()));
        Log.e("hash", user.getPwd());
        Call<SignUpResponse> get = request.SignUp(user.getPn(), user.getPwd(), user.getName(), user.getCreatedTime());

        get.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    setUser(response.body());
                    setData_SharedPreferences();
                    start();
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            response.body().getMessage().length() > 0 ? response.body().getMessage() + " - " + response.body().getStatus_code() : " - "
                            , "confirm phone Activity - complete reg / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
                Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), t.getMessage().toString(), "confirm phone Activity - complete reg / failure");
            }
        });
    }

    private void setUser(SignUpResponse response) {
        user.setID(response.getUserId());
        user.setAccountLevel(StaticFun.account.Bronze);
        user.setEndTime(response.getEnd());
        user.setBirthdayDate("");
        user.setQuestionRemaining(response.getQuestionRemaining());

    }

    private String setCreatedTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp.getTime());
        Log.e("test time stamp", " " + timestamp.getTime() / 1000);
        return timestamp.getTime() / 1000 + "";
    }

    private void start() {
        Intent intent = new Intent(this, MainPageActivity.class);
        transferData(intent);
        SweetDialog.stopProgress();
        startActivity(intent);
        finish();
    }

    private void transferData(Intent intent) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        intent.putExtra("userInfo", json);

    }

    private void setData_SharedPreferences() {
        SharedPreferences sh = getSharedPreferences("userLogin_info", MODE_PRIVATE);

        SharedPreferences.Editor editor = sh.edit();
        editor.clear();
        editor.commit();

        Gson gson = new Gson();
        String userToSave = gson.toJson(user);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.clear();
        myEdit.putString("userLogin_info", userToSave);
        myEdit.commit();
    }

    // countdown timer ==> 2 min
    private void flipTimer() {
        reSend.setEnabled(false);
        timer = 120;
        CountDownTimer countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                m = 0;
                int time = timer--;
                if (time <= 60) {
                    m = 0;
                } else
                    while (time >= 60) {
                        m++;
                        time -= 60;
                    }

                minute.setText(String.valueOf(m));
                if (time < 10)
                    second.setText("0" + time);
                else
                    second.setText(String.valueOf(time));
            }

            @Override
            public void onFinish() {
                minute.setText(String.valueOf(00));
                second.setText(String.valueOf(00));
                reSend.setEnabled(true);
            }
        }.start();

    }
}