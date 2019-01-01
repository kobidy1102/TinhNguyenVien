package com.example.pc_asus.tinhnguyenvien;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

public class CheckConnectionService extends Service implements SensorEventListener {
    private DatabaseReference mDatabase, mDatabase2;
    private FirebaseUser mCurrentUser;
    public static  String keyRoomVideoChat;
    static int CHECK_DEVICE_LOGIN = 0511;
    Handler mhanler;
    MediaPlayer mediaPlayer;
     String uid;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("abc","start service...");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
         uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien").child("Status").child(uid);
        mDatabase.child("connectionRequest").addValueEventListener(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                keyRoomVideoChat=dataSnapshot.getValue().toString();
                if(keyRoomVideoChat.equalsIgnoreCase("0")==false && keyRoomVideoChat.equalsIgnoreCase("1")==false ){    // khac 0
                    Toast.makeText(CheckConnectionService.this, "-"+dataSnapshot.getValue().toString()+"-", Toast.LENGTH_SHORT).show();                    //tới đây rồi,, giờ ,,, khi có yêu cầu thì làm cái như báo thức hỏi chấp nhận hoặc từ chối

                   // if(keyRoomVideoChat.isEmpty()){ //#0 và #1 rồi
                        mDatabase.child("checkStatusDevice").setValue(1);
                //    }


                    Intent intent1 = new Intent(CheckConnectionService.this, HaveConnectionRequestActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ///So sánh id android hiện tại và
            mDatabase2 = FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien").child("Users").child(uid).child("idDevice");
            mDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String android_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    String TNV_id_Login = dataSnapshot.getValue().toString();
                    if (!TNV_id_Login.equals(android_id)) {
                        FirebaseAuth.getInstance().signOut();
                        //startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                        Intent dialogIntent = new Intent(CheckConnectionService.this, SignInActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(dialogIntent);
                        Log.e("abcde","ket qua "+ TNV_id_Login.equals(android_id));
                        Toast.makeText(CheckConnectionService.this,"Tài khoản được đăng nhập ở một thiết bị khác",Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Tình Nguyên Viên")
                .setContentText("Đang chờ kết nối")
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1112, notification);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){

        Log.e("enn","android 8+");
        String channelId = "Đang chờ kết nối.";
        String channelName = "Tình Nguyện Viên";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
}
