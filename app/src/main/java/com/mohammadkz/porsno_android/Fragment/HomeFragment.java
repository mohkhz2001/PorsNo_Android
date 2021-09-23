package com.mohammadkz.porsno_android.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Activity.EditQuestionActivity;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Activity.QuestionnaireManagerActivity;
import com.mohammadkz.porsno_android.Adapter.QuestionAdapter;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.joda.time.Duration;
import org.joda.time.Interval;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    View view;
    RecyclerView myQuestionList;
    User user;
    TextView emptyList, userName, questionLeft, daysLeft;
    Button reBuy;
    CardView newQuestion;
    ApiConfig request;
    SwipeRefreshLayout swipeRefresh;
    BottomSheetDialog bottomSheetDialogMore;
    boolean showQueue = false;
    GetQuestionResponse questionResponse;


    public HomeFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_home, container, false);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");

            initViews();
            controllerView();
            getData();
            setValue();
            bottomSheetMore();

            return view;
        } catch (Exception e) {
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "my question fragment - create");
            return null;
        }

    }

    private void initViews() {
        myQuestionList = view.findViewById(R.id.myQuestionList);
        myQuestionList.setHasFixedSize(true);
        emptyList = view.findViewById(R.id.emptyList);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        userName = view.findViewById(R.id.userName);
        questionLeft = view.findViewById(R.id.questionLeft);
        daysLeft = view.findViewById(R.id.daysLeft);
        reBuy = view.findViewById(R.id.reBuy);
        newQuestion = view.findViewById(R.id.newQuestion);
    }

    private void controllerView() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefresh.setRefreshing(false);
            }
        });

        reBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(user.getQuestionRemaining()) > 0)
                    newQuestion();
                else
                    Toasty.error(getContext(), "پرسشنامه های شما به اتمام رسیده است.", Toasty.LENGTH_LONG, true).show();
            }
        });
    }

    private void getData() {
        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();
        Call<List<GetQuestionResponse>> get = request.getQuestions("uId", user.getID());


        get.enqueue(new Callback<List<GetQuestionResponse>>() {
            @Override
            public void onResponse(Call<List<GetQuestionResponse>> call, Response<List<GetQuestionResponse>> response) {
                if (response.body() != null) {

                    setAdapter(response.body().size() >= 0 ? response.body() : (new ArrayList<GetQuestionResponse>()));
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            ""
                            , "my question fragment - get date / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                }
            }

            @Override
            public void onFailure(Call<List<GetQuestionResponse>> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "my question fragment - get date / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
            }
        });
    }

    private void setAdapter(List<GetQuestionResponse> questionnaireList) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myQuestionList.setLayoutManager(linearLayoutManager);
        QuestionAdapter questionAdapter;

        if (questionnaireList.size() > 0) {

            questionAdapter = new QuestionAdapter(getContext(), questionnaireList, true);
            myQuestionList.setAdapter(questionAdapter);
            SweetDialog.stopProgress();
            emptyList.setVisibility(View.GONE);
            questionAdapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int pos, View v) {
                    Log.e("qId", " " + pos);
                    Intent intent = new Intent(getContext(), QuestionnaireManagerActivity.class);
                    intent.putExtra("qId", questionnaireList.get(pos).getQuestionId());
                    intent.putExtra("user-name", user.getName());
                    transferData(intent);
                    startActivity(intent);
                }
            });

            questionAdapter.setOnMoreItemClickListener(new QuestionAdapter.OnMoreItemClickListener() {
                @Override
                public void onMoreItemClick(int pos) {
                    questionResponse = questionnaireList.get(pos);
                    bottomSheetDialogMore.show();
                }
            });
        } else {
            SweetDialog.stopProgress();
            emptyList.setVisibility(View.VISIBLE);
        }

        if (!showQueue) {
            Log.e("te", "");
            checkQueue();
            showQueue = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SweetDialog.stopProgress();
        getData();
    }

    private void transferData(Intent intent) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        intent.putExtra("userInfo", json);

    }

    private void checkQueue() {
        SharedPreferences sh = getContext().getSharedPreferences("question-queue", MODE_PRIVATE);
        String qId = sh.getString("queue", "");

        if (!qId.equals("")) {
            bottomSheetQueue(qId);
        }

    }

    private void bottomSheetQueue(String qId) {
        Log.e("aaa", "aaa");
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_sheet_queue, (ConstraintLayout) view.findViewById(R.id.layout));

        // later
        bottomSheetView.findViewById(R.id.later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        // affirm to done
        bottomSheetView.findViewById(R.id.affirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AnswerActivity.class);
                intent.putExtra("qId", qId);
                intent.putExtra("queue", "true");
                intent.putExtra("user-name", user.getName());
                transferData(intent);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        // never btn
        bottomSheetView.findViewById(R.id.never).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh = getContext().getSharedPreferences("question-queue", MODE_PRIVATE);
                String a = sh.getString("queue", "");

                if (!a.equals("")) {

                    SharedPreferences.Editor editor = sh.edit();
                    editor.clear();
                    editor.commit();
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
                SweetDialog.stopProgress();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    private void bottomSheetMore() {
        bottomSheetDialogMore = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_sheet_more, (ConstraintLayout) view.findViewById(R.id.layout));

        bottomSheetView.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bottomSheetView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditQuestionActivity.class);
                intent.putExtra("qId", questionResponse.getQuestionId());
                intent.putExtra("user-name", user.getName());
                transferData(intent);
                startActivity(intent);
            }
        });

        bottomSheetView.findViewById(R.id.checkQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bottomSheetView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent();
            }
        });

        bottomSheetView.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bottomSheetDialogMore.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialogMore.dismiss();
            }
        });

        bottomSheetDialogMore.setContentView(bottomSheetView);
    }

    private void shareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String body = user.getName() + "\nشما را به انجام پرسشنامه ی زیر دعوت کرده است.\n" + "http://www.porsno.ir/questions/question.php?id=" + questionResponse.getQuestionId() +
                "\n * این پیام صرفا جهت تست توسط برنامه ارسال می شود *\n";
        intent.putExtra(intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "choose app"));
    }

    private void setValue() {
        userName.setText(user.getName());
        questionLeft.setText(user.getQuestionRemaining());
        daysLeft.setText(parseDate());
    }

    // calculate the left day of the user account
    private String parseDate() {
        try {
            Timestamp today = new Timestamp(System.currentTimeMillis());

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            Date end = new Date(Long.parseLong(user.getEndTime()));
            System.out.println(sf.format(end));

            Interval interval = new Interval(today.getTime(), Long.parseLong(user.getEndTime() + "000"));
            Duration period = interval.toDuration();

            return period.getStandardDays() + "";
        } catch (Exception e) {
            StaticFun.setLog((user == null) ? "-"
                            : (user.getPn().length() > 0 ? user.getPn() : "-"),
                    e.getMessage().toString()
                    , "re buy fragment - parse date ");

            return "---";
        }
    }

    private void newQuestion() {
        Intent intent = new Intent(getContext(), NewQuestionActivity.class);
        Gson gson = new Gson();
        String a = gson.toJson(user);
        intent.putExtra("userInfo", a);//user.getID()
        startActivity(intent);
    }
}