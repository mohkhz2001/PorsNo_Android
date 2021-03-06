package com.mohammadkz.porsno_android.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
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
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerActivity extends AppCompatActivity {

    ApiConfig request;
    Questionnaire questionnaire;
    RecyclerView list;
    LinearLayoutManager linearLayoutManager;
    MaterialToolbar topAppBar;
    ConfirmNewQuestionaireAdapter adapter;
    Button done;
    User user;
    BottomSheetDialog bottomSheetDialog;
    boolean onQueue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_answer);

            SweetDialog.setSweetDialog(new SweetAlertDialog(AnswerActivity.this, SweetAlertDialog.PROGRESS_TYPE));

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            if (getIntent().getStringExtra("user-name") != null && getIntent().getStringExtra("user-id") != null) {
                user = new User();
                user.setID(getIntent().getStringExtra("user-id"));
                user.setName(getIntent().getStringExtra("user-name"));
            } else if (getIntent().getStringExtra("queue") != null) {
                onQueue = true;
            }

            initViews();
            controllerViews();
            getQuestion();
            getDate();
        } catch (Exception e) {
            Toasty.error(getApplicationContext(), "???????????????? ???? ???????????? ?????????????? ???? ???????? ?????????? ????????", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "Answer Activity - create");
        }

    }

    private void initViews() {
        list = findViewById(R.id.list);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitle("???? ?????? ???????????? ??????????????");
        done = findViewById(R.id.done);
    }

    private void controllerViews() {
        topAppBar.setNavigationOnClickListener(v -> {
            SweetDialog.getSweetAlertDialog().show();
            SweetDialog.changeSweet(SweetAlertDialog.WARNING_TYPE, "?????? ???? ???????? ?????? ?????????????? ????????????", "???????? ?????? ?????? ?????????? ?????????????? ????.");
            SweetDialog.getSweetAlertDialog().setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    AnswerActivity.super.finish();
                }
            });
            SweetDialog.getSweetAlertDialog().setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    SweetDialog.stopProgress();
                }
            });
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null && user != null) {
                    bottomSheetAdvice();
                }
            }
        });

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
                        shareIntent();
                        return true;
                    case R.id.report:
                        bottomSheetReport();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private String getQuestionId() {
        return getIntent().getStringExtra("qId");
    }

    @Override
    public void onBackPressed() {

        SweetDialog.startProgress();
        SweetDialog.changeSweet(SweetAlertDialog.WARNING_TYPE, "????????", "???? ???????? ???????? ???????? ?????? ?????? ?????????? ?????? ????????.\n?????? ???? ???????? ?????? ?????????????? ????????????");
        SweetDialog.getSweetAlertDialog()
                .setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        AnswerActivity.super.onBackPressed();
                    }
                });

    }

    private void getQuestion() {
        SweetDialog.setSweetDialog(new SweetAlertDialog(AnswerActivity.this, SweetAlertDialog.PROGRESS_TYPE), "???? ?????? ???????????? ??????????????", "???????? ?????????? ??????????...");
        SweetDialog.startProgress();

        Call<GetQuestionResponse> get = request.getQuestion("qId", getQuestionId());

        get.enqueue(new Callback<GetQuestionResponse>() {
            @Override
            public void onResponse(Call<GetQuestionResponse> call, Response<GetQuestionResponse> response) {
                if (response.body() != null) {
                    questionnaire = new Questionnaire();
                    parseDate(response.body());

                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"), "-"
                            , "answer Activity - get question / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????? ???? ???????????? ??????????????", "?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                    SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            AnswerActivity.super.finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetQuestionResponse> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "answer Activity - get question / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????? ???? ?????????????? ????????????", "?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        AnswerActivity.super.finish();
                    }
                });
            }
        });

    }

    private void parseDate(GetQuestionResponse data) {
        questionnaire.setName(data.getQuestionName());
        topAppBar.setTitle(questionnaire.getName());
        questionnaire.setId(data.getQuestionId());
        questionnaire.setUserId(data.getUserId());


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
            Toasty.error(getApplicationContext(), "???????????????? ???? ???????????? ?????????????? ???? ???????? ?????????? ????????", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "Answer Activity - parse date");
            finish();
        }

    }

    private void setAdapter() {
        adapter = new ConfirmNewQuestionaireAdapter(getApplicationContext(), questionnaire.getQuestions(), false);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(linearLayoutManager);

        list.setAdapter(adapter);
        done.setVisibility(View.VISIBLE);

        SweetDialog.stopProgress();
    }

    private void save(String comment) {
        SweetDialog.startProgress();
        List<QuestionAnswer> list = adapter.answers();
        Gson gson = new Gson();
        String json = gson.toJson(list);

        Call<NormalResponse> get = request.saveAnswers(user.getID(), user.getName(), setCreatedTime(), json, questionnaire.getId(), comment.length() == 0 ? "-" : comment.toString());

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "???????? ?????? ?????? ???? ???????????? ?????? ????????", "");
                    SweetDialog.getSweetAlertDialog().setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            AnswerActivity.super.finish();
                        }
                    });

                    // remove the question from the share prefrences
                    if (onQueue)
                        removeQueue();

                } else if (response.body().getMessage().equals("User is spaming")) {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "?????????? ??????????", "?????? ?????? ?????????? ???????? ???? ?????????? ???????? ?????? ???????????????? ??????????.");
                    SweetDialog.getSweetAlertDialog()
                            .setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????? ???? ?????????????? ????????????", "?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                    SweetDialog.getSweetAlertDialog()
                            .setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    save(comment);
                                }
                            });

                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            response.body().getMessage().length() > 0 ? response.body().getMessage() + " - " + response.body().getStatus_code() : "-"
                            , "answer Activity - save / response");

                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "???????????????? ???? ???????????? ?????????????? ???? ???????? ?????????? ????????", Toasty.LENGTH_LONG, true).show();
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), t.getMessage().toString(), "answer Activity - save / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????? ???? ?????????????? ????????????", "?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                    }
                });
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
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "Answer Activity - get date");
                getDate();
            }
    }

    private void bottomSheetAdvice() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AnswerActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(AnswerActivity.this).inflate(R.layout.layout_bottom_sheet_advice, (LinearLayout) findViewById(R.id.layout));

        bottomSheetView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText advice = bottomSheetView.findViewById(R.id.advice);
                bottomSheetDialog.dismiss();
                save(advice.getText().toString());

            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void shareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String body = user.getName() + "\n?????? ???? ???? ?????????? ???????????????? ?? ?????? ???????? ???????? ??????.\n" + "http://www.porsno.ir/questions/question.php?id=" + questionnaire.getId() +
                "\n * ?????? ???????? ???????? ?????? ?????? ???????? ???????????? ?????????? ???? ?????? *\n";
        intent.putExtra(intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "choose app"));
    }

    private void reportApi(String txt) {
        SweetDialog.startProgress();

        Call<NormalResponse> get = request.addReport(questionnaire.getId(), user.getID(), txt);
        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "?????????? ?????? ???? ???????????? ?????? ????", "???? ???????? ???? ??????");
                    SweetDialog.getSweetAlertDialog().setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Toasty.success(getApplicationContext(), "", Toasty.LENGTH_LONG, true).show();
                            AnswerActivity.super.finish();
                        }
                    });
                } else if (response.body().getStatus_code().equals("401")) {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "?????????? ??????????", "?????? ?????? ???? ?????? ???????? ???? ?????????? ???? ???????????????? ??????????.");
                    SweetDialog.getSweetAlertDialog().setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Toasty.success(getApplicationContext(), "", Toasty.LENGTH_LONG, true).show();
                            AnswerActivity.super.finish();
                        }
                    });
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????? ???? ?????????????? ????????????", "?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                    SweetDialog.getSweetAlertDialog().setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            reportApi(txt);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "???????????????? ???? ???????????? ?????????????? ???? ???????? ?????????? ????????", Toasty.LENGTH_LONG, true).show();
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), t.getMessage().toString(), "answer Activity - report / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????? ???? ?????????????? ????????????", "?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                    }
                });
            }
        });
    }

    private void bottomSheetReport() {
        bottomSheetDialog = new BottomSheetDialog(AnswerActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(AnswerActivity.this).inflate(R.layout.layout_bottom_sheet_advice, (LinearLayout) findViewById(R.id.layout));

        bottomSheetView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText advice = bottomSheetView.findViewById(R.id.advice);
                if (advice.getText().length() > 0) {
                    bottomSheetDialog.dismiss();
                    reportApi(advice.getText().toString());
                } else {
                    Toasty.error(AnswerActivity.this, "?????????? ?????????? ???????? ???????? ???????????????? ???? ???? ???????? ?????? ?????????? ?????????? ????????.", Toasty.LENGTH_LONG, true).show();
                    advice.setError("?????????? ?????????? ???????? ???????? ???????????????? ???? ???? ???????? ?????? ?????????? ?????????? ????????.");
                }


            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void removeQueue() {
        SharedPreferences sh = getSharedPreferences("question-queue", MODE_PRIVATE);
        String a = sh.getString("queue", "");

        if (!a.equals("")) {

            SharedPreferences.Editor editor = sh.edit();
            editor.clear();
            editor.commit();
        }
    }
}