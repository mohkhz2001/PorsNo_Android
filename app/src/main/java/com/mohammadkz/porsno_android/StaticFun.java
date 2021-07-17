package com.mohammadkz.porsno_android;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.security.MessageDigest;

public class StaticFun {
    // account level enum  ==> there is 4 level
    public enum account {Bronze, Steel, Gold, Diamond}

    // error handler
    public static void alertDialog_connectionFail(Context context) {
        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
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
        } catch (Exception e) {
            e.getMessage();
        }

    }

    // error handler for input wrong phone number or pass
    public static void alertDialog_error_login(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
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

    // error handler for exists phone number for sign up
    public static void alertDialog_error_exist_pn(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
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
}
