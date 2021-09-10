package com.mohammadkz.porsno_android.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.airbnb.lottie.L;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.LoginActivity;
import com.mohammadkz.porsno_android.Activity.MainPageActivity;
import com.mohammadkz.porsno_android.Activity.WebViewActivity;
import com.mohammadkz.porsno_android.Model.PriceResponse;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.Response.UpgradeResponse;
import com.mohammadkz.porsno_android.Model.Response.UrlResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;
import com.mohammadkz.porsno_android.util.IabHelper;
import com.mohammadkz.porsno_android.util.IabResult;
import com.mohammadkz.porsno_android.util.Purchase;


import org.joda.time.Duration;
import org.joda.time.Interval;
import org.json.JSONException;
import org.json.JSONObject;

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
    boolean bazarIAB = false;

    // The helper object
    IabHelper mHelper;

    public ReBuyFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {

            // setup the bazar
            String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCn4F3QKkRgV3LSOJQV6fPGb6tsys7m0HPQknNgeBVcaSE2/izyT0RXY60koR6W7xqNn8jc1Ed/6+FJM5xYA8rHPRKrEMfONyAo+xr2ggrrfKh+EPCwUK5rh1YSt4ELov4lrB4oDUKcB86QZYxPVdZMF5Lo/XBlLCMHZrtnSFiDvlhzeBreUmWyKd9vcO5fPbUUt/DBbXYtXKZAH8e9FL+6iA/A00NgD9zisgDwY3sCAwEAAQ==";

            mHelper = new IabHelper(getContext(), base64EncodedPublicKey);

            Log.d("", "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    Log.d("", "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d("", "Problem setting up In-app Billing: " + result);
                    } else
                        bazarIAB = true;

                    // Hooray, IAB is fully set up!
                    //mHelper.queryInventoryAsync(mGotInventoryListener);
                }

            });

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_re_buy, container, false);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
            SweetDialog.getSweetAlertDialog().setCancelable(false);

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

    // public fun

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
                if (bazarIAB) {
                    ((MainPageActivity) getActivity()).buy("1004");
                } else {
                    generatePayUrl(priceResponse.get(3).getCost(), "diamond");
                }
//                upgradeAccount("diamond");

            }
        });

        bronzePrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bazarIAB) {
                    ((MainPageActivity) getActivity()).buy("1001");
                } else {
                    generatePayUrl(priceResponse.get(0).getCost(), "bronze");
                }
//                upgradeAccount("bronze");
//                generatePayUrl(priceResponse.get(0).getCost(), "bronze");
            }
        });

        goldPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bazarIAB) {
                    ((MainPageActivity) getActivity()).buy("1001");

                } else {
                    generatePayUrl(priceResponse.get(2).getCost(), "gold");
                }
//                upgradeAccount("gold");
//                generatePayUrl(priceResponse.get(2).getCost(), "gold");

            }
        });

        steelPrice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bazarIAB) {
                    ((MainPageActivity) getActivity()).buy("1002");
                } else {
                    generatePayUrl(priceResponse.get(1).getCost(), "steel");
                }
