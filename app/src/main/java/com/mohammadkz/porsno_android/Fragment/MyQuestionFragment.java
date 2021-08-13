package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Adapter.QuestionAdapter;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.List;

import es.dmoral.toasty.Toasty;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import ir.hamsaa.persiandatepicker.util.PersianCalendarUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuestionFragment extends Fragment {

    View view;
    RecyclerView myQuestionList;
    FloatingActionButton fab_add;
    User user;
    ApiConfig request;
    ProgressDialog progressDialog;

    public MyQuestionFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_question, container, false);

        request = AppConfig.getRetrofit().create(ApiConfig.class);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("منتظر باشید...");

        progressDialog.show();
        initViews();
        controllerView();
        getData();

        return view;
    }

    private void initViews() {
        myQuestionList = view.findViewById(R.id.myQuestionList);
        myQuestionList.setHasFixedSize(true);
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

    private void getData() {

        Call<List<GetQuestionResponse>> get = request.getQuestions("uId", user.getID());


        get.enqueue(new Callback<List<GetQuestionResponse>>() {
            @Override
            public void onResponse(Call<List<GetQuestionResponse>> call, Response<List<GetQuestionResponse>> response) {
                setAdapter(response.body());
            }

            @Override
            public void onFailure(Call<List<GetQuestionResponse>> call, Throwable t) {
                progressDialog.dismiss();
                StaticFun.alertDialog_connectionFail(getContext());
            }
        });
    }

    private void setAdapter(List<GetQuestionResponse> questionnaireList) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myQuestionList.setLayoutManager(linearLayoutManager);
        QuestionAdapter questionAdapter;

        if (questionnaireList.size() > 0) {

            questionAdapter = new QuestionAdapter(getContext(), questionnaireList);
            myQuestionList.setAdapter(questionAdapter);
            progressDialog.dismiss();

            questionAdapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int pos, View v) {
                    Log.e("qId", " " + pos);
                    progressDialog.show();
                    Intent intent = new Intent(getContext(), AnswerActivity.class);
                    intent.putExtra("qId", questionnaireList.get(pos).getQuestionId());
                    transferData(intent);
                    startActivity(intent);
                }
            });
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.dismiss();
        getData();
    }

    private void transferData(Intent intent) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        intent.putExtra("userInfo", json);

    }
}