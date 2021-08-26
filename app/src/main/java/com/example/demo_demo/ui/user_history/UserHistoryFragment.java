package com.example.demo_demo.ui.user_history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_demo.Announcement;
import com.example.demo_demo.AnnouncementAdapter;
import com.example.demo_demo.R;
import com.example.demo_demo.RecyclerTouchListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserHistoryFragment extends Fragment {

    private UserHistoryViewModel userHistoryViewModel;
    private List<UserHistory> userHistoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserHistoryAdapter userHistoryAdapter;
    private DatabaseReference firebase;
    private String username, orderID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userHistoryViewModel =
                ViewModelProviders.of(this).get(UserHistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user_history, container, false);
//        final TextView textView = root.findViewById(R.id.text_user_history);
//        userHistoryViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        recyclerView = (RecyclerView)root.findViewById(R.id.history_recycler_view);

        userHistoryAdapter = new UserHistoryAdapter(userHistoryList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(userHistoryAdapter);

        prepareUserHistoryDetail();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserHistory userHistory = userHistoryList.get(position);
                String orderID = userHistory.getOrderID();
                Intent intent = new Intent(getContext(), UserHistoryDetailActivity.class);
                intent.putExtra("orderID", orderID);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return root;
    }

    private void prepareUserHistoryDetail() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("SHARED_PREF_USERNAME", "");
        firebase = FirebaseDatabase.getInstance().getReference("Order");
        Query query = firebase.orderByChild("Username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        orderID = dataSnapshot.getKey();
                        firebase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String OrderID = snapshot.child("OrderID").getValue().toString();
                                String status = snapshot.child("Status").getValue().toString();
                                UserHistory userHistory = new UserHistory(OrderID, status);
                                userHistoryList.add(userHistory);
                                userHistoryAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}