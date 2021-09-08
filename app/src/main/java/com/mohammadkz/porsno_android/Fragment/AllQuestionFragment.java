package com.mohammadkz.porsno_android.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
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
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllQuestionFragment extends Fragment {

    View view;
    RecyclerView list;
    ApiConfig request;
    TextView emptyList;
    User user;
    SwipeRefreshLayout swipeRefresh;
    List<GetQuestionResponse> allList;
    List<GetQuestionResponse> toDisplay = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;
    String startStamp, endStamp;
    String date = "";

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
            filterBottomSheet();

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
                    bottomSheetDialog.show();
                } else {
                    bottomSheetDialog.dismiss();
                }
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

    private void filterBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_sheet_filter, (ConstraintLayout) view.findViewById(R.id.layout));

        AnimatedCheckBox search, date;
        search = bottomSheetView.findViewById(R.id.checkbox_search);
        date = bottomSheetView.findViewById(R.id.checkbox_date);


        CardView search_card, date_card;
        search_card = bottomSheetView.findViewById(R.id.search_card);
        date_card = bottomSheetView.findViewById(R.id.date_card);

        TextInputEditText date_start, date_end;
        date_start = bottomSheetView.findViewById(R.id.date_start);
        date_end = bottomSheetView.findViewById(R.id.date_end);


        date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = datePicker(true, date_start);
            }
        });

        date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = datePicker(false, date_end);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search.isChecked()) {
                    search.setChecked(false);
                    search_card.setVisibility(View.GONE);
                    YoYo.with(Techniques.SlideInDown)
                            .duration(600)
                            .repeat(0)
                            .playOn(bottomSheetView.findViewById(R.id.search_card));
                } else {
                    search.setChecked(true);
                    date.setChecked(false);
                    YoYo.with(Techniques.SlideInDown)
                            .duration(600)
                            .repeat(0)
                            .playOn(bottomSheetView.findViewById(R.id.search_card));
                    search_card.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInDown)
                            .duration(600)
                            .repeat(0)
                            .playOn(bottomSheetView.findViewById(R.id.date_card));
                    date_card.setVisibility(View.GONE);
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (date.isChecked()) {
                    date.setChecked(false);
                    date_card.setVisibility(View.GONE);
                    YoYo.with(Techniques.SlideInUp)
                            .duration(600)
                            .repeat(0)
                            .playOn(bottomSheetView.findViewById(R.id.search_card));
                } else {
                    date.setChecked(true);
                    date_card.setVisibility(View.VISIBLE);
                    search_card.setVisibility(View.GONE);
                    search.setChecked(false);
                    YoYo.with(Techniques.SlideInUp)
                            .duration(600)
                            .repeat(0)
                            .playOn(bottomSheetView.findViewById(R.id.search_card));
                    YoYo.with(Techniques.SlideInUp)
                            .duration(600)
                            .repeat(0)
                            .playOn(bottomSheetView.findViewById(R.id.date_card));
                }
            }
        });

        bottomSheetView.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.isChecked()) {
                    EditText searchEdt = bottomSheetView.findViewById(R.id.searchEdt);
                    searchName(searchEdt.getText().toString());
                    bottomSheetDialog.dismiss();
                    makeFilterInvisible();
                } else if (date.isChecked()) {
                    dateFilter();
                    bottomSheetDialog.dismiss();
                    makeFilterInvisible();
                }

            }
        });

        bottomSheetView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setChecked(false);
                date.setChecked(false);
                date_start.setText("");
                date_end.setText("");
            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
                SweetDialog.stopProgress();
                makeFilterInvisible();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
    }

    private String datePicker(boolean start, TextInputEditText textInputEditText) {
        String TAG = "";

        PersianDatePickerDialog datePickerDialog = new PersianDatePickerDialog(getContext())
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1400)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(Color.GREEN)
                .setActionTextSize(20)
                .setTodayTextSize(20)
                .setNegativeTextSize(20)
                .setPickerBackgroundDrawable(R.drawable.bg_bottom_sheet)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(false)
                .setListener(new PersianPickerListener() {
                    @Override
                    public void onDateSelected(PersianPickerDate persianPickerDate) {
                        date = persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay();
                        if (start) {
                            startStamp = String.valueOf(persianPickerDate.getTimestamp());
                        } else {
                            endStamp = String.valueOf(persianPickerDate.getTimestamp());
                        }

                        textInputEditText.setText(persianPickerDate.getPersianLongDate());
//                        textInputEditText.setText(String.valueOf(persianPickerDate.getTimestamp()));
                        Toast.makeText(getContext(), persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismissed() {

                    }
                });

        datePickerDialog.show();

        return date;
    }

    private void dateFilter() {
        toDisplay = new ArrayList<>();

        if (!startStamp.isEmpty() && !endStamp.isEmpty()) {
            for (int i = 0; i < allList.size(); i++) {
                if (Long.parseLong(allList.get(i).getStart()) >= Long.parseLong(startStamp) && Long.parseLong(allList.get(i).getEnd()) <= Long.parseLong(endStamp)) {
                    toDisplay.add(allList.get(i));
                }
            }
        } else if (!startStamp.isEmpty() && endStamp.isEmpty()) {
            for (int i = 0; i < allList.size(); i++) {
                if (Long.parseLong(allList.get(i).getStart()) >= Long.parseLong(startStamp)) {
                    toDisplay.add(allList.get(i));
                }
            }
        } else if (startStamp.isEmpty() && !endStamp.isEmpty()) {
            for (int i = 0; i < allList.size(); i++) {
                if (Long.parseLong(allList.get(i).getEnd()) <= Long.parseLong(endStamp)) {
                    toDisplay.add(allList.get(i));
                }
            }
        }

        setAdapter();
    }

    private void makeFilterInvisible() {
        // fragment communication
        Bundle bundle = new Bundle();
        bundle.putString("visibility", "false");
        getParentFragmentManager().setFragmentResult("visibility_t", bundle);
    }
}