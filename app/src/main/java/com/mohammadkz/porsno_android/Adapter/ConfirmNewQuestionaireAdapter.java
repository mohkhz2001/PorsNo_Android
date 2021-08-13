package com.mohammadkz.porsno_android.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.QuestionAnswer;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.R;

import java.util.ArrayList;
import java.util.List;

public class ConfirmNewQuestionaireAdapter extends RecyclerView.Adapter<ConfirmNewQuestionaireAdapter.viewHolder> {

    private Context context;
    private List<Question> questions;
    private List<QuestionAnswer> answers = new ArrayList<>();
    boolean editable;

    public ConfirmNewQuestionaireAdapter(Context context, List<Question> questions, boolean editable) {
        this.context = context;
        this.questions = questions;
        this.editable = editable;
    }

    @NonNull
    @Override
    public ConfirmNewQuestionaireAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_confirm_new_questionaire, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmNewQuestionaireAdapter.viewHolder viewHolder, int position) {



        viewHolder.question.setText(questions.get(position).getQuestion());

        if (questions.get(position).isTest()) {
            viewHolder.answer_layout.setVisibility(View.GONE);
            viewHolder.setAdapter(questions.get(position).getAnswers());

            viewHolder.answerAdapter.setOnClickListener(new AnswerAdapter.OnClickListener() {
                @Override
                public void onClickListener(boolean isChecked, int pos, List<AnswerAdapter.viewHolder> views) {
                    QuestionAnswer questionAnswer = new QuestionAnswer(views.get(pos).answer.getText().toString(), String.valueOf(position + 1));

                    if (isChecked) {

                        for (int i = 0; i < answers.size(); i++) {
                            if (answers.get(i).getQuestionNumber().equals(questionAnswer.getQuestionNumber())) {
                                answers.remove(i);
                                break;
                            }
                        }

                        for (int i = 0; i < views.size(); i++) {
                            if (i == pos) {
                                views.get(i).checkbox.setChecked(false);
                            }
                        }

                        answers.remove(questionAnswer);

                    }
                    else if (!isChecked) {

                        if (!checkAvailable(questionAnswer))
                            answers.add(questionAnswer);
                        else {
                            for (int i = 0; i < answers.size(); i++) {
                                if (answers.get(i).getQuestionNumber().equals(questionAnswer.getQuestionNumber())) {
                                    answers.get(i).setAnswer(questionAnswer.getAnswer());
                                    break;
                                }
                            }
                        }

                        for (int i = 0; i < views.size(); i++) {
                            if (i != pos) {
                                views.get(i).checkbox.setChecked(false);
                            }else if (i == pos){
                                views.get(i).checkbox.setChecked(true , true);
                            }
                        }
                    }

                }
            });

        } else {
            viewHolder.answer_layout.setVisibility(View.VISIBLE);
        }

        if (editable) {
            viewHolder.edit.setVisibility(View.VISIBLE);
            viewHolder.remove.setVisibility(View.VISIBLE);
        } else {
            viewHolder.edit.setVisibility(View.GONE);
            viewHolder.remove.setVisibility(View.GONE);
        }

    }
    private void test(List<AnswerAdapter.viewHolder> views ,  int pos){
        for (int i = 0; i < views.size(); i++) {
            if (i != pos) {
                views.get(i).checkbox.setChecked(false);
            }
        }
    }

    private boolean checkAvailable(QuestionAnswer questionAnswer) {
        boolean check = false;
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getQuestionNumber().equals(questionAnswer.getQuestionNumber())) {
                check = true;
                break;
            } else if (answers.size() - 1 == i) {
                check = false;
            }
        }
        return check;
    }

    public List<QuestionAnswer> answers() {
        return answers;
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView remove, edit;
        TextView question;
        TextInputLayout answer_layout;
        TextInputEditText answerField;
        RecyclerView answerList;
        AnswerAdapter answerAdapter;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.remove);
            edit = itemView.findViewById(R.id.edit);
            question = itemView.findViewById(R.id.question);
            answerField = itemView.findViewById(R.id.answerField);
            answer_layout = itemView.findViewById(R.id.answer_layout);
            answerList = itemView.findViewById(R.id.answerList);

            answerField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    QuestionAnswer questionAnswer = new QuestionAnswer(s.toString(), String.valueOf(getAdapterPosition() + 1));

                    if (answers.size() == 0) {
                        answers.add(questionAnswer);
                    } else
                        for (int i = 0; i < answers.size(); i++) {
                            if (answers.get(i).getQuestionNumber().equals(questionAnswer.getQuestionNumber())) {
                                answers.get(i).setAnswer(questionAnswer.getAnswer().toString());
                                break;
                            } else if (i == answers.size() - 1) {
                                answers.add(questionAnswer);
                            }
                        }

                }
            });

        }

        public void setAdapter(List<Answer> answers) {
            answerAdapter = new AnswerAdapter(context, answers);
            answerList.setLayoutManager(new LinearLayoutManager(context));
            answerList.setAdapter(answerAdapter);
        }
    }
}
