package com.example.demo_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    DatabaseReference firebase;
    StorageReference storage;
    TextView result;
    Button btn_select, btn_proceed;
    ArrayList<Uri> fileUri;
    ProgressDialog progressDialog;
    ArrayList<String> fileString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        getSupportActionBar().setTitle("Upload");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileString = new ArrayList<>();

        fileUri = new ArrayList<Uri>();
        firebase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        result = findViewById(R.id.textView_file);
        btn_select = findViewById(R.id.upload_btn_select_file);
        btn_proceed = findViewById(R.id.upload_btn_proceed);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fileUri.isEmpty()) {
                    Intent intent = new Intent(UploadActivity.this, UploadDetailsActivity.class);
                    intent.putExtra("fileString", fileString);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(UploadActivity.this, "No File is Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }
                else {
                    ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        }
        else {
            Toast.makeText(this, "Permission is Denied.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("image/jpg | image/jpeg | image/png | application/msword | application/pdf | application/vnd.ms-powerpoint | application/vnd.ms-excel");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            //fileUri = data.getData();
//            if (data.getClipData() != null) {
//                for (int count = 0; count < data.getClipData().getItemCount(); count++) {
//                    fileUri.add(data.getClipData().getItemAt(count).getUri());
//                    fileString.add(data.getClipData().getItemAt(count).getUri().toString());
//                }
            if (data.getData() != null) {
                fileUri.add(data.getData());
                fileString.add(data.getData().toString());

                result.setText("File(s) Selected: ");
                for (int count = 0; count < fileUri.size(); count++) {
                    String fileType = getContentResolver().getType(fileUri.get(count));
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
                    result.append("\n" + fileUri.get(count).getLastPathSegment() + fileType);
                    //list.add(new FileAdapter(fileUri.get(count).getLastPathSegment() + fileType, 1, R.drawable.ic_baseline_cancel_24));
                }
            }
        } else {
            Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show();
        }
    }
}
