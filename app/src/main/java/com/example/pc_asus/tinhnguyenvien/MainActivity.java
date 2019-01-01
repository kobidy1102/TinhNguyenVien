package com.example.pc_asus.tinhnguyenvien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    Handler mHandler;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView= navigationView.getHeaderView(0);
        final ImageView img= (ImageView) headView.findViewById(R.id.img_connect_avatar);
        final TextView tv_name=(TextView) headView.findViewById(R.id.tv_bar_name);
        final String[] photoURL = new String[1];

//        sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
//        final int[] status = {sharedPreferences.getInt("status", 1)};
        final View imgStatusWithFriends =  findViewById(R.id.img_statusWithFriends);
        final View imgStatusWithAll =  findViewById(R.id.img_statusWithAll);






        final int[] status = new int[2];
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser == null) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        } else {

            uid = mCurrentUser.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien");

                final ProgressDialog dialog;
                dialog = new ProgressDialog(this);
                dialog.setMessage("        Loading...");
                dialog.show();

                mDatabase.child("Status").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String s0 = dataSnapshot.child("statusWithFriends").getValue().toString();
                        status[0] = Integer.parseInt(s0);
                        String s1 = dataSnapshot.child("statusWithAll").getValue().toString();
                        status[1] = Integer.parseInt(s1);

                        if (status[0] == 1) {
                            imgStatusWithFriends.setBackgroundResource(R.mipmap.tick);
                        } else imgStatusWithFriends.setBackgroundResource(R.mipmap.stop);

                        if (status[1] == 1) {
                            imgStatusWithAll.setBackgroundResource(R.mipmap.tick);
                        } else imgStatusWithAll.setBackgroundResource(R.mipmap.stop);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        imgStatusWithFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status[0] ==1) {
                    imgStatusWithFriends.setBackgroundResource(R.mipmap.stop);
                    status[0] =0;
                }else {
                    imgStatusWithFriends.setBackgroundResource(R.mipmap.tick);
                    status[0] =1;

                }
                mDatabase.child("Status").child(uid).child("statusWithFriends").setValue(status[0]);
            }
        });


        imgStatusWithAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status[1] ==1) {
                    imgStatusWithAll.setBackgroundResource(R.mipmap.stop);
                    status[1] =0;
                }else {
                    imgStatusWithAll.setBackgroundResource(R.mipmap.tick);
                    status[1] =1;

                }
                mDatabase.child("Status").child(uid).child("statusWithAll").setValue(status[1]);
            }
        });


        mDatabase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_name.setText(dataSnapshot.child("name").getValue().toString());
                photoURL[0] =dataSnapshot.child("photoURL").getValue().toString();
                // Picasso.with(VideoChatActivity.this).load(dataSnapshot.child("photoURL").getValue().toString()).into(img);
                RequestOptions  requestOptions = new RequestOptions();
                requestOptions.fitCenter();
                requestOptions.placeholder(R.mipmap.user);
                Glide.with(getApplicationContext())
                        .load(photoURL[0])
                        .apply(requestOptions)
                        //   .override(200,150)
                        .into(img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Intent intent= new Intent(MainActivity.this, CheckConnectionService.class);
        startService(intent);
        Log.e("connect"," service...");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_accountSetting) {
            startActivity(new Intent(MainActivity.this,AccountSettingsActivity.class));
         //    Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_friends) {
            Toast.makeText(this, "friends", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,SignInActivity.class));

            mDatabase.child("Status").child(uid).child("connectionRequest").setValue(0);
            mDatabase.child("Status").child(uid).child("statusWithAll").setValue(0);
            mDatabase.child("Status").child(uid).child("statusWithFriends").setValue(0);


            finish();
            //  Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.child("Status").child(uid).child("checkStatusDevice").setValue(0);
    }


}
