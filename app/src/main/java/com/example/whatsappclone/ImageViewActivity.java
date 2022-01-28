package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageView;
    String msgId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView=findViewById(R.id.image_viewer);
        msgId=getIntent().getStringExtra("messageId");
        GetImage(msgId);

    }
    public void GetImage(String currentUser) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Image Files/" + currentUser + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(imageView);

            }
        });
    }
}