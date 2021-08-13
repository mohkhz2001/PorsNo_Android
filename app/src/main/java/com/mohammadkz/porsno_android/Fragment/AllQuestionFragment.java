package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Adapter.QuestionAdapter;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllQuestionFragment extends Fragment {

    View view;
    RecyclerView list;
    ApiConfig request;
    ProgressDialog progressDialog;
    User user;

    public AllQuestionFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_question, container, false);

        request = AppConfig.getRetrofit().create(ApiConfig.class);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("منتظر باشید...");

        initViews();
        controllerViews();
        getData();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.list);

    }

    private void controllerViews() {

    }

    private void getData() {

        Call<List<GetQuestionResponse>> get = request.getQuestions("-", "-");


        get.enqueue(new Callback<List<GetQuestionResponse>>() {
            @Override
            public void onResponse(Call<List<GetQuestionResponse>> call, Response<List<GetQuestionResponse>> response) {
                Log.e("size", response.body().size() + " ");
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
        list.setLayoutManager(linearLayoutManager);

        QuestionAdapter questionAdapter;

        if (questionnaireList.size() > 0) {

            questionAdapter = new QuestionAdapter(getContext(), questionnaireList);
            list.setAdapter(questionAdapter);
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

    private void transferData(Intent intent) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        intent.putExtra("userInfo", json);

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.dismiss();
        getData();
    }
}