package com.example.demo_demo.ui.user_history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.TextView;

import com.example.demo_demo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHistoryDetailActivity extends AppCompatActivity {

    private String orderID;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderID = getIntent().getStringExtra("orderID");
        setContentView(R.layout.user_history_detail);
        getSupportActionBar().setTitle(orderID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        final TextView txt_file = (TextView)findViewById(R.id.history_txt_file);
        final TextView txt_date = (TextView)findViewById(R.id.history_txt_date);
        final TextView txt_request_delivery = (TextView)findViewById(R.id.history_txt_request_address);
        final TextView txt_address = (TextView)findViewById(R.id.history_txt_address);
        final TextView txt_status = (TextView)findViewById(R.id.history_txt_status);

        firebase = FirebaseDatabase.getInstance().getReference("Order");
        firebase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int n = Integer.parseInt(snapshot.child("Files").child("Count").getValue().toString());
                for (int i = 1; i <= n; i++) {
                    String count = String.valueOf(i);
                    String filename = snapshot.child("Files").child(count).child("Filename").getValue().toString();
                    txt_file.append(filename + "\n");
                }

                String date = snapshot.child("Order_Date").getValue().toString();
                txt_date.setText(date);

                String request_delivery = snapshot.child("Request_Delivery").getValue().toString();
                txt_request_delivery.setText(request_delivery);

                String address = snapshot.child("Delivery_Address").getValue().toString();
                txt_address.setText(address);

                String status = snapshot.child("Status").getValue().toString();
                txt_status.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}