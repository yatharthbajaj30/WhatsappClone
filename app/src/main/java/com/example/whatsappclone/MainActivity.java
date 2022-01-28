package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.whatsappclone.LogInSignUp.LogInActivity;
import com.example.whatsappclone.LogInSignUp.RegisterActivity;
import com.example.whatsappclone.helper.TabAccesorAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;



public class MainActivity extends AppCompatActivity {
   Toolbar toolbar;
    ViewPager mainViewPager;
    TabLayout tabLayout;
    TabAccesorAdapter tabAccesorAdapter;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser!=null) {
            setContentView(R.layout.activity_main);
            RootRef = FirebaseDatabase.getInstance().getReference();
            toolbar = findViewById(R.id.main_activity_toolbar);
            mainViewPager = findViewById(R.id.main_tab_viewPager);
            tabLayout = findViewById(R.id.main_tabs);
            tabAccesorAdapter = new TabAccesorAdapter(getSupportFragmentManager());
            mainViewPager.setAdapter(tabAccesorAdapter);
            setSupportActionBar(toolbar);
            tabLayout.setupWithViewPager(mainViewPager);
            getSupportActionBar().setTitle("Whatsapp");
        }
        else
        {
            auth.signOut();
            SendUserToLogInActivity();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser==null)
        {
            auth.signOut();
            SendUserToLogInActivity();

        }
        else
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Loading Chats");
            progressDialog.setMessage("Please wait while we are loading your chats");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            updateUserStatusStartActivity("online");
            VerifyExsistenceUser();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser!=null)
        {
            updateUserStatusActivity("offline");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser!=null)
        {
            updateUserStatusActivity("online");
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main=new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_DEFAULT);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser!=null)
        {
            updateUserStatusActivity("offline");
        }
    }

    private void updateUserStatusActivity(String status) {
        String currentUserId= auth.getCurrentUser().getUid();
        String currentTime,currentDate;
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("MMM dd,yyyy");
        currentDate=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm a");
        currentTime=timeFormat.format(calendar.getTime());
        HashMap<String ,Object> userStateMapp = new HashMap<>();
        userStateMapp.put("time",currentTime);
        userStateMapp.put("date",currentDate);
        userStateMapp.put("state",status);
        RootRef.child("Users").child(currentUserId).child("userState").updateChildren(userStateMapp);
    }
    private void updateUserStatusStartActivity(String status) {
        String currentUserId= auth.getCurrentUser().getUid();
        String currentTime,currentDate;
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("MMM dd,yyyy");
        currentDate=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm a");
        currentTime=timeFormat.format(calendar.getTime());
        HashMap<String ,Object> userStateMapp = new HashMap<>();
        userStateMapp.put("time",currentTime);
        userStateMapp.put("date",currentDate);
        userStateMapp.put("state",status);
        RootRef.child("Users").child(currentUserId).child("userState").updateChildren(userStateMapp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.cancel();
            }
        });
    }
    private void VerifyExsistenceUser() {
        String currentUserId= auth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("name")).exists())
                {
                    SendUserToSettingsActivity();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToLogInActivity() {
        Intent intent =new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
    }
    private void SendUserToSettingsActivity() {
        Intent intent =new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      getMenuInflater().inflate(R.menu.options_menu,menu);
      return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       super.onOptionsItemSelected(item);
       if (item.getItemId()==R.id.logout)
       {
           FirebaseUser currentUser=auth.getCurrentUser();
           if (currentUser!=null)
           {
               updateUserStatusActivity("offline");
           }
           auth.signOut();
           SendUserToLogInActivity();
       }
        if (item.getItemId()==R.id.main_settings)
        {

           SendUserToSettingsActivity();
        }
       return true;
    }
}