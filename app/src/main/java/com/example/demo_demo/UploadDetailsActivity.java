package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hsalf.smileyrating.SmileyRating;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadDetailsActivity extends AppCompatActivity {

    private ListView listView;
    private FileAdapter adapter;
    private ArrayList<Uri> fileUri;
    private ArrayList<String> lastFileString;
    private ArrayList<String> spinnerItems;
    private CheckBox request_delivery;
    private DatabaseReference firebase;
    private StorageReference storage;
    private String username, request_delivery_status, user_delivery_address, userID;
    private ArrayList<String> copies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_details);
        getSupportActionBar().setTitle("Upload Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("SHARED_PREF_USERNAME", "");

        listView = (ListView)findViewById(R.id.list_view);
        copies = new ArrayList<>();

        ArrayList<String> fileString = (ArrayList<String>)getIntent().getSerializableExtra("fileString");

        fileUri = new ArrayList<>();
        for (int count = 0; count < fileString.size(); count++) {
            fileUri.add(Uri.parse(fileString.get(count)));
        }

        lastFileString = new ArrayList<>();
        for (int n = 0; n < fileUri.size(); n++) {
            String fileType = getContentResolver().getType(fileUri.get(n));
            if (fileType.contains("jpeg") || fileType.contains("jpg")) {
                fileType = ".jpg";
            }
            else if (fileType.contains("png")) {
                fileType = ".png";
            }
            else if (fileType.contains("pdf")) {
                fileType = ".pdf";
            }else if (fileType.contains("word")) {
                fileType = ".docx";
            }else if (fileType.contains("powerpoint") || fileType.contains("presentation")) {
                fileType = ".ppt";
            }else if (fileType.contains("excel")) {
                fileType = ".xlsx";
            }
            lastFileString.add(fileUri.get(n).getLastPathSegment() + fileType);
        }

        spinnerItems = new ArrayList<>();
        for (int item = 0; item <= 50; item++) {
            spinnerItems.add(String.valueOf(item));
        }

        adapter = new FileAdapter(lastFileString, spinnerItems, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int which_item = position;

                new AlertDialog.Builder(UploadDetailsActivity.this)
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lastFileString.remove(which_item);
                                fileUri.remove(which_item);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });

        request_delivery = findViewById(R.id.delivery_checkbox);
        request_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UploadDetailsActivity.this)
                        .setIcon(R.drawable.ic_baseline_info_24)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to request delivery service? Additional fees would be charged.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                request_delivery.setChecked(true);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                request_delivery.setChecked(false);
                            }
                        })
                        .show();
            }
        });

        firebase = FirebaseDatabase.getInstance().getReference();

        //Get User's Delivery Address
        Query query = firebase.child("Users").orderByChild("Username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userID = dataSnapshot.getKey();
                }

                firebase.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user_delivery_address = snapshot.child("Delivery_Address").getValue().toString();
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

        storage = FirebaseStorage.getInstance().getReference();
        Button btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UploadDetailsActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to upload the file(s)?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFile();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void uploadFile() {

        if (request_delivery.isChecked()) {
            request_delivery_status = "Yes";
        }
        else {
            request_delivery_status = "No";
            user_delivery_address = "";
        }

        firebase.child("Order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String now = ISO_8601_FORMAT.format(new Date());
                final int count = (int)snapshot.getChildrenCount();
                int number = count;
                firebase.child("Order").child("Count").setValue(count);
                String orderID = "";
                if (number < 10) {
                    orderID = "OR000" + number;
                }
                else if (number < 100) {
                    orderID = "OR00" + number;
                }
                else if (number < 1000) {
                    orderID = "OR0" + number;
                }
                else if (number < 10000) {
                    orderID = "OR" + number;
                }
                final String OrderID = orderID;
                firebase.child("Order").child(OrderID).child("OrderID").setValue(OrderID);
                firebase.child("Order").child(OrderID).child("Username").setValue(username);
                firebase.child("Order").child(OrderID).child("Order_Date").setValue(now);
                firebase.child("Order").child(OrderID).child("Request_Delivery").setValue(request_delivery_status);
                firebase.child("Order").child(OrderID).child("Delivery_Address").setValue(user_delivery_address);
                firebase.child("Order").child(OrderID).child("Status").setValue("Processing");

                for (int n = 0; n < lastFileString.size(); n++) {
                    final String filename = lastFileString.get(n);
                    final String filenamePath = fileUri.get(n).getLastPathSegment();
                    final String fileNumber = String.valueOf(n + 1);
                    final int itemID = n;
                    storage.child(username).child(filename).putFile(fileUri.get(n))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                                    firebase.child("Order").child(OrderID).child("Files").child("Count").setValue(fileNumber);
                                    firebase.child("Order").child(OrderID).child("Files").child(fileNumber).child("Filename").setValue(filename);
                                    firebase.child("Order").child(OrderID).child("Files").child(fileNumber).child("Copies").setValue(copies.get(itemID));
                                    //firebase.child("Order").child(OrderID).child("Files").child(fileNumber).child("Download_Url").setValue(downloadUrl);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadDetailsActivity.this, "Something Went Wrong. Try Again.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            storage.child(username).child(filename).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            firebase.child("Order").child(OrderID).child("Files").child(fileNumber).child("Download_Url").setValue(uri.toString());

                                        }
                                    });
                        }
                    }, 5000);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Toast.makeText(this, "File(s) is Uploading. PLEASE WAIT", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(UploadDetailsActivity.this)
                        .setTitle("Upload Information")
                        .setMessage("Your file(s) is uploaded.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //UploadDetailsActivity.this.finish();
                                Intent intent = new Intent(UploadDetailsActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }, 10000);

    }

    public class FileAdapter extends BaseAdapter {
        private ArrayList<String> filename;
        private ArrayList<String> spinnerItems;
        private Context context;

        public FileAdapter(ArrayList<String> filename, ArrayList<String> spinnerItems, Context context) {
            this.filename = filename;
            this.spinnerItems = spinnerItems;
            this.context = context;
        }

        @Override
        public int getCount() {
            return filename.size();
        }

        @Override
        public Object getItem(int position) {
            return filename.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.file_adapter, null);
            }

            TextView textView = (TextView) view.findViewById(R.id.row_item_textview);
            Spinner spinner = (Spinner) view.findViewById(R.id.row_item_spinner);

            textView.setText(filename.get(position));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerItems);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(UploadDetailsActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    if (!parent.getSelectedItem().toString().equals("0")) {
                        copies.add(parent.getSelectedItem().toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return view;
        }
    }
}