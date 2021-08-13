package com.mohammadkz.porsno_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.viewHolder> {

    private Context context;
    List<GetQuestionResponse> questionnaireList;
    public OnItemClickListener onItemClickListener;


    public QuestionAdapter(Context context, List<GetQuestionResponse> questionnaireList) {
        this.context = context;
        this.questionnaireList = questionnaireList;
    }

    @NonNull
    @Override
    public QuestionAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_question, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.viewHolder viewHolder, int i) {
        try {
            viewHolder.name.setText(questionnaireList.get(i).getQuestionName());
            viewHolder.dec.setText(questionnaireList.get(i).getDescription());
            viewHolder.visit.setText(questionnaireList.get(i).getViews());
            viewHolder.done.setText(questionnaireList.get(i).getAnswers());

            boolean expended = questionnaireList.get(i).isExpended();
            viewHolder.describe.setVisibility(expended ? View.VISIBLE : View.GONE);

            if (questionnaireList.get(i).isExpended()) {
                viewHolder.expend.setImageResource(R.drawable.ic_arrow_drop_up);
            } else {
                viewHolder.expend.setImageResource(R.drawable.ic_arrow_drop_down);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return questionnaireList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView name, dec, visit, done;
        ImageView icon, expend;
        CardView describe;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            dec = itemView.findViewById(R.id.dec);
            visit = itemView.findViewById(R.id.visit);
            done = itemView.findViewById(R.id.done);
            icon = itemView.findViewById(R.id.icon);
            describe = itemView.findViewById(R.id.describe);
            expend = itemView.findViewById(R.id.expend);

            expend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetQuestionResponse questionnaire = questionnaireList.get(getAdapterPosition());
                    questionnaire.setExpended(!questionnaire.isExpended());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });

        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}
