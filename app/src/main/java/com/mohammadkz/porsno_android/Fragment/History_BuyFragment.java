package com.mohammadkz.porsno_android.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Adapter.BuyHistoryAdapter;
import com.mohammadkz.porsno_android.Model.Response.HistoryBuyResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class History_BuyFragment extends Fragment {

    View view;
    RecyclerView list;
    ApiConfig request;
    User user;
    List<HistoryBuyResponse> buyList;
    TextView txt;

    public History_BuyFragment(User user, List<HistoryBuyResponse> buyList) {
        // Required empty public constructor
        this.user = user;
        this.buyList = buyList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_history_buy, container, false);
            request = AppConfig.getRetrofit().create(ApiConfig.class);

            initViews();
            controllerViews();
            if (buyList.size() == 0) {
                txt.setVisibility(View.VISIBLE);
            } else
                setAdapter();


            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "history buy fragment - create");
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
        BuyHistoryAdapter buyHistoryAdapter = new BuyHistoryAdapter(getContext(), buyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(buyHistoryAdapter);
        SweetDialog.stopProgress();
    }
}