package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        TextView return_login = findViewById(R.id.return_login);
        return_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Button btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText username = findViewById(R.id.signup_username);
                EditText password = findViewById(R.id.signup_password);
                EditText confirmPassword = findViewById(R.id.signup_confirmPassword);

                final String txt_username = username.getText().toString();
                final String txt_password = password.getText().toString();
                String txt_confirmPassword = confirmPassword.getText().toString();

                if (txt_username.isEmpty()){
                    username.setError("Username is Required");
                    return;
                }

                if (txt_username.length() < 6) {
                    username.setError("Username requires at least six characters.");
                    return;
                }

                for (int i = 0; i < txt_username.length(); i++) {
                    char name = txt_username.charAt(i);
                    if (Character.isWhitespace(name)) {
                        username.setError("Username cannot contains whitespace.");
                        return;
                    }
                }

                if(txt_password.isEmpty()){
                    password.setError("Password is Required.");
                    return;
                }

                if (txt_password.length() < 6) {
                    password.setError("Password requires at least six characters.");
                    return;
                }

                if (txt_confirmPassword.isEmpty()){
                    confirmPassword.setError("Confirm Password is Required");
                    return;
                }

                if (!txt_password.equals(txt_confirmPassword)){
                    confirmPassword.setError("Password and Confirm Password is not match.");
                    return;
                }

                if (!txt_username.isEmpty() && !txt_password.isEmpty() && !txt_confirmPassword.isEmpty()){
                    try {
                        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference("Users");
                        firebase.child(txt_username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
                                    intent.putExtra("Username", txt_username);
                                    intent.putExtra("Password", txt_password);
                                    startActivity(intent);
                                }
                                else {
                                    new AlertDialog.Builder(RegisterActivity.this)
                                            .setMessage("Username already exist.")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }catch (Exception ex){
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setMessage("Something Went Wrong. Please Try Again.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            }
        });
    }
}
