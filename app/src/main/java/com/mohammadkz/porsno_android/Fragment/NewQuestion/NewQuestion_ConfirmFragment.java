package com.mohammadkz.porsno_android.Fragment.NewQuestion;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Adapter.ConfirmNewQuestionaireAdapter;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.NewQuestionaire;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewQuestion_ConfirmFragment extends Fragment {

    View view;
    RecyclerView list;
    Questionnaire questionnaire;
    Button confirm;
    ConfirmNewQuestionaireAdapter confirmNewQuestionaireAdapter;
    ApiConfig request;
    ProgressDialog progressDialog;

    public NewQuestion_ConfirmFragment(Questionnaire questionnaire) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_question_confirm, container, false);

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("منتظر باشید...");

        ((NewQuestionActivity) getActivity()).setSeekBar(2);

        initViews();
        controllerViews();
        setAdapter();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        confirm = view.findViewById(R.id.confirm);
    }

    private void controllerViews() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Api();
            }
        });
    }

    private void setAdapter() {
        confirmNewQuestionaireAdapter = new ConfirmNewQuestionaireAdapter(getContext(), questionnaire.getQuestions() , true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(confirmNewQuestionaireAdapter);

    }

    private void Api() {
        String json = convertToJson();

        Call<NewQuestionaire> get = request.newQuestnaire(" - ", questionnaire.getName(), questionnaire.getStartDate_stamp(), questionnaire.getEndDate_stamp(), " - ",
                questionnaire.getDescription(), json, questionnaire.getUserId());

        get.enqueue(new Callback<NewQuestionaire>() {
            @Override
            public void onResponse(Call<NewQuestionaire> call, Response<NewQuestionaire> response) {
                if (response.body().getStatus_code().equals("200")) {
                    progressDialog.dismiss();
                    getActivity().finish();
                } else {
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<NewQuestionaire> call, Throwable t) {
                t.getMessage();
                StaticFun.alertDialog_connectionFail(getContext());
                progressDialog.dismiss();
            }
        });
    }

    private String convertToJson() {
        Gson gson = new Gson();
        String a = gson.toJson(questionnaire.getQuestions());
        Log.e("json", a + " ");
        return a;
    }
}