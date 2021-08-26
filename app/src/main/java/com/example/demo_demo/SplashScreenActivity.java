package com.example.demo_demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        if (isInternetAvailable()) {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
                    if (sharedPreferences.getString("SHARED_PREF_USERNAME", "").length() == 0){
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        SplashScreenActivity.this.startActivity(intent);
                        SplashScreenActivity.this.finish();
                    }
                    else {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        SplashScreenActivity.this.startActivity(intent);
                        SplashScreenActivity.this.finish();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
        else {
            new AlertDialog.Builder(SplashScreenActivity.this)
                    .setTitle("No Internet Connection")
                    .setMessage("Please confirm your device is connect with internet.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SplashScreenActivity.this.finish();
                        }
                    })
                    .show();
        }

    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}