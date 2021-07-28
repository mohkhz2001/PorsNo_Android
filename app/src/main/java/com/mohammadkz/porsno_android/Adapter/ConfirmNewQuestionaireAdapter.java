package com.mohammadkz.porsno_android.Adapter;

import android.content.Context;
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
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.R;

import java.util.ArrayList;
import java.util.List;

public class ConfirmNewQuestionaireAdapter extends RecyclerView.Adapter<ConfirmNewQuestionaireAdapter.viewHolder> {

    private Context context;
    private List<Question> questions;
    private List<Answer> answers = new ArrayList<>();

    public ConfirmNewQuestionaireAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
    }

    @NonNull
    @Override
    public ConfirmNewQuestionaireAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_confirm_new_questionaire, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmNewQuestionaireAdapter.viewHolder viewHolder, int pos) {

        viewHolder.question.setText(questions.get(pos).getQuestion());

        if (questions.get(pos).isTest()) {
            viewHolder.answer_layout.setVisibility(View.GONE);
            viewHolder.setAdapter(questions.get(pos).getAnswers());

            viewHolder.answerAdapter.setOnCheckedChangeListener(new AnswerAdapter.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked, int pos, List<AnswerAdapter.viewHolder> views) {
                    if (views.get(pos).checkbox.isChecked()) {
                        answers.add(new Answer(views.get(pos).answer.getText().toString()));
                        if (views.size() == 2) {
                            if (pos == 1) {
                                views.get(0).checkbox.setChecked(false, true);

                            } else {
                                views.get(1).checkbox.setChecked(false, true);

                            }
                        }
                    }
                }
            });

        } else {
            viewHolder.answer_layout.setVisibility(View.VISIBLE);
        }

    }

    public List<Answer> answers() {
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
        TextInputEditText answer;
        RecyclerView answerList;
        AnswerAdapter answerAdapter;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.remove);
            edit = itemView.findViewById(R.id.edit);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            answer_layout = itemView.findViewById(R.id.answer_layout);
            answerList = itemView.findViewById(R.id.answerList);

        }

        public void setAdapter(List<Answer> answers) {
            answerAdapter = new AnswerAdapter(context, answers);
            answerList.setLayoutManager(new LinearLayoutManager(context));
            answerList.setAdapter(answerAdapter);
        }
    }
}
