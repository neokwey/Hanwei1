package com.example.demo_demo.ui.change_password;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.demo_demo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment {

    private ChangePasswordViewModel changePasswordViewModel;
    private DatabaseReference firebase;
    private String username, userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        changePasswordViewModel =
                ViewModelProviders.of(this).get(ChangePasswordViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_change_password, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("SHARED_PREF_USERNAME", "");

        Button btn_change_password = root.findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText password = (EditText)root.findViewById(R.id.change_password);
                final EditText confirmPassword = (EditText)root.findViewById(R.id.change_confirmPassword);
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

                            DatabaseReference user_data = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("Password", txt_password);
                            user_data.updateChildren(updates);
                            Toast.makeText(getContext(), "Password is Changed.", Toast.LENGTH_SHORT).show();
                            password.setText("");
                            confirmPassword.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return root;
    }
}