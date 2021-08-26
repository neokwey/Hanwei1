package com.example.demo_demo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    private DatabaseReference firebase;
    private String username, orderID;
    private Handler handler;
    private int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF_USERNAME", Context.MODE_PRIVATE);
                username = sharedPreferences.getString("SHARED_PREF_USERNAME", "");
                firebase = FirebaseDatabase.getInstance().getReference("Order");
                Query query = firebase.orderByChild("Username").equalTo(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                orderID = dataSnapshot.getKey();
                                firebase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String status = snapshot.child("Status").getValue().toString();
                                        String request_delivery = snapshot.child("Request_Delivery").getValue().toString();
                                        if (count == 0) {
                                            if (status.equals("Printed")) {
                                                String message = "";
                                                if (request_delivery.equals("Yes")) {
                                                    message = "Your file(s) is printed. Please go to pick up when the delivery is arrived.";
                                                }
                                                else if (request_delivery.equals("No")) {
                                                    message = "Your file(s) is printed. Please come here to pick up.";
                                                }
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "001")
                                                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                                                        .setContentTitle("New Notification")
                                                        .setContentText(message)
                                                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                                        .setAutoCancel(true);

                                                Intent intent = new Intent(NotificationService.this, MainActivity.class);
                                                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                builder.setContentIntent(pendingIntent);
                                                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                                {
                                                    String channelId = "001";
                                                    NotificationChannel channel = new NotificationChannel(
                                                            channelId,
                                                            "Channel human readable title",
                                                            NotificationManager.IMPORTANCE_HIGH);
                                                    notificationManager.createNotificationChannel(channel);
                                                    builder.setChannelId(channelId);
                                                }
                                                notificationManager.notify(0, builder.build());
                                                count++;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 5000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
