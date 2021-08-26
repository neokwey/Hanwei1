package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference firebase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button btn_login = (Button)findViewById(R.id.btnLogin);
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                access();
            }
        });

        TextView forget_password = (TextView)findViewById(R.id.forgetPassword);
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        TextView sign_up = (TextView)findViewById(R.id.signUp);
        sign_up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    public void access(){
        try {
            firebase = FirebaseDatabase.getInstance().getReference().child("Users");
            final EditText username = findViewById(R.id.login_username);
            EditText password = findViewById(R.id.login_password);
            final String txt_username = username.getText().toString();
            final String txt_password = password.getText().toString();

            if(TextUtils.isEmpty(txt_username)){
                username.setError("Username is Required.");
                username.requestFocus();
                return;
            }

            for (int i = 0; i < txt_username.length(); i++) {
                char name = txt_username.charAt(i);
                if (Character.isWhitespace(name)) {
                    username.setError("Username cannot contains whitespace.");
                    return;
                }
            }

            if(TextUtils.isEmpty(txt_password)){
                password.setError("Password is Required.");
                password.requestFocus();
                return;
            }

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
                                String real_password = snapshot.child("Password").getValue().toString();
                                if (txt_password.equals(real_password)){
                                    SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("SHARED_PREF_USERNAME", txt_username);
                                    editor.commit();

                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Unsuccessful.", Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception ex){
            Log.i("Error : ", ex.getMessage());
        }
    }

    public void signUp(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
