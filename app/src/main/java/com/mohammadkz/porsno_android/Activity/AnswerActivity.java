package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Adapter.ConfirmNewQuestionaireAdapter;
import com.mohammadkz.porsno_android.Adapter.QuestionAdapter;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.QuestionAnswer;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ApiConfig request;
    Questionnaire questionnaire;
    RecyclerView list;
    MaterialToolbar topAppBar;
    ConfirmNewQuestionaireAdapter adapter;
    Button done;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("منتظر باشید...");


        request = AppConfig.getRetrofit().create(ApiConfig.class);

        initViews();
        controllerViews();
        getQuestion();
        getDate();

    }

    private void initViews() {
        list = findViewById(R.id.list);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitle("در حال دریافت اطلاعات");
        done = findViewById(R.id.done);
    }

    private void controllerViews() {
        topAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (adapter != null && user != null) {
                    save();
                }
            }
        });
    }

    private String getQuestionId() {
        return getIntent().getStringExtra("qId");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getQuestion() {
        progressDialog.show();
        Call<GetQuestionResponse> get = request.getQuestion("qId", getQuestionId());

        get.enqueue(new Callback<GetQuestionResponse>() {
            @Override
            public void onResponse(Call<GetQuestionResponse> call, Response<GetQuestionResponse> response) {
                if (response.body() != null) {
                    questionnaire = new Questionnaire();
                    parseDate(response.body());

                } else {
                    progressDialog.dismiss();
                    StaticFun.alertDialog_connectionFail(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<GetQuestionResponse> call, Throwable t) {
                progressDialog.dismiss();
                StaticFun.alertDialog_connectionFail(getApplicationContext());
            }
        });

    }

    private void parseDate(GetQuestionResponse data) {
        questionnaire.setName(data.getQuestionName());
        topAppBar.setTitle(questionnaire.getName());
        questionnaire.setId(data.getQuestionId());
        questionnaire.setUserId(data.getUserId());

//        if (questionnaire.getUserId().equals(user.getID()))
//            done.setVisibility(View.GONE);

        List<Question> questions = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data.getQuestion());

            for (int i = 0; i < jsonArray.length(); i++) {
                Question question = new Question();

                if (jsonArray.getJSONObject(i).getString("test").equals("true")) {
                    question.setTest(true);
                    question.setQuestion(jsonArray.getJSONObject(i).getString("question"));
                    JSONArray answers_josn = jsonArray.getJSONObject(i).getJSONArray("answers");

                    List<Answer> answers = new ArrayList<>();
                    for (int j = 0; j < answers_josn.length(); j++) {
                        Answer answer = new Answer(answers_josn.getJSONObject(j).getString("answer"));

                        answers.add(answer);
                    }
                    question.setAnswers(answers);
                } else {
                    question.setTest(false);
                    question.setQuestion(jsonArray.getJSONObject(i).getString("question"));
                }
                questions.add(question);

            }
            questionnaire.setQuestions(questions);
            setAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
            super.finish();
            Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
        }

    }

    private void setAdapter() {
        adapter = new ConfirmNewQuestionaireAdapter(getApplicationContext(), questionnaire.getQuestions(), false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(linearLayoutManager);

        list.setAdapter(adapter);

        progressDialog.dismiss();
    }

    private void save() {
        List<QuestionAnswer> list = adapter.answers();
        Gson gson = new Gson();
        String json = gson.toJson(list);

        Call<NormalResponse> get = request.saveAnswers(user.getID(), user.getName(), setCreatedTime(), json, questionnaire.getId());

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private String setCreatedTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp.getTime());
        return timestamp.getTime() + "";
    }

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

                if (jsonObject.getString("accountLevel").equals("Bronze")) {
                    user.setAccountLevel(StaticFun.account.Bronze);
                } else if (jsonObject.getString("accountLevel").equals("Steel")) {
                    user.setAccountLevel(StaticFun.account.Steel);
                } else if (jsonObject.getString("accountLevel").equals("Gold")) {
                    user.setAccountLevel(StaticFun.account.Gold);
                } else if (jsonObject.getString("accountLevel").equals("Diamond")) {
                    user.setAccountLevel(StaticFun.account.Diamond);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}