package com.mohammadkz.porsno_android.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Adapter.AnswerHistoryAdapter;
import com.mohammadkz.porsno_android.Model.Response.AnswerHistoryResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class History_DoneFragment extends Fragment {

    View view;
    ApiConfig request;
    User user;
    RecyclerView list;
    SwipeRefreshLayout swipeRefresh;

    public History_DoneFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_history_done, container, false);
            request = AppConfig.getRetrofit().create(ApiConfig.class);


            initViews();
            controllerViews();
            getData();

            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "history done fragment - create");
            return view;
        }
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
    }

    private void controllerViews() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void getData() {

        Call<List<AnswerHistoryResponse>> get = request.getAnswerHistory(user.getID());

        get.enqueue(new Callback<List<AnswerHistoryResponse>>() {
            @Override
            public void onResponse(Call<List<AnswerHistoryResponse>> call, Response<List<AnswerHistoryResponse>> response) {
                if (response.body().size() > 0) {
                    setAdapter(response.body());
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            "-"
                            , "history done fragment - get data / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                }
            }

            @Override
            public void onFailure(Call<List<AnswerHistoryResponse>> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "history done fragment - get data / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
            }
        });
    }

    private void setAdapter(List<AnswerHistoryResponse> list) {
        AnswerHistoryAdapter answerHistoryAdapter = new AnswerHistoryAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.list.setLayoutManager(linearLayoutManager);
        this.list.setAdapter(answerHistoryAdapter);

        SweetDialog.stopProgress();
    }
}