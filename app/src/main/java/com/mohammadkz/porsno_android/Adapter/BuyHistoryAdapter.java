package com.mohammadkz.porsno_android.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.porsno_android.Model.Response.HistoryBuyResponse;
import com.mohammadkz.porsno_android.R;

import java.sql.Timestamp;
import java.util.List;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;


public class BuyHistoryAdapter extends RecyclerView.Adapter<BuyHistoryAdapter.ViewHolder> {

    Context context;
    List<HistoryBuyResponse> historyBuyList;

    public BuyHistoryAdapter(Context context, List<HistoryBuyResponse> historyBuyList) {
        this.context = context;
        this.historyBuyList = historyBuyList;
    }

    @NonNull
    @Override
    public BuyHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_history_buy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyHistoryAdapter.ViewHolder holder, int position) {
        holder.trackingCode.setText(historyBuyList.get(position).getPorsnoTrackId());
        holder.price.setText(historyBuyList.get(position).getAmount());
        holder.trackingCode.setText(historyBuyList.get(position).getPorsnoTrackId());
        holder.date.setText(dateConvertor(historyBuyList.get(position).getDate()));


    }

    private String dateConvertor(String timeStamp) {

        try {
            PersianDate pdate = new PersianDate(Long.valueOf(timeStamp));
            PersianDateFormat pdformater1 = new PersianDateFormat("yyyy/mm/dd");

            return pdformater1.format(pdate);//1396/05/20
        } catch (Exception e) {
            Timestamp timestamp_sy = new Timestamp(System.currentTimeMillis());
            PersianDate pdate = new PersianDate(timestamp_sy);
            PersianDateFormat pdformater1 = new PersianDateFormat("y/m/d");
            Log.e(" ", pdformater1.format(pdate));
            return "14" + pdformater1.format(pdate);//1396/05/20
        }
    }

    @Override
    public int getItemCount() {
        return historyBuyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView kind, trackingCode, price, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            kind = itemView.findViewById(R.id.kind);
            trackingCode = itemView.findViewById(R.id.trackingCode);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
        }
    }
}