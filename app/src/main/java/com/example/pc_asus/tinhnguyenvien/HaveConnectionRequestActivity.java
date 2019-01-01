package com.example.pc_asus.tinhnguyenvien;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HaveConnectionRequestActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private ImageView imgAvatar, imgStartCall, imgEndCall;
    private TextView tvName;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    String uid;
    private int checkStartCall=0;
     String sttFriends,sttAll;
    // String getKey;
     String key="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_connection_request);

        Log.e("abc","create activity hỏi");
        //activity hiển thị lên trước màn hình khóa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mediaPlayer= MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.start();

        //rung 0-> vô thời hạn, rung 0,4s nghĩ 1s .
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 400, 1300};
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          //  v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        }else{
            //deprecated in API 26
            vibrator.vibrate(pattern, 0);
        }
        setTitle("                        Trợ Giúp");


        checkStartCall=1;

        imgAvatar= findViewById(R.id.img_connect_avatar);
        imgStartCall= findViewById(R.id.img_startCall);
        imgEndCall= findViewById(R.id.img_endCall);
        tvName= findViewById(R.id.tv_connect_name);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        key=CheckConnectionService.keyRoomVideoChat;
        Log.e("abc","key ......." + key);

        mDatabase.child("NguoiMu").child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name= dataSnapshot.child("name").getValue().toString();
                String avatarLink= dataSnapshot.child("photoURL").getValue().toString();

                tvName.setText(name);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.fitCenter();
                requestOptions.placeholder(R.mipmap.user);
                Glide.with(getApplicationContext())
                        .load(avatarLink)
                        .apply(requestOptions)
                        .into(imgAvatar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imgEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStartCall=3;

                mDatabase.child("TinhNguyenVien").child("Status").child(uid).child("connectionRequest").setValue(1);
                mediaPlayer.stop();
                vibrator.cancel();
                finish();
            }
        });


        imgStartCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkStartCall=2;
              //  Intent i = new Intent(HaveConnectionRequestActivity.this,VideoCallActivity.class);
                Intent i = new Intent(HaveConnectionRequestActivity.this,VideoCallAndMapActivity.class);
                i.putExtra("key",key);
                Log.e("abc",key + " getkey 2");
                startActivity(i);

                mediaPlayer.stop();
                vibrator.cancel();
               finish();
            }
        });


        //15s mà ko nhấc máy thì hủy

        Log.e("abc","checkstartcall-1 "+checkStartCall);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("abc","checkstartcall-2 "+checkStartCall);

                if(checkStartCall==1){
                    Toast.makeText(HaveConnectionRequestActivity.this, "15s", Toast.LENGTH_SHORT).show();
                    Log.e("abc","20s set bận");


                    mDatabase.child("TinhNguyenVien").child("Status").child(uid).child("connectionRequest").setValue(1);
                    mediaPlayer.stop();
                    vibrator.cancel();
                    finish();
                }
                }
        }, 15000);


        checkDisConnect();


    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.child("TinhNguyenVien").child("Status").child(uid).child("checkStatusDevice").setValue(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
    }


    void checkDisConnect(){
        //new add
        final DatabaseReference mDatabase;
        FirebaseUser mCurrentUser;
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien").child("Status").child(uid);
        mDatabase.child("connectionRequest").addValueEventListener(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String keyRoomVideoChat=dataSnapshot.getValue().toString();
                if(keyRoomVideoChat.equalsIgnoreCase("0")==true){    // khac 0
                    Toast.makeText(HaveConnectionRequestActivity.this, "Đã ngắt kết nối", Toast.LENGTH_SHORT).show();
                    finish();
                    return;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
