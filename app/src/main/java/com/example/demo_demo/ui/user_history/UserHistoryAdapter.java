package com.example.demo_demo.ui.user_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_demo.R;

import java.util.List;

public class UserHistoryAdapter extends RecyclerView.Adapter<UserHistoryAdapter.MyViewHolder> {

    private List<UserHistory> userHistoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView orderID, status;

        public MyViewHolder(View view) {
            super(view);
            orderID = (TextView)view.findViewById(R.id.history_orderID);
            status = (TextView)view.findViewById(R.id.history_status);
        }
    }

    public UserHistoryAdapter(List<UserHistory> userHistoryList) {
        this.userHistoryList = userHistoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_history_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserHistory userHistory = userHistoryList.get(position);
        holder.orderID.setText(userHistory.getOrderID());
        holder.status.setText((userHistory.getStatus()));
    }

    @Override
    public int getItemCount() {
        return userHistoryList.size();
    }

}