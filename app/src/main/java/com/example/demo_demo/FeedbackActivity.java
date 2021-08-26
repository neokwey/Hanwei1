package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.hsalf.smileyrating.SmileyRating;
import com.hsalf.smileyrating.helper.SmileyActiveIndicator;
import com.hsalf.smileyrating.smileys.base.Smiley;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        getSupportActionBar().setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SmileyRating smileyRating = (SmileyRating)findViewById(R.id.smile_rating);
        final EditText feedback = findViewById(R.id.editText);
        Button btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback.setText("");
                smileyRating.setRating(SmileyRating.Type.NONE);
            }
        });

        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback.getText().toString().isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Feedback cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    SmileyRating.Type type = smileyRating.getSelectedSmiley();
                    final int rating = type.getRating();
                    final String user_feedback = feedback.getText().toString();
                    SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
                    final String username = sharedPreferences.getString("SHARED_PREF_USERNAME", "");
                    final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference("Feedback");
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            int count = (int)snapshot.getChildrenCount();
                            int number = count;
                            firebase.child("Count").setValue(number);
                            String feedbackID = "";
                            if (number < 10) {
                                feedbackID = "F000" + number;
                            }
                            else if (number < 100) {
                                feedbackID = "F00" + number;
                            }
                            else if (number < 1000) {
                                feedbackID = "F0" + number;
                            }
                            else if (number < 10000) {
                                feedbackID = "F" + number;
                            }

                            firebase.child(feedbackID).child("FeedbackID").setValue(feedbackID);
                            firebase.child(feedbackID).child("Ratings").setValue(rating);
                            firebase.child(feedbackID).child("Content").setValue(user_feedback);
                            firebase.child(feedbackID).child("Username").setValue(username);
                            Toast.makeText(FeedbackActivity.this, "Your feedback is submitted.", Toast.LENGTH_SHORT).show();
                            feedback.setText("");
                            smileyRating.setRating(SmileyRating.Type.NONE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

    }
}
