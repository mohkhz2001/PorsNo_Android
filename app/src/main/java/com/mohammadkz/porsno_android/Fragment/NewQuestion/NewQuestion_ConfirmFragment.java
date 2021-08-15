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
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Adapter.ConfirmNewQuestionaireAdapter;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.NewQuestionaire;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.ArrayList;
import java.util.List;

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
    ProgressDialog progressDialog;
    BottomSheetDialog bottomSheetDialog;

    public NewQuestion_ConfirmFragment(Questionnaire questionnaire) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_question_confirm, container, false);

        request = AppConfig.getRetrofit().create(ApiConfig.class);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("منتظر باشید...");

        ((NewQuestionActivity) getActivity()).setSeekBar(2);

        initViews();
        controllerViews();
        setAdapter();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        confirm = view.findViewById(R.id.confirm);
    }

    private void controllerViews() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
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
        String json = convertToJson();

        Call<NewQuestionaire> get = request.newQuestnaire(" - ", questionnaire.getName(), questionnaire.getStartDate_stamp(), questionnaire.getEndDate_stamp(), " - ",
                questionnaire.getDescription(), json, questionnaire.getUserId());

        get.enqueue(new Callback<NewQuestionaire>() {
            @Override
            public void onResponse(Call<NewQuestionaire> call, Response<NewQuestionaire> response) {
                if (response.body().getStatus_code().equals("200")) {
                    progressDialog.dismiss();
                    getActivity().finish();
                } else {
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<NewQuestionaire> call, Throwable t) {
                t.getMessage();
                StaticFun.alertDialog_connectionFail(getContext());
                progressDialog.dismiss();
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
        builder.setTitle("حذف سوال");
        builder.setMessage("آیا از پاک کردن این سوال اطمینان دارید ؟");

        String yes = "بله";
        String no = "خیر";

        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (questionnaire.getQuestions().size() > 1) {
                    removeQuestion(pos);
                } else {
                    Toasty.error(getContext(), "تنها یک سوال موجود است و امکان حذف آن وجود ندارد.", Toasty.LENGTH_SHORT, true).show();
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

        Toasty.success(getContext(), "سوال مورد نظر با موفقیت جذف شد", Toasty.LENGTH_SHORT, true).show();

        setAdapter();
    }

    private void editQuestion(int pos) {
        bottomSheet(questionnaire.getQuestions().get(pos), pos);
    }

    private void bottomSheet(Question question, int pos) {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_edit_question_bottom_sheet, (RelativeLayout) view.findViewById(R.id.root));

        TextInputEditText question_, answer, answer1_field, answer2_field, answer3_field, answer4_field, answer5_field, answer6_field;
        RelativeLayout relative1, relative2, relative3, relative4, relative5, relative6;
        AnimatedCheckBox testQuestion, new_question;
        Button confirm;
        LinearLayout layout;


        answer = bottomSheetView.findViewById(R.id.answer);
        new_question = bottomSheetView.findViewById(R.id.new_question);

        new_question.setChecked(true);
        new_question.setClickable(false);

        testQuestion = bottomSheetView.findViewById(R.id.testQuestion);
        question_ = bottomSheetView.findViewById(R.id.question);

        question_.setText(question.getQuestion().toString());

        answer1_field = bottomSheetView.findViewById(R.id.answer1_field);
        answer2_field = bottomSheetView.findViewById(R.id.answer2_field);
        answer3_field = bottomSheetView.findViewById(R.id.answer3_field);
        answer4_field = bottomSheetView.findViewById(R.id.answer4_field);
        answer5_field = bottomSheetView.findViewById(R.id.answer5_field);
        answer6_field = bottomSheetView.findViewById(R.id.answer6_field);
        relative1 = bottomSheetView.findViewById(R.id.relative1);
        relative2 = bottomSheetView.findViewById(R.id.relative2);
        relative3 = bottomSheetView.findViewById(R.id.relative3);
        relative4 = bottomSheetView.findViewById(R.id.relative4);
        relative5 = bottomSheetView.findViewById(R.id.relative5);
        relative6 = bottomSheetView.findViewById(R.id.relative6);
        confirm = bottomSheetView.findViewById(R.id.confirm);
        layout = bottomSheetView.findViewById(R.id.layout);

        // set value
        if (!question.isTest()) {
            layout.setVisibility(View.GONE);
            answer.setVisibility(View.VISIBLE);
            testQuestion.setChecked(true);
        } else {

            // set value in question filed
            if (question.getAnswers().size() >= 2) {
                answer1_field.setText(question.getAnswers().get(0).getAnswer().toString());
                answer2_field.setText(question.getAnswers().get(1).getAnswer().toString());
                if (question.getAnswers().size() >= 3) {
                    relative3.setVisibility(View.VISIBLE);
                    answer3_field.setText(question.getAnswers().get(2).getAnswer().toString());
                    if (question.getAnswers().size() >= 4) {
                        relative4.setVisibility(View.VISIBLE);
                        answer4_field.setText(question.getAnswers().get(3).getAnswer().toString());
                        if (question.getAnswers().size() >= 5) {
                            relative5.setVisibility(View.VISIBLE);
                            answer5_field.setText(question.getAnswers().get(4).getAnswer().toString());
                            if (question.getAnswers().size() >= 6) {
                                relative6.setVisibility(View.VISIBLE);
                                answer6_field.setText(question.getAnswers().get(5).getAnswer().toString());
                            }
                        }
                    }
                }
            }


        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setQuestion(question_.getText().toString());

                if (testQuestion.isChecked()) {
                    question.setTest(false);
                    question.setAnswers(new ArrayList<>());
                } else {
                    List<Answer> answers = new ArrayList<>();
                    if (relative1.getVisibility() == View.VISIBLE) {
                        answers.add(new Answer(answer1_field.getText().toString()));
                    }
                    if (relative2.getVisibility() == View.VISIBLE) {
                        answers.add(new Answer(answer2_field.getText().toString()));
                    }
                    if (relative3.getVisibility() == View.VISIBLE) {
                        answers.add(new Answer(answer3_field.getText().toString()));
                    }
                    if (relative4.getVisibility() == View.VISIBLE) {
                        answers.add(new Answer(answer4_field.getText().toString()));
                    }
                    if (relative5.getVisibility() == View.VISIBLE) {
                        answers.add(new Answer(answer5_field.getText().toString()));
                    }
                    if (relative6.getVisibility() == View.VISIBLE) {
                        answers.add(new Answer(answer6_field.getText().toString()));
                    }
                    question.setAnswers(answers);
                }

                if (question.getAnswers().size() >= 2) {
                    answer1_field.setText(question.getAnswers().get(0).getAnswer().toString());
                    answer2_field.setText(question.getAnswers().get(1).getAnswer().toString());
                    if (question.getAnswers().size() >= 3) {
                        answer3_field.setVisibility(View.VISIBLE);
                        answer3_field.setText(question.getAnswers().get(2).getAnswer().toString());
                        if (question.getAnswers().size() >= 4) {
                            answer4_field.setVisibility(View.VISIBLE);
                            answer4_field.setText(question.getAnswers().get(3).getAnswer().toString());
                            if (question.getAnswers().size() >= 5) {
                                answer5_field.setVisibility(View.VISIBLE);
                                answer5_field.setText(question.getAnswers().get(4).getAnswer().toString());
                                if (question.getAnswers().size() >= 6) {
                                    answer6_field.setVisibility(View.VISIBLE);
                                    answer6_field.setText(question.getAnswers().get(5).getAnswer().toString());
                                }
                            }
                        }
                    }
                }

                // update the list
                questionnaire.getQuestions().remove(pos);
                questionnaire.getQuestions().add(pos, question);
                setAdapter();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}