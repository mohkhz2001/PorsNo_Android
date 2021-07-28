package com.mohammadkz.porsno_android.Fragment.NewQuestion;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;

import es.dmoral.toasty.Toasty;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import ir.hamsaa.persiandatepicker.util.PersianCalendarUtils;

public class NewQuestion_InfoFragment extends Fragment {

    Button date_start, date_end, next;
    View view;
    PersianDatePickerDialog datePickerDialog;
    TextInputEditText name, description;
    AutoCompleteTextView category;
    Questionnaire questionnaire = new Questionnaire();
    User user;

    public NewQuestion_InfoFragment(User user) {
        // Required empty public constructor
        questionnaire.setUserId(user.getID());
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_question_info, container, false);

        initViews();
        controllerViews();

        return view;
    }

    private void initViews() {
        next = view.findViewById(R.id.next);
        date_start = view.findViewById(R.id.date_start);
        date_end = view.findViewById(R.id.date_end);
        name = view.findViewById(R.id.questionName);
        category = view.findViewById(R.id.category);
        description = view.findViewById(R.id.description);
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
                if (checkValue() && questionnaire != null) {
                    if (checkEndTime(user.getEndTime())) {
                        questionnaire.setName(name.getText().toString());
                        questionnaire.setDescription(description.getText().toString());
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        NewQuestion_NewFragment newQuestion_newFragment = new NewQuestion_NewFragment(questionnaire);
                        fragmentTransaction.replace(R.id.frameLayout, newQuestion_newFragment).commit();
                    } else {
                        Toasty.error(getContext(), "زمان اتمام پرسشنامه نمی تواند بیشتر از زمان اتمام حساب کاربری شما باشد", Toasty.LENGTH_LONG).show();
                    }
                } else {
                    errorField();
                }
            }

        });
    }

    private boolean checkValue() {
        if (name.getText().length() > 0 && description.getText().length() > 0 && questionnaire.getEndDate() != null && questionnaire.getStartDate() != null) {
            return true;
        } else {
            return false;
        }
    }

    private String datePicker(boolean start) {
        String TAG = "";

        datePickerDialog = new PersianDatePickerDialog(getContext())
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1399)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(Color.GRAY)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new PersianPickerListener() {
                    @Override
                    public void onDateSelected(PersianPickerDate persianPickerDate) {
                        if (start) {
                            questionnaire.setStartDate_stamp(String.valueOf(persianPickerDate.getTimestamp() / 1000));
                            questionnaire.setStartDate(persianPickerDate.getPersianLongDate());

                            // set text for btn
                            date_start.setText("شروع: \n" + persianPickerDate.getPersianLongDate());

                        } else {
                            questionnaire.setEndDate_stamp(String.valueOf(persianPickerDate.getTimestamp() / 1000));
                            questionnaire.setEndDate(persianPickerDate.getPersianLongDate());

                            // set text for btn
                            date_end.setText("اتمام: \n" + persianPickerDate.getPersianLongDate());
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

    private void errorField() {
        if (name.getText().length() <= 0) {
            name.setError("نمیتواند خالی باشد");
        }
        if (description.getText().length() <= 0) {
            description.setError("نمیتواند خالی باشد");
        }
        if (questionnaire.getStartDate() == null) {
            Toasty.error(getContext(), "زمان شروع نمی تواند خالی باشد", Toasty.LENGTH_SHORT).show();
        } else if (questionnaire.getEndDate() == null) {
            Toasty.error(getContext(), "زمان پایان نمی تواند خالی باشد", Toasty.LENGTH_SHORT).show();
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

}