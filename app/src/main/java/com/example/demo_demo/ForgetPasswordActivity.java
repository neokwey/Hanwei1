package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ForgetPasswordActivity extends AppCompatActivity {

    private DatabaseReference firebase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        TextView return_login = findViewById(R.id.return_login);
        return_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Button btn_reset_password = findViewById(R.id.btn_reset_password);
        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText username = (EditText)findViewById(R.id.forgot_username);
                EditText phoneNumber = (EditText)findViewById(R.id.forgot_phoneNumber);
                final String txt_username = username.getText().toString();
                final String txt_phoneNumber = phoneNumber.getText().toString();

                if (txt_username.isEmpty()) {
                    username.setError("Username is Required.");
                    return;
                }

                if (txt_phoneNumber.isEmpty()) {
                    phoneNumber.setError("Phone Number is Required");
                    return;
                }

                if (!txt_phoneNumber.matches("(01)[0-46-9][0-9]{7,8}")) {
                    phoneNumber.setError("Please follow this format 01XXXXXXXX");
                    return;
                }

                try {
                    firebase = FirebaseDatabase.getInstance().getReference("Users");
                    Query query = firebase.orderByChild("Username").equalTo(txt_username);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    userID = dataSnapshot.getKey();
                                }

                                firebase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String phoneNo = snapshot.child("Phone_Number").getValue().toString();
                                        if (txt_phoneNumber.equals(phoneNo)) {
                                            Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordActivity2.class);
                                            intent.putExtra("Username", txt_username);
                                            intent.putExtra("PhoneNumber", txt_phoneNumber);
                                            startActivity(intent);
                                        }
                                        else {
                                            new AlertDialog.Builder(ForgetPasswordActivity.this)
                                                    .setMessage("Username and Phone Number is not match.")
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else {
                                new AlertDialog.Builder(ForgetPasswordActivity.this)
                                        .setMessage("Username and Phone Number is not match.")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }catch (Exception ex) {
                    new AlertDialog.Builder(ForgetPasswordActivity.this)
                            .setMessage("Something Went Wrong. Please Try Again.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });
    }
}