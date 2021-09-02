package com.mohammadkz.porsno_android.Fragment.NewQuestion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Adapter.NewAnswerAdapter;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class NewQuestion_NewFragment extends Fragment {

    View view;
    Questionnaire questionnaire;
    AnimatedCheckBox testQuestion, new_question;
    TextInputEditText question, answer;
    ImageView confirm, next, prev;
    TextInputLayout answer_layout;
    TextView questionCounter_txt;
    RelativeLayout new_root;
    RecyclerView list;
    NewAnswerAdapter newAnswerAdapter;
    List<Answer> answers = new ArrayList<>();
    int questionNumber = 1;

    public NewQuestion_NewFragment(Questionnaire questionnaire) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_new_question_new, container, false);

            ((NewQuestionActivity) getActivity()).setSeekBar(2);

            initViews();
            controllerViews();
            start();

            return view;
        } catch (Exception e) {
            StaticFun.setLog("id :" + questionnaire.getUserId(), e.getMessage().toString(), "new question new - create");
            return view;
        }

    }

    private void start() {
        questionNumber = 1;
        questionCounter_txt.setText(questionNumber + "");
        questionnaire.getQuestions().add(new Question());
        setAnswer();
        setAdapter();
    }

    private void initViews() {
        testQuestion = view.findViewById(R.id.testQuestion);
        answer_layout = view.findViewById(R.id.answer_layout);
        question = view.findViewById(R.id.question);
        new_question = view.findViewById(R.id.new_question);
        new_question.setChecked(true);
        confirm = view.findViewById(R.id.confirm);
        next = view.findViewById(R.id.next);
        prev = view.findViewById(R.id.prev);
        answer_layout = view.findViewById(R.id.answer_layout);
        questionCounter_txt = view.findViewById(R.id.questionCounter);
        list = view.findViewById(R.id.list);
        new_root = view.findViewById(R.id.new_root);
        answer = view.findViewById(R.id.answer);
    }

    private void controllerViews() {

        new_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAnswer();
                setAdapter();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValue()) {
                    answers = removeEmptyField(newAnswerAdapter.getAnswer());
                    questionnaire.getQuestions().get(questionNumber - 1).setAnswers(answers);
                    questionnaire.getQuestions().get(questionNumber - 1).setQuestion(question.getText().toString());
                    questionnaire.getQuestions().get(questionNumber - 1).setTest(!testQuestion.isChecked());

                    NewQuestion_ConfirmFragment newQuestion_confirmFragment = new NewQuestion_ConfirmFragment(questionnaire);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, newQuestion_confirmFragment).commit();
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.CustomMaterialDialog);
                    builder.setMessage("تمایل دارین این سوال حذف شود؟");

                    String yes = "بله";
                    String no = "خیر";

                    builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            questionnaire.getQuestions().remove(questionNumber - 1);
                            NewQuestion_ConfirmFragment newQuestion_confirmFragment = new NewQuestion_ConfirmFragment(questionnaire);
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frameLayout, newQuestion_confirmFragment).commit();
                        }
                    });

                    builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toasty.error(getContext(), "لطفا فیلد های مورد نظر را تکمیل نمایید.", Toasty.LENGTH_LONG).show();

                        }
                    });

                    builder.show();

                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionnaire.getQuestions().size() > questionNumber) {
                    questionNumber++;
                    questionCounter_txt.setText(questionNumber + "");
                    question.setText(questionnaire.getQuestions().get(questionNumber - 1).getQuestion().toString());
                    if (questionnaire.getQuestions().get(questionNumber - 1).isTest()) {
                        answers = questionnaire.getQuestions().get(questionNumber - 1).getAnswers();
                        setAdapter();
                        answer_layout.setVisibility(View.GONE);
                        new_root.setVisibility(View.VISIBLE);
                        list.setVisibility(View.VISIBLE);
                    } else {
                        answer_layout.setVisibility(View.VISIBLE);
                        new_root.setVisibility(View.GONE);
                        list.setVisibility(View.GONE);
                    }
                } else {
                    answers = newAnswerAdapter.getAnswer();
                    questionnaire.getQuestions().get(questionNumber - 1).setAnswers(answers);
                    newAnswerAdapter.clearList();
                    questionnaire.getQuestions().get(questionNumber - 1).setQuestion(question.getText().toString());
                    questionnaire.getQuestions().get(questionNumber - 1).setTest(!testQuestion.isChecked());
                    question.setText("");
                    answers = new ArrayList<>();
                    questionNumber++;
                    questionCounter_txt.setText(questionNumber + "");
                    newQuestion();
                }

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionnaire.getQuestions().size() == questionNumber)
                    alertDialog_saveQuestion();
                else preViues();

            }
        });

        testQuestion.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    answers = null;
                    answer_layout.setVisibility(View.VISIBLE);
                    new_root.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                } else {
                    answer_layout.setVisibility(View.GONE);
                    new_root.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    answers = new ArrayList<>();
                    setAnswer();
                    setAdapter();
                }
            }
        });
    }

    private List<Answer> removeEmptyField(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getAnswer().length() == 0) {
                answers.remove(i);
            }
        }
        return answers;
    }

    private void setAdapter() {
        newAnswerAdapter = null;
        newAnswerAdapter = new NewAnswerAdapter(getContext(), answers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(newAnswerAdapter);

        newAnswerAdapter.setOnClickListener(new NewAnswerAdapter.OnClickListener() {
            @Override
            public void onClickListener(int pos) {
                answers = newAnswerAdapter.getAnswer();
                if (answers.size() > 2) {
                    answers.remove(pos);
                    setAdapter();
                    if (new_root.getVisibility() == View.GONE)
                        new_root.setVisibility(View.VISIBLE);
                } else {
                    Toasty.error(getContext(), "نمی توانید کمتر از دو گزینه داشته باشید", Toasty.LENGTH_LONG, true).show();
                }

            }
        });
    }

    private void setAnswer() {
        answers.add(new Answer(""));
        answers.add(new Answer(""));

    }

    private void newAnswer() {
        answers = newAnswerAdapter.getAnswer();
        answers.add(new Answer(""));
        if (answers.size() == 4) {
            new_root.setVisibility(View.GONE);
        }
    }

    private void newQuestion() {
        questionnaire.getQuestions().add(new Question());
        newAnswerAdapter.clearList();
        new_root.setVisibility(View.VISIBLE);
        setAnswer();
        setAdapter();
    }

    private void alertDialog_saveQuestion() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.CustomMaterialDialog);
        builder.setMessage("تمایل دارین این سوال ذخیر شود؟");

        String yes = "بله";
        String no = "خیر";

        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // save the question
                answers = newAnswerAdapter.getAnswer();
                questionnaire.getQuestions().get(questionNumber - 1).setAnswers(answers);
                newAnswerAdapter.clearList();
                questionnaire.getQuestions().get(questionNumber - 1).setQuestion(question.getText().toString());
                questionnaire.getQuestions().get(questionNumber - 1).setTest(!testQuestion.isChecked());
                question.setText("");
                answers = new ArrayList<>();

                preViues();

            }
        });

        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                questionnaire.getQuestions().remove(questionNumber - 1);
                questionNumber--;

                preViues();
            }
        });

        builder.show();
    }

    private void preViues() {
        questionNumber--;
        questionCounter_txt.setText(questionNumber + "");
        if (questionnaire.getQuestions().get(questionNumber - 1).isTest()) {
            question.setText(questionnaire.getQuestions().get(questionNumber - 1).getQuestion().toString());
            answers = questionnaire.getQuestions().get(questionNumber - 1).getAnswers();
            setAdapter();
            answer_layout.setVisibility(View.GONE);
            new_root.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);
        } else {
            question.setText(questionnaire.getQuestions().get(questionNumber - 1).getQuestion().toString());
            answer_layout.setVisibility(View.VISIBLE);
            new_root.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
        }
    }

    private boolean checkValue() {
        answers = newAnswerAdapter.getAnswer();
        if (testQuestion.isChecked()) {
            if (question.getText().length() > 0)
                return true;
            else
                return false;
        } else {
            if (answers.size() >= 2 && answers.get(0).getAnswer().length() > 0 && answers.get(1).getAnswer().length() > 0 && question.getText().length() > 0) {
                return true;
            } else {
                return false;
            }
        }

    }
}