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

import com.mohammadkz.porsno_android.Model.Response.AnswerHistoryResponse;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.R;

import java.util.List;

public class AnswerHistoryAdapter extends RecyclerView.Adapter<AnswerHistoryAdapter.ViewHolder> {

    Context context;
    List<AnswerHistoryResponse> list;

    public AnswerHistoryAdapter(Context context, List<AnswerHistoryResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AnswerHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        try {
            viewHolder.name.setText(list.get(i).getQuestionName());
            viewHolder.dec.setText(list.get(i).getDescription());
            viewHolder.visit.setText(list.get(i).getViews());
            viewHolder.done.setText(list.get(i).getAnswers());

            boolean expended = list.get(i).isExpended();
            viewHolder.describe.setVisibility(expended ? View.VISIBLE : View.GONE);

            if (list.get(i).isExpended()) {
                viewHolder.expend.setImageResource(R.drawable.ic_arrow_drop_up);
            } else {
                viewHolder.expend.setImageResource(R.drawable.ic_arrow_drop_down);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dec, visit, done;
        ImageView icon, expend;
        CardView describe;

        public ViewHolder(@NonNull View itemView) {
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
                    AnswerHistoryResponse answerHistoryResponse = list.get(getAdapterPosition());
                    answerHistoryResponse.setExpended(!answerHistoryResponse.isExpended());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
