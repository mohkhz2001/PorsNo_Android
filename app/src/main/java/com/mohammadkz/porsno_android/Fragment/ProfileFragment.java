package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.MainPageActivity;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import es.dmoral.toasty.Toasty;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    View view;
    User user;
    TextInputEditText name, pass, phoneNumber, birthdayDate;
    TextView upgrade, accountLevel;
    PersianDatePickerDialog datePickerDialog;
    Button done;
    ApiConfig request;
    ProgressDialog progressDialog;
    BottomSheetDialog bottomSheetDialog;

    public ProfileFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        request = AppConfig.getRetrofit().create(ApiConfig.class);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("منتظر باشید...");

        initViews();
        controllerViews();
        setValue();

        return view;
    }

    private void initViews() {
        name = view.findViewById(R.id.name);
        pass = view.findViewById(R.id.password);
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
                ((MainPageActivity)getActivity()).setDrawerSelect(R.id.reBuy);
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
                        birthdayDate.setText(persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay());
                        user.setBirthdayDate(String.valueOf(persianPickerDate.getTimestamp()));
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
            birthdayDate.setText(user.getBirthdayDate() == null ? " " : user.getBirthdayDate());

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
            get = request.updateProfile(name.getText().toString(), pass, birthdayDate.getText().toString(), user.getID().toString());
        else
            get = request.updateProfile(name.getText().toString(), pass, birthdayDate.getText().toString(), user.getID().toString());

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    if (pwd && bottomSheetDialog != null) {
                        bottomSheetDialog.dismiss();
                    }
                    Toasty.success(getContext(), "تغییرات با موفقیت اعمال شد.", Toasty.LENGTH_SHORT, true).show();
                    user.setName(name.getText().toString());
                    user.setBirthdayDate(birthdayDate.getText().toString());

                    progressDialog.dismiss();

                    ((MainPageActivity) getActivity()).updateUser(user);
                } else {
                    Toasty.error(getContext(), "shiiit", Toasty.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                StaticFun.alertDialog_connectionFail(getContext());
            }
        });
    }

    private boolean checkValue() {
        if (name.getText().toString().length() > 0)
            return true;
        else
            return false;
    }

    // bottom choose => camera or file(gallery)
    public void bottomSheetChooser() {
        progressDialog.show();

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
                    progressDialog.dismiss();
                    Toasty.error(getContext(), "همه ی موارد باید تکمیل شوند", Toasty.LENGTH_SHORT, true).show();
                }

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
                } else
                    Toasty.error(getContext(), "پسور وارد شده اشتباه است", Toasty.LENGTH_SHORT, true).show();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                StaticFun.alertDialog_connectionFail(getContext());
            }
        });

    }

}