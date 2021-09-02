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
import android.widget.TextView;

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
    List<AnswerHistoryResponse> doneList;
    TextView txt;

    public History_DoneFragment(User user, List<AnswerHistoryResponse> doneList) {
        // Required empty public constructor
        this.user = user;
        this.doneList = doneList;
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
            if (doneList.size() == 0) {
                txt.setVisibility(View.VISIBLE);
            } else
                setAdapter();

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
        txt = view.findViewById(R.id.txt);
    }

    private void controllerViews() {

    }

    private void setAdapter() {
        AnswerHistoryAdapter answerHistoryAdapter = new AnswerHistoryAdapter(getContext(), doneList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.list.setLayoutManager(linearLayoutManager);
        this.list.setAdapter(answerHistoryAdapter);

        SweetDialog.stopProgress();
    }
}