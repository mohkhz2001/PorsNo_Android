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
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;

import java.io.IOException;
import java.util.List;

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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_done, container, false);
        request = AppConfig.getRetrofit().create(ApiConfig.class);

        initViews();
        controllerViews();
        getData();

        return view;
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
                    Log.e("test", "1shiit");
                }
            }

            @Override
            public void onFailure(Call<List<AnswerHistoryResponse>> call, Throwable t) {
                Log.e("test", "shiit");
            }
        });
    }

    private void setAdapter(List<AnswerHistoryResponse> list) {
        AnswerHistoryAdapter answerHistoryAdapter = new AnswerHistoryAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.list.setLayoutManager(linearLayoutManager);
        this.list.setAdapter(answerHistoryAdapter);
    }
}