//                upgradeAccount("steel");
//                generatePayUrl(priceResponse.get(1).getCost(), "steel");
            }
        });

        getParentFragmentManager().setFragmentResultListener("result", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // 101=> bronze & 102=>
                Log.d("result", result.getString("result-Purchase").toString());

                SweetDialog.startProgress();
                SweetDialog.setSweetDialog(new SweetAlertDialog(getContext()));

                String result_Purchase = result.getString("result-Purchase");
                String package_code = result.getString("package-code");
                String tracking_code = result.getString("tracking-code");

                if (result_Purchase.equals("fail")) {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "کد رهگیری:" + tracking_code, "شما از خرید انصراف داده اید.");
                    SweetDialog.getSweetAlertDialog().setCancelButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
//                    error("کد رهگیری:" + tracking_code, "شما از خرید انصراف داده اید.");

                } else if (result_Purchase.equals("not-confirmed")) {
                    String order_id = result.getString("order-id");
                    String token = result.getString("order-token");
                    error("کد رهگیری:" + tracking_code, "متاسفانه خرید شما با مشکل روبرو شد.\n در صورت کسر مبلغ تا ۷۲ ساعت بعد مبلغ به حساب شما باز خواهد گشت.");
                } else if (result_Purchase.equals("done")) {
                    String order_id = result.getString("order-id");
                    String token = result.getString("order-token");
                    upgradeInit(result_Purchase, package_code, token, tracking_code);
                }


            }
        });
    }

    private void getPrice() {
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
                    SweetDialog.getSweetAlertDialog().setConfirmButton("تلاش مجدد", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            SweetDialog.changeSweet(SweetAlertDialog.PROGRESS_TYPE, "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
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
                        SweetDialog.changeSweet(SweetAlertDialog.PROGRESS_TYPE, "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
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

        setAccountLevel();

        parseDate();
        root.setVisibility(View.VISIBLE);
        SweetDialog.stopProgress();
    }

    private void setAccountLevel() {
        if (user.getAccountLevel() == StaticFun.account.Bronze) {
            accountLevel.setText("برنز");
        } else if (user.getAccountLevel() == StaticFun.account.Steel) {
            accountLevel.setText("نقره ای");
        } else if (user.getAccountLevel() == StaticFun.account.Diamond) {
            accountLevel.setText("الماسی");
        } else if (user.getAccountLevel() == StaticFun.account.Gold) {
            accountLevel.setText("طلایی");
        }
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

    private void updateUserData() {
        //i will update the user data here

        ((MainPageActivity) getActivity()).updateUser(user);

    }

    // Id pay

    private void generatePayUrl(String price, String accountName) {
        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
        SweetDialog.startProgress();

        Call<UrlResponse> get = request.getUrl(price + "0", user.getName(), user.getPn(), user.getID(), accountName);

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
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void WebViewStart(UrlResponse urlResponse) {
        if (urlResponse.getLink() != null && urlResponse != null && urlResponse.getLink() != null) {

            webViewOpen = true;
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("url", urlResponse.getLink());
            SweetDialog.stopProgress();
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
        if (webViewOpen) {
            SweetDialog.setSweetDialog(new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
            SweetDialog.startProgress();
            updateUser();
            SweetDialog.stopProgress();
        }
        super.onResume();
    }

    // get data from the server to update the user info
    private void updateUser() {

        String pwd = "";
        SharedPreferences sh = getContext().getSharedPreferences("userLogin_info", MODE_PRIVATE);
        String a = sh.getString("userLogin_info", "");
        if (!a.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(a);
                pwd = jsonObject.getString("pwd");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if (pwd.equals("")) {
            Call<LoginResponse> get = request.loginResponse(user.getPn(), pwd);

            get.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.body().getStatus_code().equals("200")) {

                        if (response.body().getAccountLevel().equals("bronze")) {
                            user.setAccountLevel(StaticFun.account.Bronze);
                        } else if (response.body().getAccountLevel().equals("steel")) {
                            user.setAccountLevel(StaticFun.account.Steel);
                        } else if (response.body().getAccountLevel().equals("gold")) {
                            user.setAccountLevel(StaticFun.account.Gold);
                        } else if (response.body().getAccountLevel().equals("diamond")) {
                            user.setAccountLevel(StaticFun.account.Diamond);
                        }
                        user.setEndTime(response.body().getEnd());

                        updateUserData();
                        parseDate();
                        setAccountLevel();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            t.getMessage().toString()
                            , "re buy fragment - updateUser / failure");

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
    }

    //     IAB

    private void error(String title, String text) {

        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE), title, text);
        SweetDialog.getSweetAlertDialog().setCancelButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });

    }

    private void upgradeInit(@Nullable String result, String package_code, @Nullable String token, @Nullable String tracking_code) {
        switch (package_code) {
            case "1001":
                upgradeAccount("bronze", result, token, tracking_code);
                break;
            case "1002":
                upgradeAccount("steel", result, token, tracking_code);
                break;
            case "1003":
                upgradeAccount("gold", result, token, tracking_code);
                break;
            case "1004":
                upgradeAccount("diamond", result, token, tracking_code);
                break;

        }
    }

    private void upgradeAccount(@NonNull String level, @Nullable String result, @Nullable String token, @Nullable String tracking_code) {

        SweetDialog.startProgress();

        Call<UpgradeResponse> get = request.upgradeAccount(level, user.getID());

        get.enqueue(new Callback<UpgradeResponse>() {
            @Override
            public void onResponse(Call<UpgradeResponse> call, Response<UpgradeResponse> response) {
                // should check the response
                if (response.body().getStatus_code().equals("200")) {
                    setOrder(level, result, token, tracking_code, response.body().getEnd());
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            response.body().getMessage() != null ? response.body().getMessage() : "-"
                            , "re buy fragment - upgrade account / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                    SweetDialog.getSweetAlertDialog().setCancelButton("تلاش مجدد", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            SweetDialog.changeSweet(SweetAlertDialog.PROGRESS_TYPE, "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
                            upgradeAccount(level, result, token, tracking_code);
                        }
                    }).setCancelButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
                }


            }

            @Override
            public void onFailure(Call<UpgradeResponse> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "re buy fragment - upgrade account / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setCancelButton("تلاش مجدد", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SweetDialog.changeSweet(SweetAlertDialog.PROGRESS_TYPE, "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
                        upgradeAccount(level, result, token, tracking_code);
                    }
                }).setCancelButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        });
    }

    // just use when user choose to pay with Bazar IAB
    private void setOrder(@NonNull String level, @Nullable String result, @Nullable String token, @Nullable String tracking_code, String end) {

        Call<NormalResponse> get = request.newOrder(tracking_code, user.getID(), "2000", user.getName(), user.getPn(), level, result, token);

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    refresh(end, level);
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            response.body().getMessage() != null ? response.body().getMessage() : "-"
                            , "re buy fragment - set order / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در ارسال اطلاعات", "کاربر گرامی ارتباط با سرور برای ثبت درخواست شما برقرار نشد.");
                    SweetDialog.getSweetAlertDialog().setConfirmButton("تلاش مجدد", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            SweetDialog.changeSweet(SweetAlertDialog.PROGRESS_TYPE, "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
                            setOrder(level, result, token, tracking_code, end);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "re buy fragment - set order / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در دریافت اطلاعات", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmButton("تلاش مجدد", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SweetDialog.changeSweet(SweetAlertDialog.PROGRESS_TYPE, "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
                        setOrder(level, result, token, tracking_code, end);
                    }
                }).setCancelButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        });
    }

    public void refresh(String end, String level) {

        user.setEndTime(end);

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

        SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "ارتقا یافت", "حساب شما ارتقا یافت.");
        SweetDialog.getSweetAlertDialog().setConfirmButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });


        updateUserData();
    }

}
