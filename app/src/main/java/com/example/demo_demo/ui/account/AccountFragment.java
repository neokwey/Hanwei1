package com.example.demo_demo.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.demo_demo.LoginActivity;
import com.example.demo_demo.MainActivity;
import com.example.demo_demo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private DatabaseReference firebase;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        final EditText account_name = root.findViewById(R.id.account_name);
        final EditText account_phoneNumber = root.findViewById(R.id.account_phone_number);
        final EditText account_email = root.findViewById(R.id.account_email);
        final EditText account_deliveryAddress = root.findViewById(R.id.account_delivery_address);
        final Button btnEdit_Save = root.findViewById(R.id.btn_edit);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString("SHARED_PREF_USERNAME", "");

        firebase = FirebaseDatabase.getInstance().getReference("Users");
        Query query = firebase.orderByChild("Username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userID = dataSnapshot.getKey();
                }

                firebase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String full_name = snapshot.child("Name").getValue().toString();
                        String phoneNumber = snapshot.child("Phone_Number").getValue().toString();
                        String email = snapshot.child("Email").getValue().toString();
                        String deliveryAddress = snapshot.child("Delivery_Address").getValue().toString();
                        account_name.setText(full_name);
                        account_phoneNumber.setText(phoneNumber);
                        account_email.setText(email);
                        account_deliveryAddress.setText(deliveryAddress);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEdit_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEdit_Save.getText().equals("Edit")){
                    btnEdit_Save.setText("Save");
                    account_name.setEnabled(true);
                    account_phoneNumber.setEnabled(true);
                    account_email.setEnabled(true);
                    account_deliveryAddress.setEnabled(true);
                }
                else if (btnEdit_Save.getText().equals("Save")){

                    if (account_name.getText().toString().isEmpty()) {
                        account_name.setError("Name is Required");
                        return;
                    }

                    for (int i = 0; i < account_name.getText().toString().length(); i++) {
                        char name = account_name.getText().toString().charAt(i);
                        if (!Character.isLetter(name) && !Character.isWhitespace(name)) {
                            account_name.setError("Invalid Name");
                            return;
                        }
                    }

                    if (account_phoneNumber.getText().toString().isEmpty()) {
                        account_phoneNumber.setError("Phone Number is Required");
                        return;
                    }

                    if (!account_phoneNumber.getText().toString().matches("(01)[0-46-9][0-9]{7,8}")) {
                        account_phoneNumber.setError("Please follow this format 01XXXXXXXX");
                        return;
                    }

                    if (account_email.getText().toString().isEmpty()) {
                        account_email.setError("Email is Required");
                        return;
                    }

                    CharSequence email_input = account_email.getText().toString();
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_input).matches()) {
                        account_email.setError("Invalid Email Format");
                        return;
                    }

                    if (account_deliveryAddress.getText().toString().isEmpty()) {
                        account_deliveryAddress.setError("Delivery Address is Required");
                        return;
                    }

                    DatabaseReference user_data = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put("Name", account_name.getText().toString());
                    updates.put("Phone_Number", account_phoneNumber.getText().toString());
                    updates.put("Email", account_email.getText().toString());
                    updates.put("Delivery_Address", account_deliveryAddress.getText().toString());
                    user_data.updateChildren(updates);
                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

                    btnEdit_Save.setText("Edit");
                    account_name.setEnabled(false);
                    account_phoneNumber.setEnabled(false);
                    account_email.setEnabled(false);
                    account_deliveryAddress.setEnabled(false);
                }
            }
        });

        return root;
    }
}