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
    SweetAlertDialog sweetAlertDialog;

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

            sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("???? ?????? ???????????? ??????????????").setContentText("???????? ?????????? ??????????...").setCancelable(false);

            initViews();
            controllerViews();
            getPrice();

            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "???????????????? ???? ???????????? ?????????????? ???? ???????? ?????????? ????????", Toasty.LENGTH_LONG, true).show();
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
                if (false) {
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

                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                sweetAlertDialog.show();

                String result_Purchase = result.getString("result-Purchase");
                String package_code = result.getString("package-code");
                String tracking_code = result.getString("tracking-code");
                String level = upgradeInit(package_code);

                if (result_Purchase.equals("fail")) {

                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("???? ????????????:" + tracking_code);
                    sweetAlertDialog.setContentText("?????? ???? ???????? ???????????? ???????? ??????.");

                    setOrder(level, result_Purchase, "-", tracking_code);

                    sweetAlertDialog.setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
//                    error("???? ????????????:" + tracking_code, "?????? ???? ???????? ???????????? ???????? ??????.");

                } else if (result_Purchase.equals("not-confirmed")) {
                    String order_id = result.getString("order-id");
                    String token = result.getString("order-token");

                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("???? ????????????:" + tracking_code);
                    sweetAlertDialog.setContentText("???????????????? ???????? ?????? ???? ???????? ?????????? ????.\n ???? ???????? ?????? ???????? ???? ???? ???????? ?????? ???????? ???? ???????? ?????? ?????? ?????????? ??????.");

                    setOrder(level, result_Purchase, token, tracking_code);

                    sweetAlertDialog.setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });

                } else if (result_Purchase.equals("done")) {
                    String order_id = result.getString("order-id");
                    String token = result.getString("order-token");
                    upgradeAccount(level, result_Purchase, token, tracking_code);

                }


            }
        });
    }

    private void getPrice() {
        sweetAlertDialog.show();

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
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                    sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                    sweetAlertDialog.setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                            sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                            sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
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
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                        sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                        getPrice();
                    }
                });
            }
        });
    }

    private void setValue(String bronze, String steel, String gold, String diamond) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        bronzePrice_txt.setText(decimalFormat.format(Integer.parseInt(bronze)) + " ??????????");
        goldPrice_txt.setText(decimalFormat.format(Integer.parseInt(gold)) + " ??????????");
        steelPrice_txt.setText(decimalFormat.format(Integer.parseInt(steel)) + " ??????????");
        diamondPrice_txt.setText(decimalFormat.format(Integer.parseInt(diamond)) + " ??????????");

        setAccountLevel();

        parseDate();
        root.setVisibility(View.VISIBLE);
        sweetAlertDialog.dismiss();
    }

    private void setAccountLevel() {
        if (user.getAccountLevel() == StaticFun.account.Bronze) {
            accountLevel.setText("????????");
        } else if (user.getAccountLevel() == StaticFun.account.Steel) {
            accountLevel.setText("???????? ????");
        } else if (user.getAccountLevel() == StaticFun.account.Diamond) {
            accountLevel.setText("????????????");
        } else if (user.getAccountLevel() == StaticFun.account.Gold) {
            accountLevel.setText("??????????");
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
            dayLeft.setText("???? ???????? ???????????????? ???????? ???????? ??????????????");
        }
    }

    private void updateUserData() {
        //i will update the user data here

        ((MainPageActivity) getActivity()).updateUser(user);

    }

    // Id pay

    private void generatePayUrl(String price, String accountName) {
        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("???? ?????? ???????????? ??????????????").setContentText("???????? ?????????? ??????????...").setCancelable(false);
        sweetAlertDialog.show();


        Call<UrlResponse> get = request.getUrl("100" + "0", user.getName(), user.getPn(), user.getID(), accountName);

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
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void WebViewStart(UrlResponse urlResponse) {

        Log.d("webview", urlResponse.toString());

        if (urlResponse.getLink() != null && urlResponse != null && urlResponse.getLink() != null) {
            Log.d("webview", urlResponse.getLink().toString());
            webViewOpen = true;
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("url", urlResponse.getLink());
            sweetAlertDialog.dismiss();
            startActivity(intent);
        } else {
            Log.d("webview", "no url");
            StaticFun.setLog((user == null) ? "-"
                            : (user.getPn().length() > 0 ? user.getPn() : "-"),
                    "there is problem in get or handle link"
                    , "re buy fragment - WebViewStart");
            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
            sweetAlertDialog.setContentText("???????????????? ?????????? ?????????? ???????? ???????????? ???? ?????? ???????? ?????????? ???????? ????????.");
        }

    }

    @Override
    public void onPause() {
        Log.d("activity", "pause");
        super.onPause();
        sweetAlertDialog.dismiss();
    }

    @Override
    public void onResume() {
        if (webViewOpen) {
            updateUser();
        }
        super.onResume();
    }

    // get data from the server to update the user info
    private void updateUser() {
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("???? ?????? ???????????? ??????????????").setContentText("???????? ?????????? ??????????...").setCancelable(false);
        sweetAlertDialog.show();

        Log.d("update", "update started");

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


        if (!pwd.equals("")) {
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
                        sweetAlertDialog.dismiss();
                    } else {
                        StaticFun.setLog((user == null) ? "-"
                                        : (user.getPn().length() > 0 ? user.getPn() : "-"),
                                response.body().getMessage() != null ? response.body().getMessage() : "-"
                                , "re buy fragment - upgrade account / response");

                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                        sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                        sweetAlertDialog.setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                                sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                                getPrice();
                            }
                        });

                        sweetAlertDialog.setCancelButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                                sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                                updateUser();
                            }
                        }).setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            t.getMessage().toString()
                            , "re buy fragment - updateUser / failure");

                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                    sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    //     IAB

    private String upgradeInit(String package_code) {
        switch (package_code) {
            case "1001":
                return "bronze";
            case "1002":
                return "steel";
            case "1003":
                return "gold";
            case "1004":
                return "diamond";
            default:
                return "-";
        }
    }

    private void upgradeAccount(@NonNull String level, @Nullable String result, @Nullable String token, @Nullable String tracking_code) {
        Log.d("upgrade", "upgrade started");
        sweetAlertDialog.setTitleText("???? ?????? ???????????? ??????????????").setContentText("???????? ?????????? ??????????...").setCancelable(false);
        sweetAlertDialog.show();

        Call<UpgradeResponse> get = request.upgradeAccount(level, user.getID());

        get.enqueue(new Callback<UpgradeResponse>() {
            @Override
            public void onResponse(Call<UpgradeResponse> call, Response<UpgradeResponse> response) {
                // should check the response
                if (response.body().getStatus_code().equals("200")) {

                    user.setEndTime(response.body().getEnd());

                    setOrder(level, result, token, tracking_code);
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            response.body().getMessage() != null ? response.body().getMessage() : "-"
                            , "re buy fragment - upgrade account / response");

                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                    sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                    sweetAlertDialog.setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                            sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                            sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                            getPrice();
                        }
                    });
                    sweetAlertDialog.setCancelButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                            sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                            sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                            upgradeAccount(level, result, token, tracking_code);
                        }
                    }).setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
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
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                sweetAlertDialog.setCancelButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                        sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                        upgradeAccount(level, result, token, tracking_code);
                    }
                }).setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        });
    }

    // just use when user choose to pay with Bazar IAB
    private void setOrder(@NonNull String level, @Nullable String result, @Nullable String token, @Nullable String tracking_code) {

        Log.d("set order", "order started");

        Call<NormalResponse> get = request.newOrder(tracking_code, user.getID(), "2000", user.getName(), user.getPn(), level, result, token);

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    refresh(level);
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            response.body().getMessage() != null ? response.body().getMessage() : "-"
                            , "re buy fragment - set order / response");
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                    sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                    sweetAlertDialog.setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                            sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                            sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                            setOrder(level, result, token, tracking_code);
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
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitle("???????? ???? ???????????? ??????????????");
                sweetAlertDialog.setContentText("?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                sweetAlertDialog.setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialog.setTitle("???? ?????? ???????????? ??????????????");
                        sweetAlertDialog.setContentText("???????? ?????????? ??????????...");
                        setOrder(level, result, token, tracking_code);
                    }
                }).setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        });
    }

    public void refresh(String level) {

        if (level.equals("bronze")) {
            user.setAccountLevel(StaticFun.account.Bronze);
            accountLevel.setText("????????");
        } else if (level.equals("steel")) {
            user.setAccountLevel(StaticFun.account.Steel);
            accountLevel.setText("???????? ????");
        } else if (level.equals("gold")) {
            user.setAccountLevel(StaticFun.account.Gold);
            accountLevel.setText("??????????");
        } else if (level.equals("diamond")) {
            accountLevel.setText("????????????");
            user.setAccountLevel(StaticFun.account.Diamond);
        }

        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitle("?????????? ????????");
        sweetAlertDialog.setContentText("???????? ?????? ?????????? ????????.");
        sweetAlertDialog.setConfirmButton("????????", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });

        parseDate();
        updateUserData();
    }

}
