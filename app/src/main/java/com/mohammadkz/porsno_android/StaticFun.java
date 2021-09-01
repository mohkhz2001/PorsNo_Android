package com.mohammadkz.porsno_android;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;

import java.security.MessageDigest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaticFun {
    // account level enum  ==> there is 4 level
    public enum account {Bronze, Steel, Gold, Diamond}

    // error handler
    // connect to net error
    public static void alertDialog_connectionFail(Context context) {
        try {

            new MaterialAlertDialogBuilder(context, R.style.CustomMaterialDialog)
                    .setTitle("مشکل در برقراری ارتباط")
                    .setMessage("مشکل در برقراری ارتباط با سرور\nلطفا بعد از اطمینان از اتصال خود به اینترنت دوباره تلاش نمایید.")
                    .setCancelable(true)
                    .setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }

    }

    // error handler
    // when have time out or ... that about the server
    public static void alertDialog_serverConnectFail(Context context) {
        try {

            new MaterialAlertDialogBuilder(context, R.style.CustomMaterialDialog)
                    .setTitle("مشکل در برقراری ارتباط")
                    .setMessage("ارتباط با سرور برقرار نشد\nدقایقی دیگر امتحان کنید و یا با واحد پشتیبانی نماس حاصل فرمایید.")
                    .setCancelable(true)
                    .setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }

    }

    // error handler for input wrong phone number or pass
    public static void alertDialog_error_login(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialDialog);
        builder.setTitle("مشکل در ورود");
        builder.setMessage("نام کاربری یا رمز عبور اشتباه است");
        String positive = "بستن";

        // have one btn ==> close
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.show();
    }

    // error handler for exists phone number for sign up
    public static void alertDialog_error_exist_pn(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialDialog);
        builder.setTitle("");
        builder.setMessage("");
        String positive = "بستن";

        // have one btn ==> close
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.show();
    }

    // check internet connection
    public static boolean isNetworkAvailable(Context context) {
        // Get Connectivity Manager class object from Systems Service
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get Network Info from connectivity Manager
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    // hashing password
    public static String md5(String password) {
        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();

        } catch (Exception e) {
            e.getMessage();
            return null;
        }

    }

    public static void setLog(String pn, String error, String location) {
        ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);

        Call<NormalResponse> get = request.appLog(pn, error, location);
        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                Log.e("", "");
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                Log.e("", "");
            }
        });
    }
}
