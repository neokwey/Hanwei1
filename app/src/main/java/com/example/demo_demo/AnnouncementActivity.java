package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {

    private List<Announcement> announcementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AnnouncementAdapter announcementAdapter;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement);
        getSupportActionBar().setTitle("Announcement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        announcementAdapter = new AnnouncementAdapter(announcementList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(announcementAdapter);
        
        prepareAnnouncementDetails();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Announcement announcement = announcementList.get(position);
                String title = announcement.getTitle();
                Intent intent = new Intent(AnnouncementActivity.this, AnnouncementDetailActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }){

        });
    }

    private void prepareAnnouncementDetails() {
        firebase = FirebaseDatabase.getInstance().getReference("Announcement");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int)snapshot.getChildrenCount();
                String announcementID = "";
                for (int i = count; i > 0; i--) {
                    if (i < 10) {
                        announcementID = "A000" + i;
                    }
                    else if (i < 100) {
                        announcementID = "A00" + i;
                    }
                    else if (i < 1000) {
                        announcementID = "A0" +i;
                    }
                    else if (i < 10000) {
                        announcementID = "A" + i;
                    }

                    final String AnnouncementID = announcementID;
                    firebase.child(AnnouncementID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String title = snapshot.child("Title").getValue().toString();
                            String date = snapshot.child("aDate").getValue().toString();
                            String status = snapshot.child("Status").getValue().toString();
                            if (status.equals("Valid")) {
                                Announcement announcement = new Announcement(title, date);
                                announcementList.add(announcement);
                                announcementAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
