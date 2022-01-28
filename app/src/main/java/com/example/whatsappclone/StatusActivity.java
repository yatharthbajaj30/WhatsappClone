package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusActivity extends AppCompatActivity {
    TextView username , userTimeUploaded;
    ImageView statusImage;
    CircleImageView circleImageView;
    String uid,user,time;
    ProgressBar statusProgress;
    int progress=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        user=getIntent().getExtras().get("username").toString();
        uid=getIntent().getExtras().get("uid").toString();
        time=getIntent().getExtras().get("time").toString();
        username=findViewById(R.id.user_status_name);
        statusProgress=findViewById(R.id.user_status_progress);
        userTimeUploaded=findViewById(R.id.user_time_show);
       circleImageView=findViewById(R.id.users_profile_image_status);
       statusImage=findViewById(R.id.status_image);
       username.setText(user);
       userTimeUploaded.setText("Time : "+time);
       GetImageProfile();
       GetImageStatus();
        setProgressBar(progress);

    }
    private void  GetImageStatus() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                child("Status/" + uid + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(statusImage);
            }
        });

    }
    private void setProgressBar(int progress)
    {
        statusProgress.setProgress(progress);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setProgressBar(progress+1);
            }
        });
        thread.start();
        if (progress>100)
        {
            finish();
        }
    }
    private void GetImageProfile() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                child("Profile Images/" + uid + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(circleImageView);
            }
        });
    }
}