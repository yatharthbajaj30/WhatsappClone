package com.example.whatsappclone.Fragments;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.ChatActivity;
import com.example.whatsappclone.FindFriends;
import com.example.whatsappclone.R;
import com.example.whatsappclone.helper.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

   View chatview;
    String CurrentUserID;
    FirebaseAuth auth;
    String time , date ,CurrentDate;
   RecyclerView recyclerView;
    DatabaseReference ContactRef,UserRef;
   FloatingActionButton floatingActionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        chatview =inflater.inflate(R.layout.fragment_chat, container, false);
        auth= FirebaseAuth.getInstance();
        CurrentUserID=auth.getCurrentUser().getUid();
        ContactRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserID);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView=chatview.findViewById(R.id.chat_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        floatingActionButton=chatview.findViewById(R.id.chat_float_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendtoFindFriendsActivity();
            }
        });

        return chatview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContactRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,ChatViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Contacts model) {
                 String userid=getRef(position).getKey();
                final String[] image = {""};
                final String[] name = {""};
                UserRef.child(userid).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if (snapshot.hasChild("image"))
                         {
                             image[0] =snapshot.child("image").getValue().toString();
                             GetImage(image[0], holder.profileImage );

                         }
                         name[0] =snapshot.child("name").getValue().toString();
                         holder.lastSeen.setText("Date"+"Time");
                         holder.userName.setText(name[0]);
                         holder.userStatus.setText(snapshot.child("status").getValue().toString());
                         holder.lastSeen.setVisibility(View.VISIBLE);
                         if (snapshot.child("userState").hasChild("state"))
                         {
                            String state=snapshot.child("userState").child("state").getValue().toString();
                            date=snapshot.child("userState").child("date").getValue().toString();
                             time=snapshot.child("userState").child("time").getValue().toString();
                             if (state.equals("online"))
                             {
                                 holder.lastSeen.setVisibility(View.INVISIBLE);
                                 holder.online.setVisibility(View.VISIBLE);

                             }
                            else if (state.equals("offline"))
                             {
                                 holder.lastSeen.setVisibility(View.VISIBLE);
                                 holder.online.setVisibility(View.INVISIBLE);
                                 Calendar calendar=Calendar.getInstance();
                                 SimpleDateFormat dateFormat=new SimpleDateFormat("MMM dd,yyyy");
                                 CurrentDate=dateFormat.format(calendar.getTime());
                                 if (CurrentDate.equals(date))
                                 {
                                     holder.lastSeen.setText(time.toLowerCase(Locale.ROOT));

                                 }
                                 else
                                 {
                                     holder.lastSeen.setText(date);
                                 }

                             }
                         }
                         holder.itemView.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 Intent chat=new Intent(getActivity(), ChatActivity.class);
                                 chat.putExtra("uid",userid);
                                 chat.putExtra("name",name[0]);
                                 chat.putExtra("image",image[0]);
                                 startActivity(chat);
                             }
                         });
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
                ChatViewHolder viewHolder=new ChatViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus,lastSeen;
        CircleImageView profileImage;
        ImageView online;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            userStatus.setVisibility(View.VISIBLE);
            lastSeen=itemView.findViewById(R.id.user_profile_lastseen);
            lastSeen.setVisibility(View.VISIBLE);
            online=itemView.findViewById(R.id.online_icon);
        }
    }
    private void SendtoFindFriendsActivity() {
        Intent intent=new Intent(getActivity(), FindFriends.class);
        startActivity(intent);
    }
    private void GetImage(String currentUser, CircleImageView imageView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                child("Profile Images/" + currentUser + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).into(imageView);
            }
        });
    }
}