package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
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

import java.sql.Timestamp;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    EditText pwd, name, phoneNumber;
    Button sendSMS;
    ApiConfig request;
    TextView privacy;
    BottomSheetDialog bottomSheetDialog_confirm;
    int timer = 120, m = 0;
    String codeToConfirm;
    TextView minute, second, reSend;
    User user;
    ImageView back, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            SweetDialog.setSweetDialog(new SweetAlertDialog(SignUp.this, SweetAlertDialog.PROGRESS_TYPE));

            initViews();
            bottomSheetConfirmPhone();
            controllerViews();

        } catch (Exception e) {
            Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog(phoneNumber.getText() != null ? (phoneNumber.getText().length() > 0 ? phoneNumber.getText().toString() : "-") : "-"
                    , e.getMessage().toString()
                    , "signup Activity - create");
        }

    }

    private void initViews() {
        pwd = findViewById(R.id.password);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        sendSMS = findViewById(R.id.send);
        privacy = findViewById(R.id.privacy);
        info = findViewById(R.id.info);
        back = findViewById(R.id.back);
    }

    private void controllerViews() {
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetDialog.setSweetDialog(new SweetAlertDialog(SignUp.this, SweetAlertDialog.PROGRESS_TYPE), "لطفا منتظر باشید...", "در حال دریافت اطلاعات");
                SweetDialog.startProgress();
                if (StaticFun.isNetworkAvailable(getApplicationContext())) {
                    if (checkValue()) {
                        checkPN();
                    } else {
                        SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "", "تمامی موارد خواسته شده را وارد نمایید!");
                    }
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "", "لطفا ارتباط خود با اینترنت را بررسی نمایید.");
                }

            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetPrivacy();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void checkPN() {
        Call<CheckPhoneResponse> get = request.checkPhoneNumber(phoneNumber.getText().toString());

        get.enqueue(new Callback<CheckPhoneResponse>() {
            @Override
            public void onResponse(Call<CheckPhoneResponse> call, Response<CheckPhoneResponse> response) {
                if (response.body().getStatus_code().equals("200")) {//
//                    requestCode();
                    requestCode();
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "پیامک حاوی کد تایید با موفقیت ارسال شد.", "");
                    if (!bottomSheetDialog_confirm.isShowing())
                        bottomSheetDialog_confirm.show();
                    flipTimer();

                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "", "کاربری قبلا با این مشخصات ثبت نام کرده است.");
                }
            }

            @Override
            public void onFailure(Call<CheckPhoneResponse> call, Throwable t) {
                t.getMessage();
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
            }
        });

    }

    private boolean checkValue() {
        if ((name.getText().length() > 0 && pwd.getText().length() > 0 && phoneNumber.getText().length() > 0)) {
            return true;
        } else
            return false;
    }

    private void bottomSheetPrivacy() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SignUp.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(SignUp.this).inflate(R.layout.layout_bottom_sheet_privacy, (ConstraintLayout) findViewById(R.id.layout));

        bottomSheetView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void bottomSheetConfirmPhone() {
        bottomSheetDialog_confirm = new BottomSheetDialog(SignUp.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog_confirm.setCancelable(false);
        View bottomSheetView = LayoutInflater.from(SignUp.this).inflate(R.layout.layout_bottom_sheet_confirm_phone_number, (ConstraintLayout) findViewById(R.id.layout));

        TextInputEditText code = bottomSheetView.findViewById(R.id.code);

        minute = bottomSheetView.findViewById(R.id.minute);
        second = bottomSheetView.findViewById(R.id.second);
        reSend = bottomSheetView.findViewById(R.id.reSend);

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if (code.getText().toString().length() == 4 && code.getText().equals(codeToConfirm)) {
//                    completeRegister();
//                } else if (!code.getText().equals(codeToConfirm)) {
////                    Toasty.error(SignUp.this, "کد وارد شده صحیح نمی باشد.", Toasty.LENGTH_LONG, true).show();
//                }
            }
        });

        reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCode();
            }
        });

        bottomSheetView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("code", codeToConfirm);
                Log.d("code--", "11");
                Log.d("code", code.getText().toString());
                if (code.getText().toString().length() == 4 && code.getText().toString().equals(codeToConfirm)) {
                    completeRegister();
                } else if (!code.getText().toString().equals(codeToConfirm)) {
                    Toasty.error(SignUp.this, "کد وارد شده صحیح نمی باشد.", Toasty.LENGTH_LONG, true).show();
                } else
                    Toasty.error(SignUp.this, "کد تایید را وارد نمایید.", Toasty.LENGTH_LONG, true).show();
            }
        });

        bottomSheetView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog_confirm.dismiss();
            }
        });

        bottomSheetDialog_confirm.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog_confirm.dismiss();
            }
        });

        bottomSheetDialog_confirm.setContentView(bottomSheetView);
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

                SweetDialog.setSweetDialog(new SweetAlertDialog(SignUp.this, SweetAlertDialog.NORMAL_TYPE), "اتمام وقت", "در صورت عدم دریافت کد تایید ارسال مجدد را انتخاب نمایید.");
                SweetDialog.getSweetAlertDialog().show();
                SweetDialog.getSweetAlertDialog().setConfirmButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        }.start();

    }

    private void requestCode() {
        SweetDialog.startProgress();
        Call<SMSResponse> get = request.SendSMS(phoneNumber.getText().toString());

        get.enqueue(new Callback<SMSResponse>() {
            @Override
            public void onResponse(Call<SMSResponse> call, Response<SMSResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    codeToConfirm = response.body().getCode();
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "پیامک حاوی کد تایید با موفقیت ارسال شد.", "");
                    if (!bottomSheetDialog_confirm.isShowing())
                        bottomSheetDialog_confirm.show();
                    flipTimer();
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی تماس حاصل فرمایید.");
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
        pwd.setText(StaticFun.md5(pwd.getText().toString()));
        Log.e("pwd", "hashed");
        Call<SignUpResponse> get = request.SignUp(phoneNumber.getText().toString(), pwd.getText().toString(), name.getText().toString(), setCreatedTime());

        get.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    setUser(response.body());
                    setData_SharedPreferences();
                    start();
                } else {
                    StaticFun.setLog(phoneNumber.getText().toString(),
                            response.body().getMessage().length() > 0 ? response.body().getMessage() + " - " + response.body().getStatus_code() : " - "
                            , "confirm phone Activity - complete reg / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.");
                Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
                StaticFun.setLog(phoneNumber.getText().toString(), t.getMessage().toString(), "confirm phone Activity - complete reg / failure");
            }
        });
    }

    private void setUser(SignUpResponse response) {
        user = new User();
        user.setID(response.getUserId());
        user.setAccountLevel(StaticFun.account.Bronze);
        user.setEndTime(response.getEnd());
        user.setBirthdayDate("");
        user.setQuestionRemaining(response.getQuestionRemaining());
        user.setName(name.getText().toString());
        user.setPn(phoneNumber.getText().toString());
        user.setCreatedTime(response.getCreated());

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
        User user = new User();
        user.setPn(phoneNumber.getText().toString());
        user.setPwd(pwd.getText().toString());
        String userToSave = gson.toJson(user);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.clear();
        myEdit.putString("userLogin_info", userToSave);
        myEdit.commit();
    }
}