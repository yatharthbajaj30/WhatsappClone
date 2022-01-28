package com.example.whatsappclone.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Fragments.RequestFragment;
import com.example.whatsappclone.ImageViewActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<Messages> userMessageLIST;
    FirebaseAuth auth;
    DatabaseReference userRef;
    Context context;
    public MessageAdapter(List<Messages> userMessageLIST, Context context)
    {
        this.userMessageLIST=userMessageLIST;
        this.context=context;
    }
    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        auth=FirebaseAuth.getInstance();
        return  new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String messageSenderId=auth.getCurrentUser().getUid();
        Messages messages=userMessageLIST.get(position);
        String fromUserId=messages.getFrom();
        String fromMessageType=messages.getType();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderMessageTexttd.setVisibility(View.GONE);
        holder.sender.setVisibility(View.GONE);
        holder.senderImageView.setVisibility(View.GONE);
        holder.receiverImageView.setVisibility(View.GONE);
        holder.receiverMessageTexttd.setVisibility(View.GONE);
        holder.receiver.setVisibility(View.GONE);
        holder.receiverMessageText.setVisibility(View.GONE);

        if (fromMessageType.equals("text"))
        {
            if (fromUserId.equals(messageSenderId)) {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageTexttd.setVisibility(View.VISIBLE);
                holder.sender.setVisibility(View.VISIBLE);
                holder.senderMessageText.setText(messages.getMessage());
                holder.senderMessageTexttd.setText(messages.getTime() + " " + messages.getDate());
            }
            else
            {
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageTexttd.setVisibility(View.VISIBLE);
                holder.receiver.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getMessage());
                holder.receiverMessageTexttd.setText(messages.getTime() + " " + messages.getDate());
            }
            holder.senderImageView.setVisibility(View.GONE);
            holder. receiverImageView.setVisibility(View.GONE);
        }
        if (fromMessageType.equals("image"))
        {
            if (fromUserId.equals(messageSenderId)) {
                holder.senderImageView.setVisibility(View.VISIBLE);
                holder.receiverImageView.setVisibility(View.GONE);
                GetImage(messages.getMessageID(),holder.senderImageView);
            }
            else
            {
                holder.receiverImageView.setVisibility(View.VISIBLE);
                holder.senderImageView.setVisibility(View.GONE);
                GetImage(messages.getMessageID(),holder.receiverImageView);
            }
        }
        if (fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {
            if (fromUserId.equals(messageSenderId)) {
                holder.senderImageView.setVisibility(View.VISIBLE);
                holder.senderImageView.setBackgroundResource(R.drawable.ic_baseline_insert_drive_file_24);
                holder.receiverImageView.setVisibility(View.GONE);

            }
            else
            {
                holder.receiverImageView.setVisibility(View.VISIBLE);
                holder.receiverImageView.setBackgroundResource(R.drawable.ic_baseline_insert_drive_file_24);
                holder.senderImageView.setVisibility(View.GONE);

            }
        }
        if (fromUserId.equals(messageSenderId))
            
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messages.getType().equals("pdf") ||messages.getType().equals("docx") )
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete for Me",
                                        "Download and View this Document",
                                        "Cancel",
                                        "Delete for EveryOne"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    deleteSendMessageForeMe(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (i==1)
                                {
                                    openFile(messages.getMessageID(),messages.getType(),holder.itemView.getContext());
                                }
                                if (i==3)
                                {
                                    deleteMessageForeEveryOne(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    if (messages.getType().equals("text") )
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete for Me",
                                        "Cancel",
                                        "Delete for EveryOne"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    deleteSendMessageForeMe(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (i==2)
                                {
                                    deleteMessageForeEveryOne(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    if (messages.getType().equals("image") )
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete for Me",
                                        "View This Image",
                                        "Cancel",
                                        "Delete for EveryOne"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    deleteSendMessageForeMe(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (i==1)
                                {
                                    Intent intent=new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                                    intent.putExtra("messageId",messages.getMessageID());
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (i==3)
                                {
                                    deleteMessageForeEveryOne(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
        else
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messages.getType().equals("pdf") ||messages.getType().equals("docx") )
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete for Me",
                                        "Download and View this Document",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    deleteReceiverMessageForeMe(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (i==1)
                                {
                                    openFile(messages.getMessageID(),messages.getType(),holder.itemView.getContext());
                                }
                            }
                        });
                        builder.show();
                    }
                    if (messages.getType().equals("text") )
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete for Me",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    deleteReceiverMessageForeMe(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    if (messages.getType().equals("image") )
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete for Me",
                                        "View This Image",
                                        "Cancel",
                                        
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    deleteReceiverMessageForeMe(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (i==1)
                                {
                                    Intent intent=new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                                    intent.putExtra("messageId",messages.getMessageID());
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
        
    }
    private void deleteSendMessageForeMe(int position, MessageViewHolder holder) {
        DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageLIST.get(position).getFrom())
                .child(userMessageLIST.get(position).getTo())
                .child(userMessageLIST.get(position).getMessageID())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(holder.itemView.getContext(), "Message Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteReceiverMessageForeMe(int position, MessageViewHolder holder) {
        DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageLIST.get(position).getTo())
                .child(userMessageLIST.get(position).getFrom())
                .child(userMessageLIST.get(position).getMessageID())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(holder.itemView.getContext(), "Message Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteMessageForeEveryOne(int position, MessageViewHolder holder) {
        DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageLIST.get(position).getTo())
                .child(userMessageLIST.get(position).getFrom())
                .child(userMessageLIST.get(position).getMessageID())
                .removeValue();
        RootRef.child("Messages")
                .child(userMessageLIST.get(position).getFrom())
                .child(userMessageLIST.get(position).getTo())
                .child(userMessageLIST.get(position).getMessageID())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(holder.itemView.getContext(), "Message Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    public void openFile(String currentUserID,String checker,Context context) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files/" + currentUserID + "."+checker);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                if(checker.equals("pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent in = Intent.createChooser(intent, "open file");
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in);
                }
                else
                {
                    String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    Intent in = Intent.createChooser(intent, "open file");
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in);
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return userMessageLIST.size();
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public TextView senderMessageTexttd, receiverMessageTexttd;
        public LinearLayout sender, receiver;
        public ImageView senderImageView, receiverImageView;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_messages);
            receiverMessageText =  itemView.findViewById(R.id.reciever_messages);
            senderMessageTexttd =  itemView.findViewById(R.id.sender_messages_td);
            receiverMessageTexttd =  itemView.findViewById(R.id.reciever_messages_td);
            sender =  itemView.findViewById(R.id.lls);
            receiver =  itemView.findViewById(R.id.llr);
            senderImageView=itemView.findViewById(R.id.messager_sender_image_view);
            receiverImageView=itemView.findViewById(R.id.receiver_sender_image_view);

        }
    }
    private void GetImage(String currentUser, ImageView imageView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                child("Image Files/" + currentUser + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(imageView);
            }
        });
    }
   
}
