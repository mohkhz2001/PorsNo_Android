package com.mohammadkz.porsno_android.Fragment.NewQuestion;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.R;

import java.util.ArrayList;
import java.util.List;


public class NewQuestion_NewFragment extends Fragment {

    View view;
    Questionnaire questionnaire;
    AnimatedCheckBox testQuestion;
    TextInputEditText question, answer1_field, answer2_field, answer3_field, answer4_field, answer5_field, answer6_field, answer;
    FloatingActionButton fab_add;
    ImageView confirm, next, prev;
    TextInputLayout answer_layout;
    TextView questionCounter_txt;
    LinearLayout answerLayout;
    ImageView answer1_remove, answer2_remove, answer3_remove, answer4_remove, answer5_remove, answer6_remove;
    RelativeLayout relative1, relative2, relative3, relative4, relative5, relative6;
    int questionNumber = 1;
    int answerCounter = 2;

    public NewQuestion_NewFragment(Questionnaire questionnaire) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_question_new, container, false);

        ((NewQuestionActivity) getActivity()).setSeekBar(1);

        initViews();
        controllerViews();
        start();

        return view;
    }

    private void start() {
        questionNumber = 1;
        questionCounter_txt.setText(questionNumber + "");
        questionnaire.getQuestions().add(new Question());
    }

    private void initViews() {
        testQuestion = view.findViewById(R.id.testQuestion);
        answerLayout = view.findViewById(R.id.layout);
        question = view.findViewById(R.id.question);
        fab_add = view.findViewById(R.id.fab_add);
        confirm = view.findViewById(R.id.confirm);
        next = view.findViewById(R.id.next);
        prev = view.findViewById(R.id.prev);
        answer_layout = view.findViewById(R.id.answer_layout);
        questionCounter_txt = view.findViewById(R.id.questionCounter);

//        answer1_field = view.findViewById(R.id.answer1_field);
//        answer2_field = view.findViewById(R.id.answer2_field);
//        answer3_field = view.findViewById(R.id.answer3_field);
//        answer4_field = view.findViewById(R.id.answer4_field);
//        answer5_field = view.findViewById(R.id.answer5_field);
//        answer6_field = view.findViewById(R.id.answer6_field);
//
//        answer1_remove = view.findViewById(R.id.answer1_remove);
//        answer2_remove = view.findViewById(R.id.answer2_remove);
//        answer3_remove = view.findViewById(R.id.answer3_remove);
//        answer4_remove = view.findViewById(R.id.answer4_remove);
//        answer5_remove = view.findViewById(R.id.answer5_remove);
//        answer6_remove = view.findViewById(R.id.answer6_remove);
//
//        relative1 = view.findViewById(R.id.relative1);
//        relative2 = view.findViewById(R.id.relative2);
//        relative3 = view.findViewById(R.id.relative3);
//        relative4 = view.findViewById(R.id.relative4);
//        relative5 = view.findViewById(R.id.relative5);
//        relative6 = view.findViewById(R.id.relative6);

        answer = view.findViewById(R.id.answer);
    }

    private void controllerViews() {

        testQuestion.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    answer_layout.setVisibility(View.VISIBLE);
                    answerLayout.setVisibility(View.GONE);
                    fab_add.setVisibility(View.GONE);
                } else {
                    answer_layout.setVisibility(View.GONE);
                    answerLayout.setVisibility(View.VISIBLE);
                    fab_add.setVisibility(View.VISIBLE);
                }
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerCounter < 7) {
                    answerCounter++;
                    newAnswer();
                } else {

                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveInputed();

                relative3.setVisibility(View.GONE);
                relative4.setVisibility(View.GONE);
                relative5.setVisibility(View.GONE);
                relative6.setVisibility(View.GONE);

                clearField();

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInputed();

                NewQuestion_ConfirmFragment newQuestion_confirmFragment = new NewQuestion_ConfirmFragment(questionnaire);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, newQuestion_confirmFragment).commit();

            }
        });

        answer1_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative3.setVisibility(View.GONE);
            }
        });

        answer1_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        answer3_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative3.setVisibility(View.GONE);
            }
        });

        answer4_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative4.setVisibility(View.GONE);
            }
        });

        answer5_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative5.setVisibility(View.GONE);
            }
        });

        answer6_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative6.setVisibility(View.GONE);
            }
        });

    }

    private void clearField() {
        answerCounter = 2;
        questionNumber++;
        questionnaire.getQuestions().add(new Question());
        questionCounter_txt.setText(questionNumber + "");
        answer.setText("");
        question.setText("");
        answer1_field.setText("");
        answer2_field.setText("");
        answer3_field.setText("");
        answer4_field.setText("");
        answer5_field.setText("");
        answer6_field.setText("");
    }

    private void newAnswer() {
        switch (answerCounter) {
            case 3:
                relative3.setVisibility(View.VISIBLE);
                break;
            case 4:
                relative4.setVisibility(View.VISIBLE);
                break;
            case 5:
                relative5.setVisibility(View.VISIBLE);
                break;
            case 6:
                fab_add.setVisibility(View.GONE);
                relative6.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void saveInputed() {
        questionnaire.getQuestions().get(questionNumber - 1).setQuestion(question.getText().toString());

        if (testQuestion.isChecked()) {
            questionnaire.getQuestions().get(questionNumber - 1).setTest(false);

        } else {
            questionnaire.getQuestions().get(questionNumber - 1).setTest(true);

            if (relative3.getVisibility() == View.VISIBLE) {

            }
            if (answerCounter == 2) {
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer1_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer2_field.getText().toString()));
            } else if (answerCounter == 3) {
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer1_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer2_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer3_field.getText().toString()));
            } else if (answerCounter == 4) {
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer1_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer2_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer3_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer4_field.getText().toString()));
            } else if (answerCounter == 5) {
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer1_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer2_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer3_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer4_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer5_field.getText().toString()));
            } else if (answerCounter == 6) {
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer1_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer2_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer3_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer4_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer5_field.getText().toString()));
                questionnaire.getQuestions().get(questionNumber - 1).getAnswers().add(new Answer(answer6_field.getText().toString()));
            }
        }
    }
}