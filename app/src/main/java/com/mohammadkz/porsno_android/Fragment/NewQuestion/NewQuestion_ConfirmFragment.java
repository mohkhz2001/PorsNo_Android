package com.mohammadkz.porsno_android.Fragment.NewQuestion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.API.ApiConfig;
import com.mohammadkz.porsno_android.API.AppConfig;
import com.mohammadkz.porsno_android.Activity.AnswerActivity;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Adapter.AnswerAdapter;
import com.mohammadkz.porsno_android.Adapter.ConfirmNewQuestionaireAdapter;
import com.mohammadkz.porsno_android.Adapter.NewAnswerAdapter;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.NewQuestionaire;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewQuestion_ConfirmFragment extends Fragment {

    View view;
    RecyclerView list;
    Questionnaire questionnaire;
    Button confirm;
    ConfirmNewQuestionaireAdapter confirmNewQuestionaireAdapter;
    ApiConfig request;
    BottomSheetDialog bottomSheetDialog;
    NewAnswerAdapter newAnswerAdapter;

    public NewQuestion_ConfirmFragment(Questionnaire questionnaire) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_new_question_confirm, container, false);

            SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE));

            request = AppConfig.getRetrofit().create(ApiConfig.class);

            ((NewQuestionActivity) getActivity()).setSeekBar(3);

            initViews();
            controllerViews();
            setAdapter();

            return view;
        } catch (Exception e) {
            StaticFun.setLog("id:" + questionnaire.getUserId(), e.getMessage().toString(), "new question confirm fragment - create");
            return view;
        }
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        confirm = view.findViewById(R.id.confirm);
    }

    private void controllerViews() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api();
            }
        });
    }

    private void setAdapter() {
        confirmNewQuestionaireAdapter = new ConfirmNewQuestionaireAdapter(getContext(), questionnaire.getQuestions(), true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(confirmNewQuestionaireAdapter);

        // edit and remove click listener
        confirmNewQuestionaireAdapter.setOnClickListener(new ConfirmNewQuestionaireAdapter.OnClickListener() {
            @Override
            public void onRemoveClickListener(int pos) { //  removed clicked
                alertDialog_remove(pos);
            }

            @Override
            public void onEditClickListener(int pos) { // edit clicked
                editQuestion(pos);
            }
        });

    }

    private void Api() {
        SweetDialog.setSweetDialog(new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE), "???? ?????? ?????????? ??????????????", "???????? ?????????? ??????????.");
        SweetDialog.startProgress();

        String json = convertToJson();

        Call<NewQuestionaire> get = request.newQuestnaire(" - ", questionnaire.getName(), questionnaire.getStartDate_stamp(), questionnaire.getEndDate_stamp(), " - ",
                questionnaire.getDescription(), json, questionnaire.getUserId());

        get.enqueue(new Callback<NewQuestionaire>() {
            @Override
            public void onResponse(Call<NewQuestionaire> call, Response<NewQuestionaire> response) {
                if (response.body().getStatus_code().equals("200")) {
                    SweetDialog.changeSweet(SweetAlertDialog.SUCCESS_TYPE, "???????????????? ?? ?????? ???? ?????????? ?????????? ????.", "");
                    getActivity().finish();
                } else if (response.body().getStatus_code().equals("404")) {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????????????? ?? ?????? ?????????? ??????.", "???????? ???????? ?????? ???? ?????????? ?????????? ??????.");
                } else if (response.body().getStatus_code().equals("403")) {
                    SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????????????? ?? ?????? ?????????? ??????.", "?????????? ???????????????? ?????? ???????? ?????? ???? ?????????? ?????????? ????????");
                } else {
                    StaticFun.setLog("id:" + questionnaire.getUserId(),
                            response.body() != null ? (response.body().getMessage() + " - " + response.body().getStatus_code()) : "-"
                            , "new question confirm fragment - api / response");
                }
            }

            @Override
            public void onFailure(Call<NewQuestionaire> call, Throwable t) {
                StaticFun.setLog("id:" + questionnaire.getUserId(),
                        t.getMessage().toString()
                        , "new question confirm fragment - api / failure");
                StaticFun.alertDialog_connectionFail(getContext());
                SweetDialog.changeSweet(SweetAlertDialog.ERROR_TYPE, "???????? ???? ?????????????? ????????????", "?????????? ?????????? ???????????? ???? ???????? ???????? ???????????? ?????????????? ???????????? ??????.\n???????? ???????????? ???????? ???????? ????????????.");
                SweetDialog.getSweetAlertDialog().setConfirmButton("???????? ????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Api();
                        SweetDialog.stopProgress();
                    }
                });
                SweetDialog.getSweetAlertDialog().setCancelButton("????????", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                    }
                });
            }
        });
    }

    private String convertToJson() {
        Gson gson = new Gson();
        String a = gson.toJson(questionnaire.getQuestions());
        Log.e("json", a + " ");
        return a;
    }

    //warning to edit or remove the ....
    public void alertDialog_remove(int pos) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.CustomMaterialDialog);
        builder.setTitle("?????? ????????");
        builder.setMessage("?????? ???? ?????? ???????? ?????? ???????? ?????????????? ?????????? ??");

        String yes = "??????";
        String no = "??????";

        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (questionnaire.getQuestions().size() > 1) {
                    removeQuestion(pos);
                } else {
                    Toasty.error(getContext(), "???????? ???? ???????? ?????????? ?????? ?? ?????????? ?????? ???? ???????? ??????????.", Toasty.LENGTH_SHORT, true).show();
                }
            }
        });

        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    // if user confirm to remove the question and there is more that one question => question will remove and adapter will be update
    private void removeQuestion(int pos) {

        questionnaire.getQuestions().remove(pos);

        Toasty.success(getContext(), "???????? ???????? ?????? ???? ???????????? ?????? ????", Toasty.LENGTH_SHORT, true).show();

        setAdapter();
    }

    private void editQuestion(int pos) {
        bottomSheet(questionnaire.getQuestions().get(pos), pos);
    }

    private void bottomSheet(Question question, int pos) {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_edit_question_bottom_sheet, (RelativeLayout) view.findViewById(R.id.root));

        TextInputEditText question_, answer;
        TextInputLayout answer_layout;
        SwitchButton testQuestion;
        AnimatedCheckBox new_question;
        Button confirm;
        LinearLayout layout;
        RecyclerView editList;


        answer = bottomSheetView.findViewById(R.id.answer);
        answer_layout = bottomSheetView.findViewById(R.id.answer_layout);
        new_question = bottomSheetView.findViewById(R.id.new_question);

        new_question.setChecked(true);
        new_question.setClickable(false);

        testQuestion = bottomSheetView.findViewById(R.id.testQuestion);
        question_ = bottomSheetView.findViewById(R.id.question);
        question_.setText(question.getQuestion().toString());

        editList = bottomSheetView.findViewById(R.id.editList);


        confirm = bottomSheetView.findViewById(R.id.confirm);
        layout = bottomSheetView.findViewById(R.id.layout);

        if (question.isTest()) {
            testQuestion.setChecked(false);

            setEditAdapter(editList, question);

            layout.setVisibility(View.VISIBLE);
            answer.setVisibility(View.GONE);

        } else {
            testQuestion.setChecked(true);
            layout.setVisibility(View.GONE);
            answer.setVisibility(View.VISIBLE);
        }

        testQuestion.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {

                    layout.setVisibility(View.GONE);
                    answer_layout.setVisibility(View.VISIBLE);

                } else {
                    setEditAdapter(editList, question);

                    layout.setVisibility(View.VISIBLE);
                    answer.setVisibility(View.GONE);

                }
            }
        });

        new_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Answer> answers = newAnswerAdapter.getAnswer();
                answers.add(new Answer(""));
                question.setAnswers(answers);
                setEditAdapter(editList, question);
            }
        });

        NewAnswerAdapter finalNewAnswerAdapter = newAnswerAdapter;

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setQuestion(question_.getText().toString());

                question.setTest(!testQuestion.isChecked());
                if (testQuestion.isChecked()) {
                    question.setAnswers(new ArrayList<>());
                } else {
                    question.setAnswers(finalNewAnswerAdapter.getAnswer());
                }

                // update the list
                questionnaire.getQuestions().remove(pos);
                questionnaire.getQuestions().add(pos, question);
                setAdapter();
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void setEditAdapter(RecyclerView editList, Question question) {
        newAnswerAdapter = new NewAnswerAdapter(getContext(), question.getAnswers());
        editList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        editList.setAdapter(newAnswerAdapter);

        newAnswerAdapter.setOnClickListener(new NewAnswerAdapter.OnClickListener() {
            @Override
            public void onClickListener(int pos) {

                List<Answer> answers = newAnswerAdapter.getAnswer();
                if (answers.size() > 2) {
                    answers.remove(pos);
                    question.setAnswers(answers);

                    setEditAdapter(editList, question);

                } else {
                    Toasty.error(getContext(), "?????? ???????????? ???????? ???? ???? ?????????? ?????????? ??????????", Toasty.LENGTH_LONG, true).show();
                }
            }
        });
    }

}