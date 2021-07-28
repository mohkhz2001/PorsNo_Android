package com.mohammadkz.porsno_android.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import ir.hamsaa.persiandatepicker.util.PersianCalendarUtils;

public class MyQuestionFragment extends Fragment {

    View view;
    RecyclerView myQuestionList;
    FloatingActionButton fab_add;
    User user;

    public MyQuestionFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_question, container, false);

        initViews();
        controllerView();


        return view;
    }

    private void initViews() {
        myQuestionList = view.findViewById(R.id.myQuestionList);
        fab_add = view.findViewById(R.id.fab_add);

    }

    private void controllerView() {
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuestion();
            }
        });


    }

    private void newQuestion() {
        Intent intent = new Intent(getContext(), NewQuestionActivity.class);
        Gson gson = new Gson();
        String a = gson.toJson(user);
        intent.putExtra("userInfo", a);//user.getID()
        startActivity(intent);
    }
}