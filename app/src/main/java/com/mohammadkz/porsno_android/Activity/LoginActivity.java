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
    TextView signUp, forgotPWD;
    Button login, signUpBtn;
    ApiConfig request;
    User user;
    ConstraintLayout root;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            request = AppConfig.getRetrofit().create(ApiConfig.class);
            SweetDialog.setSweetDialog(new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE));
            getToken();
            topic();
            initViews();
            controllerViews();

            if (autoLogin()) {
                getData_SharedPreferences();
            } else {
//            SweetDialog.stopProgress();
                root.setVisibility(View.VISIBLE);
            }
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
        signUp = findViewById(R.id.signUp);
        forgotPWD = findViewById(R.id.forgotPWD);
        login = findViewById(R.id.login);
        root = findViewById(R.id.root);
        signUpBtn = findViewById(R.id.signUpBtn);
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
                bottomSheetChooser();
            }
        });

        SweetDialog.getSweetAlertDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                root.setVisibility(View.VISIBLE);

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
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

                    setUser(response.body());
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    transferData(intent);
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "خوش آمدید", "ورود با موفقیت انجام شد");
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
                t.getMessage();
                if (shared) {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی تماس حاصل فرمایید.");
                    root.setVisibility(View.VISIBLE);
                } else {
                    SweetDialog.stopProgress();
                    root.setVisibility(View.VISIBLE);
                }

                StaticFun.setLog(phoneNumber.getText().toString(), t.getMessage().toString(), "Login - api");

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
                StaticFun.setLog((user == null) ? (phoneNumber != null && phoneNumber.getText().length() > 0 ? phoneNumber.getText().toString() : "-")
                        : (user.getPn().length() > 0 ? user.getPn() : (phoneNumber.getText().length() > 0 ? phoneNumber.getText().toString() : "-")), e.getMessage().toString(), "login");
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
        View bottomSheetView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_forget_pwd, (LinearLayout) findViewById(R.id.layout));
        TextInputLayout pn_layout, new_pn_layout, new_rePn_layout;
        TextInputEditText pn, new_pn, new_rePn;
        EditText code;
        Button get, confirm, confirmCode;

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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new_pn.getText().toString().equals(new_rePn.getText().toString())) {

                    String pwd = StaticFun.md5(new_pn.getText().toString());

                    Call<NormalResponse> updatePwdCall = request.updatePwd(pn.getText().toString(), pwd);

                    updatePwdCall.enqueue(new Callback<NormalResponse>() {
                        @Override
                        public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                            if (response.body().getStatus_code().equals("200")) {
                                Toasty.success(LoginActivity.this, "رمز عبور شما به درستی تغییر کرد.", Toasty.LENGTH_SHORT, true).show();
                                bottomSheetDialog.dismiss();
                            } else {
                                System.out.println();
                            }
                        }

                        @Override
                        public void onFailure(Call<NormalResponse> call, Throwable t) {
                            System.out.println();
                        }
                    });


                } else {
                    Toasty.error(LoginActivity.this, "رمز های وارد شده یکسان نمی باشد.", Toasty.LENGTH_SHORT).show();
                }
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

    // create the topic in app
    private void topic() {
        FirebaseMessaging.getInstance().subscribeToTopic("offer")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

}