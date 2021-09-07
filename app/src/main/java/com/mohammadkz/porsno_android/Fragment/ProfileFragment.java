package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Activity.MainPageActivity;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.JodaTimePermission;
import org.joda.time.LocalDate;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class ProfileFragment extends Fragment {

    View view;
    User user;
    EditText pass, phoneNumber, birthdayDate;
    TextView upgrade, accountLevel, name, questionLeft, daysLeft;
    PersianDatePickerDialog datePickerDialog;
    Button done;
    ApiConfig request;
    BottomSheetDialog bottomSheetDialog;

    public ProfileFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_profile, container, false);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE));

            initViews();
            controllerViews();
            setValue();

            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "profile fragment - create");
            return view;
        }

    }

    private void initViews() {
        name = view.findViewById(R.id.name);
        daysLeft = view.findViewById(R.id.daysLeft);
        questionLeft = view.findViewById(R.id.questionLeft);
        pass = view.findViewById(R.id.pwd);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        birthdayDate = view.findViewById(R.id.birthday);
        upgrade = view.findViewById(R.id.upgrade);
        accountLevel = view.findViewById(R.id.accountLevel);
        done = view.findViewById(R.id.done);
    }

    private void controllerViews() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValue()) {
                    update(false, "-");
                } else {
                    Toasty.error(getContext(), "'نام' نمیتواند خالی باشد.", Toasty.LENGTH_SHORT, true).show();
                }
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                ReBuyFragment reBuyFragment = new ReBuyFragment(user);
                ((MainPageActivity) getActivity()).setDrawerSelect(R.id.reBuy);
                fragmentTransaction.replace(R.id.frameLayout, reBuyFragment).commit();
            }
        });

        birthdayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetChooser();
            }
        });

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.warning(getContext() , "شما قادر به تغییر شماره خود نیستید." , Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {

        datePickerDialog = new PersianDatePickerDialog(getContext())
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButtonVisible(false)
                .setMinYear(1350)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(Color.GREEN)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new PersianPickerListener() {
                    @Override
                    public void onDateSelected(PersianPickerDate persianPickerDate) {
                        if (!user.getBirthdayDate().equals(String.valueOf(persianPickerDate.getTimestamp()))) {
                            birthdayDate.setText(persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay());
                            user.setBirthdayDate(String.valueOf(persianPickerDate.getTimestamp()));
                            done.setVisibility(View.VISIBLE);
                        }

                        Toast.makeText(getContext(), persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismissed() {

                    }
                });

        datePickerDialog.show();
    }

    private void setValue() {
        if (user != null) {
            name.setText(user.getName().toString());
            pass.setText(user.getName().toString());
            phoneNumber.setText(user.getPn().toString());
            questionLeft.setText(user.getQuestionRemaining());
            calculateDaysLeft();

            if (user.getBirthdayDate().length() > 1) {
                PersianDate pdate = new PersianDate(Long.valueOf(user.getBirthdayDate()));
                PersianDateFormat pdformater1 = new PersianDateFormat("Y/m/d");
                birthdayDate.setText("" + pdformater1.format(pdate));
            }


            if (user.getAccountLevel().toString().equals("Bronze")) {
                accountLevel.setText("برنز");
            } else if (user.getAccountLevel().toString().equals("Steel")) {
                accountLevel.setText("استیل");
            } else if (user.getAccountLevel().toString().equals("Gold")) {
                accountLevel.setText("طلایی");
            } else if (user.getAccountLevel().toString().equals("Diamond")) {
                accountLevel.setText("الماس");
            }

        }
    }

    private void update(boolean pwd, String pass) {
        Call<NormalResponse> get = null;
        if (pwd)
            get = request.updateProfile(name.getText().toString(), pass, user.getBirthdayDate(), user.getID().toString());
        else
            get = request.updateProfile(name.getText().toString(), pass, user.getBirthdayDate(), user.getID().toString());

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    if (pwd && bottomSheetDialog != null) {
                        bottomSheetDialog.dismiss();
                    }
                    Toasty.success(getContext(), "تغییرات با موفقیت اعمال شد.", Toasty.LENGTH_SHORT, true).show();
                    user.setName(name.getText().toString());

                    SweetDialog.stopProgress();

                    ((MainPageActivity) getActivity()).updateUser(user);
                } else {
                    StaticFun.setLog((user == null) ? "-"
                            : (user.getPn().length() > 0 ? user.getPn() : "-"), "-", "profile fragment - update / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), t.getMessage().toString(), "profile fragment - update / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
            }
        });
    }

    private boolean checkValue() {
        if (name.getText().toString().length() > 0)
            return true;
        else
            return false;
    }

    // bottom choose => for change the pwd
    public void bottomSheetChooser() {
        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();

        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_sheet_pwd, (LinearLayout) view.findViewById(R.id.layout));

        bottomSheetView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText now_pas, new_pas, new_pas_re;

                now_pas = bottomSheetView.findViewById(R.id.now_pas);
                new_pas = bottomSheetView.findViewById(R.id.new_pas);
                new_pas_re = bottomSheetView.findViewById(R.id.new_pas_re);

                if (now_pas.getText().length() > 0 && new_pas.getText().length() > 0 && new_pas_re.getText().length() > 0
                        && (new_pas.getText().toString().equals(new_pas_re.getText().toString()))) {

                    checkPrePwd(now_pas.getText().toString(), new_pas.getText().toString());

                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                }

            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
                SweetDialog.stopProgress();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void checkPrePwd(String pwd, String finalPwd) {
        pwd = StaticFun.md5(pwd);
        Call<LoginResponse> get = request.loginResponse(user.getPn(), pwd);


        get.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    update(true, StaticFun.md5(finalPwd));
                } else {
                    StaticFun.setLog((user == null) ? "-"
                            : (user.getPn().length() > 0 ? user.getPn() : "-"), "-", "profile fragment - check pwd / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), t.getMessage().toString(), "profile fragment - check pwd / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
            }
        });

    }

    private void calculateDaysLeft() {
        try {
            Timestamp today = new Timestamp(System.currentTimeMillis());

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date end = new java.util.Date(Long.parseLong(user.getEndTime()));
            System.out.println(sf.format(end));

            Interval interval = new Interval(today.getTime(), Long.parseLong(user.getEndTime() + "000"));
            Duration period = interval.toDuration();

            daysLeft.setText(period.getStandardDays() + "");
        } catch (Exception e) {
            daysLeft.setText("N/A");
        }


    }
}