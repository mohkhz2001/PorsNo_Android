package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.mohammadkz.porsno_android.Fragment.NewQuestion.NewQuestion_InfoFragment;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;
import com.warkiz.widget.IndicatorSeekBar;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class NewQuestionActivity extends AppCompatActivity {

    IndicatorSeekBar seekBar;
    String Id;
    MaterialToolbar materialToolbar;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_question);

            SweetDialog.setSweetDialog(new SweetAlertDialog(NewQuestionActivity.this, SweetAlertDialog.PROGRESS_TYPE));

            Id = getIntent().getStringExtra("id");

            initViews();
            controllerViews();
            getDate();
            startFragment();
        } catch (Exception e) {
            Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "new question Activity - create");
            onCreate(savedInstanceState);
        }

    }

    private void initViews() {
        seekBar = findViewById(R.id.seekBar);
        seekBar.setClickable(false);
        materialToolbar = findViewById(R.id.topAppBar);
    }

    private void controllerViews() {
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetDialog.getSweetAlertDialog().show();
                SweetDialog.changeSweet(SweetAlertDialog.WARNING_TYPE, "آیا از خروج خود اطمینان دارید؟", "اطلاعات وارد شده شما ذخیره نخواهند شد.");
                SweetDialog.getSweetAlertDialog().setConfirmButton("خروج", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                });
                SweetDialog.getSweetAlertDialog().setCancelButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SweetDialog.stopProgress();
                    }
                });
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void setSeekBar(int pos) {
        switch (pos) {
            case 1:
                seekBar.setProgress(28);
                break;
            case 2:
                seekBar.setProgress(60);
                break;
            case 3:
                seekBar.setProgress(100);
                break;
        }
    }

    private void startFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewQuestion_InfoFragment newQuestion_infoFragment = new NewQuestion_InfoFragment(user);
        fragmentTransaction.replace(R.id.frameLayout, newQuestion_infoFragment).commit();

    }

    // get data from sign up page(intent)
    private void getDate() {
        String data = getIntent().getStringExtra("userInfo");
        Log.e("user", " " + data);
        if (data != null)
            try {
                JSONObject jsonObject = new JSONObject(data);

                user = new User();
                user.setID(jsonObject.getString("ID"));
                user.setPn(jsonObject.getString("pn"));
                user.setName(jsonObject.getString("name"));
                user.setCreatedTime(jsonObject.getString("createdTime"));
                user.setEndTime(jsonObject.getString("endTime"));

                // account level ....

            } catch (Exception e) {
                e.printStackTrace();
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "new questionnair Activity - get date");
                getDate();
            }
    }

    @Override
    public void onBackPressed() {

        SweetDialog.startProgress();
        SweetDialog.changeSweet(SweetAlertDialog.WARNING_TYPE, "خروج", "در صورت خروج جواب های شما ذخیره نمی شوند.\nآیا از خروج خود اطمینان دارین؟");
        SweetDialog.getSweetAlertDialog()
                .setCancelButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setConfirmButton("خروج", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                });

    }
}