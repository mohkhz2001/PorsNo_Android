package com.mohammadkz.porsno_android.Fragment.ManageQuestionnaire;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Activity.QuestionnaireManagerActivity;
import com.mohammadkz.porsno_android.Adapter.AdviceAdapter;
import com.mohammadkz.porsno_android.Model.Advice;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class Manager_infoFragment extends Fragment {

    View view;
    Questionnaire questionnaire;
    int done24;
    TextView name, dec, startDateTxt, endDateTxt, numberSeen, numberDone, numberDone_24, numberLiked, numberDisliked, idea_txt;
    ImageView edit_name, edit_Dec, editStart, editEnd;
    Button confirm;
    PersianDatePickerDialog datePickerDialog;
    BottomSheetDialog bottomSheetDialog;
    List<Advice> adviceList;
    RecyclerView list;
    ApiConfig request;


    public Manager_infoFragment(Questionnaire questionnaire, int done24, List<Advice> adviceList) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
        this.done24 = done24;
        this.adviceList = adviceList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_manager_info, container, false);

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            initViews();
            controllerViews();
            setValue();

            return view;
        } catch (Exception e) {
            Toasty.error(getContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog("-", e.getMessage().toString(), "manager info fragment - create");
            return view;
        }

    }

    private void initViews() {
        name = view.findViewById(R.id.name);
        dec = view.findViewById(R.id.dec);
        startDateTxt = view.findViewById(R.id.startDateTxt);
        endDateTxt = view.findViewById(R.id.endDateTxt);
        numberSeen = view.findViewById(R.id.numberSeen);
        numberDone = view.findViewById(R.id.numberDone);
        numberDone_24 = view.findViewById(R.id.numberDone_24);
        numberLiked = view.findViewById(R.id.numberLiked);
        numberDisliked = view.findViewById(R.id.numberDisliked);
        edit_name = view.findViewById(R.id.edit_name);
        edit_Dec = view.findViewById(R.id.edit_Dec);
        editStart = view.findViewById(R.id.editStart);
        editEnd = view.findViewById(R.id.editEnd);
        confirm = view.findViewById(R.id.confirm);
        list = view.findViewById(R.id.list);
        idea_txt = view.findViewById(R.id.idea_txt);

    }

    private void controllerViews() {

        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetChooser(questionnaire.getName(), "name");
            }
        });

        edit_Dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetChooser(questionnaire.getDescription(), "dec");
            }
        });

        editStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(true, questionnaire.getStartDate_stamp());
            }
        });

        editEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(false, questionnaire.getEndDate_stamp());
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE));
                SweetDialog.startProgress();
                updateQuestion();
            }
        });

    }

    // set value to the field
    private void setValue() {
        DecimalFormat decimalFormat = new DecimalFormat("###,###"); // format

        name.setText(questionnaire.getName());
        dec.setText(questionnaire.getDescription());
        startDateTxt.setText(parseDate(questionnaire.getStartDate_stamp()));
        endDateTxt.setText(parseDate(questionnaire.getEndDate_stamp()));
        numberDone.setText(decimalFormat.format(Integer.valueOf(questionnaire.getDone())));
        numberSeen.setText(decimalFormat.format(Integer.valueOf(questionnaire.getViews())));
        numberDone_24.setText(String.valueOf(done24));

        if (Long.parseLong(questionnaire.getStartDate_stamp()) > new Timestamp(System.currentTimeMillis()).getTime()) {
            editStart.setVisibility(View.VISIBLE);
        } else {
            editStart.setVisibility(View.GONE);
        }

        if (adviceList != null) {
            setAdapter();
        } else {
            list.setVisibility(View.GONE);
            idea_txt.setVisibility(View.VISIBLE);
        }
    }

    // parse the date
    private String parseDate(String timeStamp) {
        PersianDate pdate = new PersianDate(Long.valueOf(timeStamp) * 1000);
        PersianDateFormat pdformater1 = new PersianDateFormat("l d F Y");//l d F y
        return pdformater1.format(pdate);//1396/05/20
    }

    private String datePicker(boolean start, String initDate) {
        String TAG = "";

        datePickerDialog = new PersianDatePickerDialog(getContext())
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1400)
                .setInitDate(Long.parseLong(initDate) * 1000)
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
                        if (start) {
                            questionnaire.setStartDate_stamp(String.valueOf(persianPickerDate.getTimestamp() / 1000));
                            questionnaire.setStartDate(persianPickerDate.getPersianLongDate());

                            // set text for btn
                            startDateTxt.setText(persianPickerDate.getPersianLongDate());
                            confirm.setVisibility(View.VISIBLE);

                        } else {
                            questionnaire.setEndDate_stamp(String.valueOf(persianPickerDate.getTimestamp() / 1000));
                            questionnaire.setEndDate(persianPickerDate.getPersianLongDate());

                            // set text for btn
                            endDateTxt.setText(persianPickerDate.getPersianLongDate());
                            confirm.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getContext(), persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismissed() {

                    }
                });

        datePickerDialog.show();

        if (start) {
            return questionnaire.getStartDate();
        } else {
            return questionnaire.getEndDate();
        }
    }

    // update the info
    public void bottomSheetChooser(String string, String title) {

        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_sheet_update_txt, (LinearLayout) view.findViewById(R.id.layout));

        TextInputEditText text = bottomSheetView.findViewById(R.id.text);
        TextInputLayout textLayout = bottomSheetView.findViewById(R.id.text_layout);
        text.setText(string.toString());

        textLayout.setCounterEnabled(true);
        textLayout.setCounterMaxLength(title.equals("name") ? 20 : 200);


        bottomSheetView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.equals("name") && !text.equals(text.getText().toString())) {
                    questionnaire.setName(text.getText().toString());
                    name.setText(text.getText().toString());
                    confirm.setVisibility(View.VISIBLE);
                } else if (title.equals("dec") && !text.equals(text.getText().toString())) {
                    questionnaire.setDescription(text.getText().toString());
                    dec.setText(text.getText().toString());
                    confirm.setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void setAdapter() {
        AdviceAdapter adapter = new AdviceAdapter(getContext(), adviceList);
        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        list.setAdapter(adapter);
    }

    private void updateQuestion() {

        Call<NormalResponse> get = request.editQuestion(questionnaire.getUserId(), questionnaire.getId(),
                questionnaire.getName(), questionnaire.getDescription(), questionnaire.getStartDate_stamp(), questionnaire.getEndDate_stamp());

        get.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (response.body().getStatus_code().equals("200")) {
                    System.out.println();
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "اطلاع پرسشنامه با موفقیت تغییر کرد.", "");
                } else {
                    StaticFun.setLog("-", response.body() != null ? response.body().getMessage().toString() : "-"
                            , "manager info fragment - update question / response");
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "خطایی در ویراش بوجود آمده است.", "");
                    SweetDialog.getSweetAlertDialog().setConfirmButton("تلاش مجدد", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            updateQuestion();
                        }
                    });

                    SweetDialog.getSweetAlertDialog().setConfirmButton("بستن", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            SweetDialog.stopProgress();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {

                StaticFun.setLog("-",
                        t.getMessage().toString()
                        , "manager info fragment - update question / failure");

                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "مشکل در برقراری ارتباط", "کاربر گرامی ارتباط با سرور برای دریافت اطلاعات برقرار نشد.\nلطفا دقایقی دیگر تلاش نمایید.");
                SweetDialog.getSweetAlertDialog().setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SweetDialog.stopProgress();
                    }
                });
            }
        });
    }
}