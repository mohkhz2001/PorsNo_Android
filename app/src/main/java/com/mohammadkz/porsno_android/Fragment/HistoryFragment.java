package com.mohammadkz.porsno_android.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Model.Response.AnswerHistoryResponse;
import com.mohammadkz.porsno_android.Model.Response.HistoryBuyResponse;
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

public class HistoryFragment extends Fragment {

    View view;
    ViewPager viewPager;
    TabLayout tabLayout;
    MainAdapter adapter;
    User user;
    ApiConfig request;
    List<HistoryBuyResponse> buyList;
    List<AnswerHistoryResponse> doneList;

    public HistoryFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_history, container, false);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "در حال دریافت اطلاعات", "لطفا منتظر باشید...");
            SweetDialog.startProgress();

            initViews();
            controllerViews();
            getHistoryBuy();

            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "history fragment - create");
            return view;
        }

    }

    private void initViews() {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.viewPager);
    }

    private void controllerViews() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    tabLayout.setSelectedTabIndicator(R.drawable.bg_tab_select_l);
                } else {
                    tabLayout.setSelectedTabIndicator(R.drawable.bg_tab_select_r);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setAdapter() {
        adapter = new MainAdapter(getChildFragmentManager());
        SweetDialog.stopProgress();

        if (buyList != null)
            adapter.addFragment(new History_BuyFragment(user, buyList), "خرید ها");
        if (doneList != null)
            adapter.addFragment(new History_DoneFragment(user, doneList), "انجام شده");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void getHistoryBuy() {

        Call<List<HistoryBuyResponse>> get = request.getBuyHistory(user.getID());

        get.enqueue(new Callback<List<HistoryBuyResponse>>() {
            @Override
            public void onResponse(Call<List<HistoryBuyResponse>> call, Response<List<HistoryBuyResponse>> response) {
                if (response.body().size() >= 0) {
                    buyList = response.body();
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"), "-"
                            , "history fragment - History buy / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                }
                getHistoryDone();

            }

            @Override
            public void onFailure(Call<List<HistoryBuyResponse>> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"), t.getMessage().toString()
                        , "history fragment - History buy / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                getHistoryDone();
            }
        });

    }

    private void getHistoryDone() {
        Call<List<AnswerHistoryResponse>> get = request.getAnswerHistory(user.getID());

        get.enqueue(new Callback<List<AnswerHistoryResponse>>() {
            @Override
            public void onResponse(Call<List<AnswerHistoryResponse>> call, Response<List<AnswerHistoryResponse>> response) {
                if (response.body().size() >= 0) {
                    doneList = response.body();
                } else {
                    StaticFun.setLog((user == null) ? "-"
                                    : (user.getPn().length() > 0 ? user.getPn() : "-"),
                            "-"
                            , "history fragment - History Done / response");
                }
                setAdapter();

            }

            @Override
            public void onFailure(Call<List<AnswerHistoryResponse>> call, Throwable t) {
                StaticFun.setLog((user == null) ? "-"
                                : (user.getPn().length() > 0 ? user.getPn() : "-"),
                        t.getMessage().toString()
                        , "history fragment - History Done / failure");
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                setAdapter();
            }

        });
    }

    private class MainAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        ArrayList<String> stringArrayList = new ArrayList<>();

        public void addFragment(Fragment fragment, String string) {
            fragmentArrayList.add(fragment);
            stringArrayList.add(string);
        }


        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            return fragmentArrayList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return stringArrayList.get(position);
        }
    }
}