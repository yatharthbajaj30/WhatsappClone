package com.example.whatsappclone.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.FindFriends;
import com.example.whatsappclone.R;
import com.example.whatsappclone.helper.Contacts;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;


public class RequestFragment extends Fragment {
   View reqView;
   RecyclerView recyclerView;
    DatabaseReference RootRef,ChatReqRef,ContactRef,UserRef;
    StorageReference storageReference;
    String CurrentUserID;
    FirebaseAuth auth;

    FloatingActionButton floatingActionButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        reqView= inflater.inflate(R.layout.fragment_request, container, false);
        auth=FirebaseAuth.getInstance();
        CurrentUserID=auth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        ChatReqRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView=reqView.findViewById(R.id.request_recyclerview);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        floatingActionButton=reqView.findViewById(R.id.request_float_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendtoFindFriendsActivity();
            }
        });
        return reqView;
    }
    private void SendtoFindFriendsActivity() {
        Intent intent=new Intent(getActivity(), FindFriends.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatReqRef.child(CurrentUserID),Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter
                =new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Contacts model) {
                holder.Accept.setVisibility(View.VISIBLE);
                holder.Cancel.setVisibility(View.VISIBLE);
                final String list_of_users=getRef(position).getKey();
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String type=snapshot.getValue().toString();
                            if (type.equals("received")) {
                                holder.reqlinearLayout.setVisibility(View.VISIBLE);
                                UserRef.child(list_of_users).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("image")) {
                                            GetImage(list_of_users, holder.profileImage);
                                        }
                                        String receiverName=snapshot.child("name").getValue().toString();
                                        holder.userName.setText(receiverName);
                                        holder.userStatus.setText("wants to connect with you");
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[]=new CharSequence[]
                                                        {
                                                                "Accept",
                                                                "Cancel"
                                                        };
                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                builder.setTitle(receiverName+" 's Chat Request");
                                                builder.setItems(options,(DialogInterface.OnClickListener) (dialogInterface,i)->{
                                                    if (i==0)
                                                    {
                                                        ContactRef.child(CurrentUserID).child(list_of_users)
                                                                .child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    ContactRef.child(list_of_users).child(CurrentUserID)
                                                                        .child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    ChatReqRef.child(CurrentUserID).child(list_of_users)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        ChatReqRef.child(list_of_users).child(CurrentUserID)
                                                                                                                .removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if (task.isSuccessful()) {
                                                                                                                            Toast.makeText(getContext(), "Contact Added Successfully", Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                                }


                                                            }
                                                        });
                                                        
                                                    }
                                                    if (i==1)
                                                    {
                                                        ChatReqRef.child(CurrentUserID).child(list_of_users)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            ChatReqRef.child(list_of_users).child(CurrentUserID)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            if (type.equals("sent"))
                            {
                                holder.Accept.setText("Req Sent");
                                holder.Cancel.setVisibility(View.INVISIBLE);
                                holder.reqlinearLayout.setVisibility(View.VISIBLE);
                                UserRef.child(list_of_users).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("image")) {
                                            GetImage(list_of_users, holder.profileImage);
                                        }
                                        String receiverName=snapshot.child("name").getValue().toString();
                                        holder.userName.setText(receiverName);
                                        holder.userStatus.setText("you have sent a request to "+receiverName);
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[]=new CharSequence[]
                                                        {
                                                                "Cancel Chat Request"
                                                        };
                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                builder.setTitle( "Cancel Chat Request");
                                                builder.setItems(options,(DialogInterface.OnClickListener) (dialogInterface,i)->{
                                                    if (i==0)
                                                    {
                                                        ChatReqRef.child(CurrentUserID).child(list_of_users)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            ChatReqRef.child(list_of_users).child(CurrentUserID)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Toast.makeText(getContext(), "You have Canceled Chat Request", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
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

            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
                RequestViewHolder viewHolder=new RequestViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class RequestViewHolder  extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView profileImage;
        Button Accept,Cancel;
        LinearLayout reqlinearLayout;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            Accept=itemView.findViewById(R.id.request_accept_btn);
            Cancel=itemView.findViewById(R.id.request_cancel_btn);
            reqlinearLayout=itemView.findViewById(R.id.set_requests);
            userStatus.setVisibility(View.VISIBLE);
            reqlinearLayout.setVisibility(View.INVISIBLE);
        }
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