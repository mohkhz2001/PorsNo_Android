package com.mohammadkz.porsno_android.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.mohammadkz.porsno_android.Model.Answer;
import com.mohammadkz.porsno_android.R;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.viewHolder> {

    Context context;
    List<Answer> answers;
    OnCheckedChangeListener onCheckedChangeListener;
    OnClickListener onClickListener;
    List<viewHolder> views = new ArrayList<>();

    public AnswerAdapter(Context context, List<Answer> answers) {
        this.context = context;
        this.answers = answers;
    }

    @NonNull
    @Override
    public AnswerAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_answer, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.viewHolder viewHolder, int i) {
        viewHolder.answer.setText(answers.get(i).getAnswer());
        views.add(viewHolder);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        AnimatedCheckBox checkbox;
        TextView answer;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            answer = itemView.findViewById(R.id.answer);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickListener(checkbox.isChecked() , getAdapterPosition() , views);
                }
            });


        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked, int pos, List<viewHolder> views);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnClickListener {
        void onClickListener( boolean isChecked, int pos, List<viewHolder> views);
    }

    public void setOnClickListener(OnClickListener OnClickListener) {
        this.onClickListener = OnClickListener;
    }

}
