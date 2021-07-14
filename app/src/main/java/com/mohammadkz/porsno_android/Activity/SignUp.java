package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

    ApiConfig request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        initViews();
        controllerViews();

    }

    private void initViews() {
        pwd = findViewById(R.id.password);
        pwd.setText("m98783110");
        name = findViewById(R.id.name);
        name.setText("mohammadmehdi khajehzADEH");
        phoneNumber = findViewById(R.id.phoneNumber);
        phoneNumber.setText("09388209270");
        sendSMS = findViewById(R.id.send);
    }

    private void controllerViews() {
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaticFun.isNetworkAvailable(getApplicationContext())) {
                    if (checkValue()) {
                        signUp();
                    } else {
                        Toasty.error(getApplicationContext(), "لطفا تمامی موارد را تکمیل نمایید.", Toast.LENGTH_LONG, true).show();
                    }
                } else {
                    StaticFun.alertDialog_connectionFail(getApplicationContext());
                }

            }
        });
    }

    private void signUp() {
        if (true) {//!checkPN(phoneNumber.getText().toString())
            confirmPhoneNumber(new User(name.getText().toString(), phoneNumber.getText().toString(), pwd.getText().toString()));
        } else {

        }
    }

    // convert inputed data to json code then send it to another activity
    private boolean checkPN(String phoneNumber) {
        final boolean[] check = {false};
        Call<CheckPhoneResponse> get = request.checkPhoneNumber(phoneNumber);
        get.enqueue(new Callback<CheckPhoneResponse>() {
            @Override
            public void onResponse(Call<CheckPhoneResponse> call, Response<CheckPhoneResponse> response) {

            }

            @Override
            public void onFailure(Call<CheckPhoneResponse> call, Throwable t) {
                t.getMessage();
                StaticFun.alertDialog_connectionFail(getApplicationContext());
                check[0] = true;
            }
        });

        return check[0];
    }

    private void confirmPhoneNumber(User user) {
        Intent intent = new Intent(this, ConfirmPhoneNumberActivity.class);
        transferData(user, intent);
        startActivity(intent);
    }

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