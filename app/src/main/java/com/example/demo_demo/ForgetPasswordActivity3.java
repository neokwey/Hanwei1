package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity3 extends AppCompatActivity {

    private String username, userID;
    private EditText password, confirmPassword;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password3);

        username = getIntent().getStringExtra("Username");

        password = (EditText)findViewById(R.id.forget_reset_password);
        confirmPassword = (EditText)findViewById(R.id.forget_reset_confirmPassword);

        TextView return_login = findViewById(R.id.return_login);
        return_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity3.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Button btn_reset_password = findViewById(R.id.btn_reset_password);
        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String txt_password = password.getText().toString();
                String txt_confirmPassword = confirmPassword.getText().toString();

                if(txt_password.isEmpty()){
                    password.setError("Password is Required.");
                    return;
                }

                if (txt_confirmPassword.isEmpty()){
                    confirmPassword.setError("Confirm Password is Required");
                    return;
                }

                if (txt_password.length() < 6) {
                    password.setError("Password requires at least six characters.");
                    return;
                }

                if (!txt_password.equals(txt_confirmPassword)){
                    confirmPassword.setError("Password and Confirm Password is not match.");
                    return;
                }

                firebase = FirebaseDatabase.getInstance().getReference("Users");
                Query query = firebase.orderByChild("Username").equalTo(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                userID = dataSnapshot.getKey();
                            }

                            DatabaseReference user_password = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("Password", txt_password);
                            user_password.updateChildren(updates);
                            Toast.makeText(ForgetPasswordActivity3.this, "Password is reset.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgetPasswordActivity3.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}