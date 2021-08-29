package com.mohammadkz.porsno_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.porsno_android.Model.Advice;
import com.mohammadkz.porsno_android.R;

import java.util.List;

public class AdviceAdapter extends RecyclerView.Adapter<AdviceAdapter.viewHolder> {

    Context context;
    List<Advice> adviceList;

    public AdviceAdapter(Context context, List<Advice> adviceList) {
        this.context = context;
        this.adviceList = adviceList;
    }

    @NonNull
    @Override
    public AdviceAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_advice, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdviceAdapter.viewHolder holder, int position) {
        holder.advice.setText(adviceList.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return adviceList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView advice;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            advice = itemView.findViewById(R.id.advice);
        }
    }
}
