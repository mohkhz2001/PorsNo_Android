package com.mohammadkz.porsno_android.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.Response.ForgetPwdResponse;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.Notification.PushNotificationService;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText pwd, phoneNumber;
    TextView forgotPWD;
    Button login;
    ApiConfig request;
    User user;
    BottomSheetDialog bottomSheetDialog;
    ImageView back, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            request = AppConfig.getRetrofit().create(ApiConfig.class);
            SweetDialog.setSweetDialog(new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE));
            getToken();
            initViews();
            controllerViews();

        } catch (Exception e) {
            StaticFun.setLog((user == null) ? (phoneNumber != null && phoneNumber.getText().length() > 0 ? phoneNumber.getText().toString() : "-")
                    : (user.getPn().length() > 0 ? user.getPn() : (phoneNumber.getText().length() > 0 ? phoneNumber.getText().toString() : "-")), e.getMessage().toString(), "login - create");
            SweetDialog.setSweetDialog(new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE), "کاربر گرامی مشکلی در حال حاضر بر روی سیستم ما وجود دادم لطفا دقایقی دیگر تلاش کنید.", "");
            SweetDialog.startProgress();
        }

    }

    private void initViews() {
        pwd = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phoneNumber);
        forgotPWD = findViewById(R.id.forgetPwd);
        login = findViewById(R.id.login);
        info = findViewById(R.id.info);
        back = findViewById(R.id.back);
    }

    private void controllerViews() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetDialog.setSweetDialog(new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE), "در حال ورود", "لطفا منتظر باشید...");
                SweetDialog.startProgress();
                if (StaticFun.isNetworkAvailable(getApplicationContext())) {
                    if (checkValue()) {
                        login();
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

        forgotPWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetChooser();
            }
        });

        SweetDialog.getSweetAlertDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, StartActivity.class);
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

                    setData_SharedPreferences(pass);
                    setUser(response.body());
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    transferData(intent);
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "خوش آمدید", "ورود با موفقیت انجام شد");
                    start(intent);
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در ورود", "نام کاربری یا رمز عبور اشتباه است");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.getMessage();

                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی تماس حاصل فرمایید.");

                StaticFun.setLog(phoneNumber.getText().toString(), t.getMessage().toString(), "Login - api - on fail");

            }
        });

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
        SweetDialog.stopProgress();
        startActivity(intent);
        finish();
    }

    // bottom choose => camera or file(gallery)
    public void bottomSheetChooser() {

        bottomSheetDialog = new BottomSheetDialog(LoginActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setCancelable(false);
        View bottomSheetView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_forget_pwd, (LinearLayout) findViewById(R.id.layout));
        TextInputLayout pn_layout, new_pn_layout, new_rePn_layout;
        TextInputEditText pn, new_pn, new_rePn;
        EditText code;
        Button get, confirm, confirmCode;
        ImageView close = bottomSheetView.findViewById(R.id.close);

        // init views
        pn_layout = bottomSheetView.findViewById(R.id.pn_layout);
        new_pn_layout = bottomSheetView.findViewById(R.id.new_pn_layout);
        new_rePn_layout = bottomSheetView.findViewById(R.id.new_rePn_layout);
        pn = bottomSheetView.findViewById(R.id.pn);
        new_pn = bottomSheetView.findViewById(R.id.new_pn);
        new_rePn = bottomSheetView.findViewById(R.id.new_rePn);
        code = bottomSheetView.findViewById(R.id.code);
        get = bottomSheetView.findViewById(R.id.get);
        confirm = bottomSheetView.findViewById(R.id.confirm);
        confirmCode = bottomSheetView.findViewById(R.id.confirmCode);

        final String[] genearatedCode = new String[1];

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check phoneNumber
                Call<ForgetPwdResponse> forgetPwdCall = request.forgetPwd(pn.getText().toString());

                forgetPwdCall.enqueue(new Callback<ForgetPwdResponse>() {
                    @Override
                    public void onResponse(Call<ForgetPwdResponse> call, Response<ForgetPwdResponse> response) {
                        if (response.body().getStatus_code().equals("200")) {
                            Toasty.info(LoginActivity.this, response.body().getCode() + "", Toasty.LENGTH_SHORT, true).show();
                            genearatedCode[0] = response.body().getCode();

                            get.setVisibility(View.GONE);
                            pn.setFocusable(false);

                            code.setVisibility(View.VISIBLE);
                            confirmCode.setVisibility(View.VISIBLE);
                        } else {
                            Toasty.warning(LoginActivity.this, "چنین مشخصاتی یافت نشد.", Toasty.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForgetPwdResponse> call, Throwable t) {
                        System.out.println();
                    }
                });

            }
        });

        confirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().toString().equals(genearatedCode[0] + "")) {

                    pn_layout.setVisibility(View.GONE);
                    code.setVisibility(View.GONE);
                    confirmCode.setVisibility(View.GONE);

                    new_pn_layout.setVisibility(View.VISIBLE);
                    new_rePn_layout.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.VISIBLE);


                } else {
                    Toasty.error(LoginActivity.this, "کد وارد شده صحیح نمی باشد.", Toasty.LENGTH_SHORT, true).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    // get the firebase token
    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("test token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("test token", token);
                    }
                });
    }

}