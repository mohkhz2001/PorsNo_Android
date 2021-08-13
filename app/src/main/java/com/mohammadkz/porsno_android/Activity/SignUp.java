package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.Response.CheckPhoneResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.Random;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    EditText pwd, name, phoneNumber;
    Button sendSMS;
    ProgressDialog progressDialog;
    ApiConfig request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage("منتظر باشید...");

        initViews();
        controllerViews();

    }

    private void initViews() {
        pwd = findViewById(R.id.password);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        sendSMS = findViewById(R.id.send);
    }

    private void controllerViews() {
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (StaticFun.isNetworkAvailable(getApplicationContext())) {
                    if (checkValue()) {
                        checkPN(phoneNumber.getText().toString());
                    } else {
                        progressDialog.dismiss();
                        Toasty.error(getApplicationContext(), "لطفا تمامی موارد را تکمیل نمایید.", Toast.LENGTH_LONG, true).show();
                    }
                } else {
                    progressDialog.dismiss();
                    StaticFun.alertDialog_connectionFail(SignUp.this);
                }

            }
        });
    }

    private void checkPN(String phoneNumber) {
        Call<CheckPhoneResponse> get = request.checkPhoneNumber(phoneNumber);
        String finalPhoneNumber = phoneNumber;
        get.enqueue(new Callback<CheckPhoneResponse>() {
            @Override
            public void onResponse(Call<CheckPhoneResponse> call, Response<CheckPhoneResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    confirmPhoneNumber(new User(name.getText().toString(), finalPhoneNumber, pwd.getText().toString()));
                } else {
                    progressDialog.dismiss();
                    Toasty.error(getApplicationContext(), "این مشخصات موجود است", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CheckPhoneResponse> call, Throwable t) {
                t.getMessage();
                StaticFun.alertDialog_serverConnectFail(SignUp.this);
                progressDialog.dismiss();
            }
        });

    }

    private void confirmPhoneNumber(User user) {
        Intent intent = new Intent(this, ConfirmPhoneNumberActivity.class);
        transferData(user, intent);
        progressDialog.dismiss();
        startActivity(intent);
        finish();
    }

    // convert inputed data to json code then send it to another activity
    private void transferData(User user, Intent intent) {
        Gson gson = new Gson();
        String userToTransfer = gson.toJson(user);
        intent.putExtra("signUp", userToTransfer);

    }

    // generate the code for send sms to confirm phone number
    public int getRandomNumberString() {
        // It will generate 4 digit random Number.
        // from 0 to 9999
        String number = "";
        while (number.length() != 4) {
            Random rnd = new Random();
            number = Integer.toString(rnd.nextInt(9999));
        }
        Toast.makeText(getApplicationContext(), number, Toast.LENGTH_LONG).show();
        // this will convert any number sequence into 6 character.

        return Integer.valueOf(String.format("%04d", Integer.parseInt(number)));
    }

    private boolean checkValue() {
        if ((name.getText().length() > 0 && pwd.getText().length() > 0 && phoneNumber.getText().length() > 0)) {
            return true;
        } else
            return false;
    }
}