package com.mohammadkz.porsno_android.Fragment.NewQuestion;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;

import es.dmoral.toasty.Toasty;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;


public class NewQuestion_DateFragment extends Fragment {

    View view;
    PersianDatePickerDialog datePickerDialog;
    TextInputEditText date_start, date_end;
    Button next;
    Questionnaire questionnaire;
    User user;

    public NewQuestion_DateFragment(Questionnaire questionnaire, User user) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_question_date, container, false);

        ((NewQuestionActivity) getActivity()).setSeekBar(1);

        initViews();
        controllerViews();

        return view;
    }

    private void initViews() {
        date_start = view.findViewById(R.id.date_start);
        date_end = view.findViewById(R.id.date_end);
        next = view.findViewById(R.id.next);
    }

    private void controllerViews() {
        date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(true);
            }
        });

        date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(false);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValue()) {
                    if (checkEndTime(user.getEndTime())) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        NewQuestion_NewFragment newQuestion_newFragment = new NewQuestion_NewFragment(questionnaire);
                        fragmentTransaction.replace(R.id.frameLayout, newQuestion_newFragment).commit();
                    } else {
                        Toasty.error(getContext(), "زمان اتمام پرسشنامه نمی تواند بیشتر از زمان اتمام حساب کاربری شما باشد", Toasty.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(getContext(), "تمامی موارد را تکمیل نمایید.", Toasty.LENGTH_LONG).show();
                }
            }
        });
    }

    private String datePicker(boolean start) {
        String TAG = "";

        datePickerDialog = new PersianDatePickerDialog(getContext())
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1400)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(Color.GREEN)
                .setActionTextSize(20)
                .setTodayTextSize(20)
                .setNegativeTextSize(20)
                .setPickerBackgroundDrawable(R.drawable.bg_bottom_sheet)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(false)
                .setListener(new PersianPickerListener() {
                    @Override
                    public void onDateSelected(PersianPickerDate persianPickerDate) {
                        if (start) {
                            questionnaire.setStartDate_stamp(String.valueOf(persianPickerDate.getTimestamp() / 1000));
                            questionnaire.setStartDate(persianPickerDate.getPersianLongDate());

                            // set text for btn
                            date_start.setText(persianPickerDate.getPersianLongDate());

                        } else {
                            questionnaire.setEndDate_stamp(String.valueOf(persianPickerDate.getTimestamp() / 1000));
                            questionnaire.setEndDate(persianPickerDate.getPersianLongDate());

                            // set text for btn
                            date_end.setText(persianPickerDate.getPersianLongDate());
                        }
                        Toast.makeText(getContext(), persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismissed() {

                    }
                });

        datePickerDialog.show();

        if (start) {
            return questionnaire.getStartDate();
        } else {
            return questionnaire.getEndDate();
        }
    }

    private boolean checkEndTime(String endTime) {
        if (Long.parseLong(endTime) >= Long.parseLong(questionnaire.getEndDate_stamp()))
            return true;
        else if (Long.parseLong(endTime) <= Long.parseLong(questionnaire.getEndDate_stamp()) || endTime == null) {
            return false;
        } else
            return false;
    }

    private boolean checkValue() {
        if (date_end.getText().length() > 0 && date_start.getText().length() > 0)
            return true;
        else
            return false;
    }
}