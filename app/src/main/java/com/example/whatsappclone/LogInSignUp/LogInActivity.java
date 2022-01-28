package com.example.whatsappclone.LogInSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class LogInActivity extends AppCompatActivity {
    TextView needanewAccount,forgotpassword;
    Button phonenumberlogIn,LogIn;
    EditText email,password;
    DatabaseReference UserRef;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Intialize();
        progressDialog=new ProgressDialog(this);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        needanewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sendusertoregactivity();
            }
        });
        phonenumberlogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Sendusertophoneactivity();
            }
        });
        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserLogIn();
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLinktoMail();
            }
        });
    }

    private void sendLinktoMail() {
        if (email.getText().toString().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$") && email.getText().toString().length()>8)
        {
            AlertDialog.Builder passwordreset=new AlertDialog.Builder(this);
            passwordreset.setTitle("Reset Password ?");
            passwordreset.setMessage("Press Yes to receive the reset link");
            passwordreset.setPositiveButton("YES",(dialogInterface, i) ->
            {
                String resetEmail=email.getText().toString();
                auth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Reset Email Link has been send to your emailId", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            passwordreset.setNegativeButton("NO",(dialogInterface, i) -> {});
            passwordreset.create().show();
        }
        else
        {
            email.setError("please Enter a valid Email");
        }


    }

    private void AllowUserLogIn() {
        String userEmail=email.getText().toString();
        String userPass=password.getText().toString();
        if(TextUtils.isEmpty(userEmail))
        {
            email.setError("please enter email id");

        }
        if(TextUtils.isEmpty(userPass))
        {
           password.setError("please enter password");

        }
        if(TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPass))
        {
            email.setError("please enter email id");
           password.setError("please enter password");

        }
        if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPass)) {
            progressDialog.setTitle("Signing In");
            progressDialog.setMessage("Please wait , while we are logging into your account ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            auth.signInWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                       String deviceToken= FirebaseInstanceId.getInstance().getToken();
                        String currentUserId=auth.getCurrentUser().getUid();
                        UserRef.child(currentUserId).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SendUserToMainActivity();
                                Toast.makeText(getApplicationContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                    else
                    {
                        String message=task.getException().getLocalizedMessage();
                        Toast.makeText(getApplicationContext(), "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void Sendusertoregactivity() {

        Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

        private void Sendusertophoneactivity() {

            Intent intent =new Intent(LogInActivity.this, PhnoLoginActivity.class);
            startActivity(intent);

        }

    private void SendUserToMainActivity() {
        Intent intent =new Intent(LogInActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Intialize() {
        needanewAccount=findViewById(R.id.needanewAccount);
       phonenumberlogIn=findViewById(R.id.phone_number_login);
       email=findViewById(R.id.login_email);
       password=findViewById(R.id.login_password);
       LogIn=findViewById(R.id.login_btn);
       forgotpassword=findViewById(R.id.forgot_password);
       auth=FirebaseAuth.getInstance();

    }
}