package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.PriceResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.joda.time.Duration;
import org.joda.time.Interval;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReBuyFragment extends Fragment {

    View view;
    User user;
    ApiConfig request;
    ProgressDialog progressDialog;
    Button diamondPrice_btn, goldPrice_btn, steelPrice_btn, bronzePrice_btn;
    TextView diamondPrice_txt, goldPrice_txt, steelPrice_txt, bronzePrice_txt, dayLeft, accountLevel;

    public ReBuyFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_re_buy, container, false);

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("در حال دریافت اطلاعات");
        progressDialog.show();
        initViews();
        controllerViews();
        getPrice();

        return view;
    }

    private void initViews() {
        diamondPrice_btn = view.findViewById(R.id.diamondPrice_btn);
        goldPrice_btn = view.findViewById(R.id.goldPrice_btn);
        steelPrice_btn = view.findViewById(R.id.steelPrice_btn);
        bronzePrice_btn = view.findViewById(R.id.bronzePrice_btn);
        diamondPrice_txt = view.findViewById(R.id.diamondPrice_txt);
        goldPrice_txt = view.findViewById(R.id.goldPrice_txt);
        steelPrice_txt = view.findViewById(R.id.steelPrice_txt);
        bronzePrice_txt = view.findViewById(R.id.bronzePrice_txt);
        dayLeft = view.findViewById(R.id.dayLeft);
        accountLevel = view.findViewById(R.id.accountLevel);
    }

    private void controllerViews() {
        diamondPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeAccount("diamond");
            }
        });
        bronzePrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeAccount("bronze");
            }
        });
        goldPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeAccount("gold");
            }
        });
        steelPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeAccount("steel");
            }
        });
    }

    private void getPrice() {

        Call<List<PriceResponse>> get = request.getPrice();

        get.enqueue(new Callback<List<PriceResponse>>() {
            @Override
            public void onResponse(Call<List<PriceResponse>> call, Response<List<PriceResponse>> response) {
                if (response.body().size() > 0) {

                    setValue(response.body().get(0).getCost(), response.body().get(1).getCost(), response.body().get(2).getCost(), response.body().get(3).getCost());

                } else {
                    StaticFun.alertDialog_connectionFail(getContext());
                }

            }

            @Override
            public void onFailure(Call<List<PriceResponse>> call, Throwable t) {
                System.out.println();
                StaticFun.alertDialog_connectionFail(getContext());
            }
        });
    }

    private void setValue(String bronze, String steel, String gold, String diamond) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        bronzePrice_txt.setText(decimalFormat.format(Integer.parseInt(bronze)) + " تومان");
        goldPrice_txt.setText(decimalFormat.format(Integer.parseInt(gold)) + " تومان");
        steelPrice_txt.setText(decimalFormat.format(Integer.parseInt(steel)) + " تومان");
        diamondPrice_txt.setText(decimalFormat.format(Integer.parseInt(diamond)) + " تومان");
        parseDate();

        progressDialog.dismiss();
    }

    private void parseDate() {
        try {
            Timestamp today = new Timestamp(System.currentTimeMillis());
//            Date today = new Date(ts.getTime());

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            Date end = new Date(Long.parseLong(user.getEndTime() ));
            System.out.println(sf.format(end));

            Interval interval = new Interval(today.getTime(), Long.parseLong(user.getEndTime()+"000"));
            Duration period = interval.toDuration();

            dayLeft.setText(period.getStandardDays() + "");
        } catch (Exception e) {
            dayLeft.setText("با واحد پشتیبانی تماس حاصل فرمایید");
        }


    }

    private void upgradeAccount(String level) {

        Call<NormalResponse> get = request.upgradeAccount(level, user.getID());

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                refresh();
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                StaticFun.alertDialog_connectionFail(getContext());
            }
        });
    }

    public void refresh() {
        getPrice();
    }
}