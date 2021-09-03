package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Adapter.QuestionAdapter;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllQuestionFragment extends Fragment {

    View view;
    RecyclerView list;
    ApiConfig request;
    CardView filter_card;
    TextView emptyList;
    User user;
    SwipeRefreshLayout swipeRefresh;
    EditText searchEdt;
    List<GetQuestionResponse> allList;
    List<GetQuestionResponse> toDisplay = new ArrayList<>();

    public AllQuestionFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_all_question, container, false);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            SweetDialog.setSweetDialog(new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");

            initViews();
            controllerViews();
            getData();

            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-" : (user.getPn().length() > 0 ? user.getPn() : "-")
                    , e.getMessage().toString()
                    , "all question fragment - create");
            return view;
        }
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        emptyList = view.findViewById(R.id.emptyList);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        searchEdt = view.findViewById(R.id.searchEdt);
        filter_card = view.findViewById(R.id.filter_card);
        YoYo.with(Techniques.SlideInDown)
                .duration(300)
                .repeat(0)
                .playOn(view.findViewById(R.id.list));
    }

    private void controllerViews() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefresh.setRefreshing(false);
            }
        });

        getParentFragmentManager().setFragmentResultListener("visibility_data", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.e("test data ", result.getString("visibility").toString());
                boolean visibility = result.getString("visibility") == "true";

                if (visibility) {
                    filter_card.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInDown)
                            .duration(300)
                            .repeat(0)
                            .playOn(view.findViewById(R.id.filter_card));
                } else {
                    YoYo.with(Techniques.SlideInUp)
                            .duration(600)
                            .repeat(0)
                            .playOn(view.findViewById(R.id.filter_card));
                    filter_card.setVisibility(View.GONE);
                }
            }
        });

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    searchName(editable.toString());
            }
        });

    }

    private void getData() {
        SweetDialog.setSweetDialog(new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();
        Call<List<GetQuestionResponse>> get = request.getQuestions("-", "-");


        get.enqueue(new Callback<List<GetQuestionResponse>>() {
            @Override
            public void onResponse(Call<List<GetQuestionResponse>> call, Response<List<GetQuestionResponse>> response) {
                if (response.body().size() >= 0) {
                    allList = response.body();
                    toDisplay = allList;
                    setAdapter();
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            response.body() != null ? "-" : "response null"
                            , "all question fragment - get data / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                }
            }

            @Override
            public void onFailure(Call<List<GetQuestionResponse>> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "all question fragment - get data / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
            }
        });
    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(linearLayoutManager);

        QuestionAdapter questionAdapter;

        if (allList.size() > 0) {

            questionAdapter = new QuestionAdapter(view.getContext(), toDisplay);
            list.setAdapter(questionAdapter);
            SweetDialog.stopProgress();
            emptyList.setVisibility(View.GONE);
            questionAdapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int pos, View v) {
                    Log.e("qId", " " + pos);
                    Intent intent = new Intent(view.getContext(), AnswerActivity.class);
                    intent.putExtra("qId", toDisplay.get(pos).getQuestionId());
                    transferData(intent);
                    startActivity(intent);
                }
            });
        } else {
            SweetDialog.stopProgress();
            emptyList.setVisibility(View.VISIBLE);
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
        SweetDialog.stopProgress();
        getData();
    }

    private void searchName(String name) {
        toDisplay = new ArrayList<>();
        for (int i = 0; i < allList.size(); i++) {
            if (allList.get(i).getQuestionName().contains(name)) {
                toDisplay.add(allList.get(i));
            }
        }
        setAdapter();
    }
}