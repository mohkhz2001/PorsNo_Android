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
import com.mohammadkz.porsno_android.StaticFun;

import es.dmoral.toasty.Toasty;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import ir.hamsaa.persiandatepicker.util.PersianCalendarUtils;

public class NewQuestion_InfoFragment extends Fragment {

    Button next;
    View view;
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

        try {

            view = inflater.inflate(R.layout.fragment_new_question_info, container, false);

            initViews();
            controllerViews();

            return view;
        }catch (Exception e){
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "new question info - create");
            return view;
        }
        // Inflate the layout for this fragment
    }

    private void initViews() {
        next = view.findViewById(R.id.next);
        name = view.findViewById(R.id.questionName);
        category = view.findViewById(R.id.category);
        description = view.findViewById(R.id.description);
    }

    private void controllerViews() {

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValue() && questionnaire != null) {
                    questionnaire.setName(name.getText().toString());
                    questionnaire.setDescription(description.getText().toString());
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    NewQuestion_DateFragment newQuestion_dateFragment = new NewQuestion_DateFragment(questionnaire, user);
                    fragmentTransaction.replace(R.id.frameLayout, newQuestion_dateFragment).commit();
                } else {
                    errorField();
                }
            }

        });
    }

    private boolean checkValue() {
        if (name.getText().length() > 0 && description.getText().length() > 0) {
            return true;
        } else {
            return false;
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

}