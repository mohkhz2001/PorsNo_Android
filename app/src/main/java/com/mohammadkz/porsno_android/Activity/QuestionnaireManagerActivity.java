package com.mohammadkz.porsno_android.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Adapter.ConfirmNewQuestionaireAdapter;
import com.mohammadkz.porsno_android.Fragment.HistoryFragment;
import com.mohammadkz.porsno_android.Fragment.History_BuyFragment;
import com.mohammadkz.porsno_android.Fragment.History_DoneFragment;
import com.mohammadkz.porsno_android.Fragment.ManageQuestionnaire.Manager_infoFragment;
import com.mohammadkz.porsno_android.Fragment.ManageQuestionnaire.Manager_questionsFragment;
import com.mohammadkz.porsno_android.Model.Advice;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.GetAnswer24;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionnaireManagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    MainAdapter adapter;
    ApiConfig request;
    Questionnaire questionnaire;
    MaterialToolbar topAppBar;
    List<Advice> adviceList = new ArrayList<>();
    int done24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_manager);

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        SweetDialog.setSweetDialog(new SweetAlertDialog(QuestionnaireManagerActivity.this, SweetAlertDialog.PROGRESS_TYPE));

        initViews();
        controllerViews();
        calculateInfo();
        getQuestion();

    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        topAppBar = findViewById(R.id.topAppBar);
    }

    private void controllerViews() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    tabLayout.setSelectedTabIndicator(R.drawable.bg_tab_select_l);
                } else {
                    tabLayout.setSelectedTabIndicator(R.drawable.bg_tab_select_r);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        topAppBar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void calculateInfo() {

    }

    private String getQuestionId() {
        return getIntent().getStringExtra("qId");
    }

    private void getQuestion() {
        SweetDialog.setSweetDialog(new SweetAlertDialog(QuestionnaireManagerActivity.this, SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();

        Call<GetQuestionResponse> get = request.getQuestion("qId", getQuestionId());

        get.enqueue(new Callback<GetQuestionResponse>() {
            @Override
            public void onResponse(Call<GetQuestionResponse> call, Response<GetQuestionResponse> response) {
                if (response.body() != null) {
                    questionnaire = new Questionnaire();
                    parseDate(response.body());

                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                    SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            QuestionnaireManagerActivity.super.finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetQuestionResponse> call, Throwable t) {
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        QuestionnaireManagerActivity.super.finish();
                    }
                });
            }
        });

    }

    private void parseDate(GetQuestionResponse data) {

        questionnaire.setName(data.getQuestionName());
        questionnaire.setId(data.getQuestionId());
        questionnaire.setDescription(data.getDescription());
        questionnaire.setUserId(data.getUserId());
        questionnaire.setEndDate_stamp(data.getEnd());
        questionnaire.setStartDate_stamp(data.getStart());
        questionnaire.setViews(data.getViews());
        questionnaire.setDone(data.getAnswers());

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

            getComment();
        } catch (JSONException e) {
            e.printStackTrace();
            super.finish();
            Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
            SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    QuestionnaireManagerActivity.super.finish();
                }
            });
        }

    }

    private void getComment() {

        Call<List<Advice>> get = request.getComment(getQuestionId());

        get.enqueue(new Callback<List<Advice>>() {
            @Override
            public void onResponse(Call<List<Advice>> call, Response<List<Advice>> response) {

                if (response.body().size() >= 0) {
                    adviceList = response.body();
                    setDone24();
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                    SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            QuestionnaireManagerActivity.super.finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Advice>> call, Throwable t) {
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        QuestionnaireManagerActivity.super.finish();
                    }
                });
            }
        });
    }

    private void setAdapter() {
        adapter = new MainAdapter(getSupportFragmentManager());

        if (questionnaire != null)
            adapter.addFragment(new Manager_infoFragment(questionnaire, done24, adviceList), "اطلاعات");
        if (questionnaire != null)
            adapter.addFragment(new Manager_questionsFragment(questionnaire), "پرسشنامه");

        SweetDialog.stopProgress();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setDone24() {

        Call<GetAnswer24> get = request.getDone24(getQuestionId());

        get.enqueue(new Callback<GetAnswer24>() {
            @Override
            public void onResponse(Call<GetAnswer24> call, Response<GetAnswer24> response) {
                if (response.body().getStatus_code().equals("200")) {
                    done24 = Integer.parseInt(response.body().getCount());
                    setAdapter();
                } else {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                    SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            QuestionnaireManagerActivity.super.finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetAnswer24> call, Throwable t) {
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        QuestionnaireManagerActivity.super.finish();
                    }
                });
            }
        });
    }

    private class MainAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        ArrayList<String> stringArrayList = new ArrayList<>();

        public void addFragment(Fragment fragment, String string) {
            fragmentArrayList.add(fragment);
            stringArrayList.add(string);
        }


        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            return fragmentArrayList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return stringArrayList.get(position);
        }
    }
}