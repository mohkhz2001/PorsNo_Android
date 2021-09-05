package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Activity.MainPageActivity;
import com.mohammadkz.porsno_android.Activity.WebViewActivity;
import com.mohammadkz.porsno_android.Model.PriceResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.Response.UpgradeResponse;
import com.mohammadkz.porsno_android.Model.Response.UrlResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReBuyFragment extends Fragment {

    View view;
    User user;
    ApiConfig request;
    Button diamondPrice_btn, goldPrice_btn, steelPrice_btn, bronzePrice_btn;
    TextView diamondPrice_txt, goldPrice_txt, steelPrice_txt, bronzePrice_txt, dayLeft, accountLevel;
    ConstraintLayout root;
    boolean webViewOpen = false;
    List<PriceResponse> priceResponse;

    public ReBuyFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_re_buy, container, false);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE));

            initViews();
            controllerViews();
            getPrice();

            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "re but fragment - create");
            return view;
        }

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
        root = view.findViewById(R.id.root);
    }

    private void controllerViews() {
        diamondPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                upgradeAccount("diamond");
                generatePayUrl(priceResponse.get(3).getCost() , "diamond");
            }
        });

        bronzePrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                upgradeAccount("bronze");
                generatePayUrl(priceResponse.get(0).getCost() , "bronze");
            }
        });

        goldPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                upgradeAccount("gold");
                generatePayUrl(priceResponse.get(2).getCost() , "gold");
            }
        });

        steelPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                upgradeAccount("steel");
                generatePayUrl(priceResponse.get(1).getCost() , "steel");
            }
        });
    }

    private void getPrice() {
        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();

        Call<List<PriceResponse>> get = request.getPrice();

        get.enqueue(new Callback<List<PriceResponse>>() {
            @Override
            public void onResponse(Call<List<PriceResponse>> call, Response<List<PriceResponse>> response) {
                if (response.body().size() > 0) {
                    priceResponse = response.body();
                    setValue(response.body().get(0).getCost(), response.body().get(1).getCost(), response.body().get(2).getCost(), response.body().get(3).getCost());

                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"), "-"
                            , "re buy fragment - get price / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                    SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            getPrice();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<List<PriceResponse>> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "re buy fragment - get price / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        getPrice();
                    }
                });
            }
        });
    }

    private void setValue(String bronze, String steel, String gold, String diamond) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        bronzePrice_txt.setText(decimalFormat.format(Integer.parseInt(bronze)) + " تومان");
        goldPrice_txt.setText(decimalFormat.format(Integer.parseInt(gold)) + " تومان");
        steelPrice_txt.setText(decimalFormat.format(Integer.parseInt(steel)) + " تومان");
        diamondPrice_txt.setText(decimalFormat.format(Integer.parseInt(diamond)) + " تومان");

        if (user.getAccountLevel() == StaticFun.account.Bronze) {
            accountLevel.setText("برنز");
        } else if (user.getAccountLevel() == StaticFun.account.Steel) {
            accountLevel.setText("نقره ای");
        } else if (user.getAccountLevel() == StaticFun.account.Diamond) {
            accountLevel.setText("الماسی");
        } else if (user.getAccountLevel() == StaticFun.account.Gold) {
            accountLevel.setText("طلایی");
        }

        parseDate();
        root.setVisibility(View.VISIBLE);
        SweetDialog.stopProgress();
    }

    private void parseDate() {
        try {
            Timestamp today = new Timestamp(System.currentTimeMillis());

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            Date end = new Date(Long.parseLong(user.getEndTime()));
            System.out.println(sf.format(end));

            Interval interval = new Interval(today.getTime(), Long.parseLong(user.getEndTime() + "000"));
            Duration period = interval.toDuration();

            dayLeft.setText(period.getStandardDays() + "");
        } catch (Exception e) {
            StaticFun.setLog((user == null) ? "-"
                            : (user.getPn().length() > 0 ? user.getPn() : "-"),
                    e.getMessage().toString()
                    , "re buy fragment - parse date ");
            dayLeft.setText("با واحد پشتیبانی تماس حاصل فرمایید");
        }


    }

    private void upgradeAccount(String level) {

        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();

        Call<UpgradeResponse> get = request.upgradeAccount(level, user.getID());

        get.enqueue(new Callback<UpgradeResponse>() {
            @Override
            public void onResponse(Call<UpgradeResponse> call, Response<UpgradeResponse> response) {
                // should check the response
                refresh(response.body(), level);

            }

            @Override
            public void onFailure(Call<UpgradeResponse> call, Throwable t) {
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SweetDialog.changeSweet(SweetAlertDialog.PROGRESS_TYPE, "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
                    }
                });
            }
        });
    }

    public void refresh(UpgradeResponse response, String level) {
        SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "ارتقا یافت", "حساب شما ارتقا یافت.");
        user.setEndTime(response.getEnd());
        if (level.equals("bronze")) {
            user.setAccountLevel(StaticFun.account.Bronze);
            accountLevel.setText("برنز");
        } else if (level.equals("steel")) {
            user.setAccountLevel(StaticFun.account.Steel);
            accountLevel.setText("نقره ای");
        } else if (level.equals("gold")) {
            user.setAccountLevel(StaticFun.account.Gold);
            accountLevel.setText("طلایی");
        } else if (level.equals("diamond")) {
            accountLevel.setText("الماسی");
            user.setAccountLevel(StaticFun.account.Diamond);
        }

        getPrice();
        updateUserData();
    }

    private void updateUserData() {
        //i will update the user data here

        ((MainPageActivity) getActivity()).updateUser(user);

    }

    private void generatePayUrl(String price , String accountName) {
        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();

        Call<UrlResponse> get = request.getUrl(price + "0", user.getName(), user.getPn(), user.getID() , accountName);

        get.enqueue(new Callback<UrlResponse>() {
            @Override
            public void onResponse(Call<UrlResponse> call, Response<UrlResponse> response) {
                WebViewStart(response.body());
            }

            @Override
            public void onFailure(Call<UrlResponse> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "re buy fragment - generate Pay Url / failure");
            }
        });
    }

    private void WebViewStart(UrlResponse urlResponse) {
        if (urlResponse.getLink() != null && urlResponse != null && urlResponse.getLink() != null) {

            webViewOpen = true;
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("url", urlResponse.getLink());
            startActivity(intent);
        } else {
            StaticFun.setLog((user == null) ? "-"
                            : (user.getPn().length() > 0 ? user.getPn() : "-"),
                    "there is problem in get or handle link"
                    , "re buy fragment - WebViewStart");
            SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "متاسفانه امکان ارتقا حساب کاربری در حال حاظر امکان پذیر نیست.");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        SweetDialog.stopProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
