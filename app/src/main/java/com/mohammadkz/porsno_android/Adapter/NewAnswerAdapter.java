package com.mohammadkz.porsno_android.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.R;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class NewAnswerAdapter extends RecyclerView.Adapter<NewAnswerAdapter.viewHolder> {

    Context context;
    List<Answer> answers;
    OnClickListener onClickListener;

    public NewAnswerAdapter(Context context, List<Answer> answers) {
        this.context = context;
        this.answers = answers;
    }

    @NonNull
    @Override
    public NewAnswerAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_new_answer, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewAnswerAdapter.viewHolder holder, int position) {
        holder.answer_field.setText(answers.get(position).getAnswer().toString());

        holder.answer_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                answers.get(position).setAnswer(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public List<Answer> getAnswer() {
        return answers;
    }

    public int newAnswer() {
        if (answers.size() <= 4) {
            answers.add(new Answer(""));
            notifyItemInserted(answers.size());
        }
        return answers.size();
    }

    public int removeAnswer(int i) {
        if (answers.size() != 2) {
            answers.remove(i);
            notifyItemRemoved(i);
            return answers.size();
        } else {
            Toasty.error(context, "نمی توانید کمتر از دو گزینه داشته باشید", Toasty.LENGTH_LONG, true).show();
            return answers.size();
        }
    }

    public void clearList() {
        answers = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        EditText answer_field;
        ImageView remove;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            answer_field = itemView.findViewById(R.id.answer_field);
            remove = itemView.findViewById(R.id.remove);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickListener(getAdapterPosition());
                }
            });
        }
    }

    public interface OnClickListener {
        void onClickListener(int pos);
    }

    public void setOnClickListener(OnClickListener OnClickListener) {
        this.onClickListener = OnClickListener;
    }
}
