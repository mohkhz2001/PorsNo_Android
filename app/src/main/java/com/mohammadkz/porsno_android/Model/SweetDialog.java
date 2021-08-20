package com.mohammadkz.porsno_android.Model;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetDialog {

    static SweetAlertDialog sweetAlertDialog;

    public static void setSweetDialog(SweetAlertDialog sweetDialog, String title, String text) {
        sweetAlertDialog = sweetDialog;
        sweetAlertDialog.setTitle(title);
        sweetAlertDialog.setContentText(text);
    }

    public static void setSweetDialog(SweetAlertDialog sweetDialog) {
        sweetAlertDialog = sweetDialog;
        sweetAlertDialog.setCancelable(false);
    }

    public static SweetAlertDialog getSweetAlertDialog() {
        return sweetAlertDialog;
    }

    public static void startProgress() {
        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.show();
    }

    public static void stopProgress() {
        sweetAlertDialog.dismiss();
    }

    public static void changeSweet(int type, String title, String text) {
        sweetAlertDialog.changeAlertType(type);
        sweetAlertDialog.setTitle(title);
        sweetAlertDialog.setContentText(text);
    }
}
