package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity2 extends AppCompatActivity {

    private DatabaseReference firebase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("Username");
        final String password = intent.getStringExtra("Password");

        TextView return_login = findViewById(R.id.return_login2);
        return_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity2.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Button btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final EditText name = findViewById(R.id.signup_name);
                EditText phoneNumber = findViewById(R.id.signup_phoneNumber);
                EditText email = findViewById(R.id.signup_email);
                EditText delivery_address = findViewById(R.id.signup_delivery_address);

                final String txt_name = name.getText().toString();
                final String txt_phoneNumber = phoneNumber.getText().toString();
                final String txt_email = email.getText().toString();
                final String txt_delivery_address = delivery_address.getText().toString();

                if(txt_name.isEmpty()){
                    name.setError("Name is Required.");
                    return;
                }

                for (int i = 0; i < txt_name.length(); i++) {
                    char c_name = txt_name.charAt(i);
                    if (!Character.isLetter(c_name) && !Character.isWhitespace(c_name)) {
                        name.setError("Invalid Name");
                        return;
                    }
                }

                if (txt_phoneNumber.isEmpty()){
                    phoneNumber.setError("Phone Number is Required");
                    return;
                }

                if (!txt_phoneNumber.matches("(01)[0-46-9][0-9]{7,8}")) {
                    phoneNumber.setError("Please follow this format 01XXXXXXXX");
                    return;
                }

                if(txt_email.isEmpty()){
                    email.setError("Email is Required.");
                    return;
                }

                CharSequence email_input = txt_email;
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_input).matches()) {
                    email.setError("Invalid Email Format");
                    return;
                }

                if(txt_delivery_address.isEmpty()){
                    delivery_address.setError("Delivery Address is Required.");
                    return;
                }

                if (!txt_name.isEmpty() && !txt_phoneNumber.isEmpty() && !txt_email.isEmpty() && !txt_delivery_address.isEmpty()){
                    try {
                        firebase = FirebaseDatabase.getInstance().getReference("Users");
                        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = (int)snapshot.getChildrenCount();
                                int number = count;
                                firebase.child("Count").setValue(number);

                                if (number < 10) {
                                    userID = "U000" + number;
                                }
                                else if (number < 100) {
                                    userID = "U00" + number;
                                }
                                else if (number < 1000) {
                                    userID = "U0" + number;
                                }
                                else if (number < 10000) {
                                    userID = "U" + number;
                                }

                                firebase.child(userID).child("Username").setValue(username);
                                firebase.child(userID).child("Password").setValue(password);
                                firebase.child(userID).child("Name").setValue(txt_name);
                                firebase.child(userID).child("Phone_Number").setValue(txt_phoneNumber);
                                firebase.child(userID).child("Email").setValue(txt_email);
                                firebase.child(userID).child("Delivery_Address").setValue(txt_delivery_address);
                                firebase.child(userID).child("Status").setValue("Active");
                                Toast.makeText(RegisterActivity2.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }catch (Exception ex){
                        new AlertDialog.Builder(RegisterActivity2.this)
                                .setMessage("Something Went Wrong. Please Try Again.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
                Intent intent1 = new Intent(RegisterActivity2.this, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
            }
        });
    }
}