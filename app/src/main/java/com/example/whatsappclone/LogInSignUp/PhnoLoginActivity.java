package com.example.whatsappclone.LogInSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class PhnoLoginActivity extends AppCompatActivity {
    EditText verfication_text,phonenumber;
    Button sendVerficationCode,Verify;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callback;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phno_login);
        Intialize();
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        sendVerficationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PhoneNumber=phonenumber.getText().toString();
                if(TextUtils.isEmpty(PhoneNumber))
                {
                    phonenumber.setError("please enter phone number");

                }
                else
                {
                    progressDialog.setTitle("Phone Verification");
                    progressDialog.setMessage("please wait , while we are authentication your phone ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthOptions options= PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber("+91"+ PhoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(PhnoLoginActivity.this)
                            .setCallbacks(callback)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                    auth.setLanguageCode("en");
                }
            }
        });
        callback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressDialog.cancel();
                verfication_text.setText(phoneAuthCredential.getSmsCode());
               signInWithPhoneCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                  progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Invalid Phone Number , please enter correct phone number", Toast.LENGTH_SHORT).show();
                Verify.setVisibility(View.INVISIBLE);
                verfication_text.setVisibility(View.INVISIBLE);
                sendVerficationCode.setVisibility(View.VISIBLE);
                phonenumber.setVisibility(View.VISIBLE);
                //exceptional handling
                if(e instanceof FirebaseAuthInvalidCredentialsException)
                { Toast.makeText(getApplicationContext(), "Invalid Request : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                }
                if(e instanceof FirebaseTooManyRequestsException)
                {
                    Toast.makeText(getApplicationContext(), " Your sms limit has been expired ", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken Token) {
                progressDialog.cancel();
                verificationId=s;
                token=Token;
                Toast.makeText(getApplicationContext(), "Code Send", Toast.LENGTH_SHORT).show();
                Verify.setVisibility(View.VISIBLE);
                verfication_text.setVisibility(View.VISIBLE);
                sendVerficationCode.setVisibility(View.INVISIBLE);
                phonenumber.setVisibility(View.INVISIBLE);
            }

        };
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonenumber.setVisibility(View.INVISIBLE);
                sendVerficationCode.setVisibility(View.INVISIBLE);
                String code=verfication_text.getText().toString();
                if(TextUtils.isEmpty(code))
                {
                    verfication_text.setError("please enter verification code");
                }
                else
                {
                    progressDialog.setTitle("Verification Code");
                    progressDialog.setMessage("Please wait , while we are verifying you code ....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
                    signInWithPhoneCredentials(credential);
                }
            }
        });
    }

    private void Intialize() {
        verfication_text=findViewById(R.id.phone_number_verify_code);
        sendVerficationCode=findViewById(R.id.send_verify_code_btn);
       Verify=findViewById(R.id.verify_btn);
        phonenumber=findViewById(R.id.phone_number_edit);
        Verify.setVisibility(View.INVISIBLE);
        verfication_text.setVisibility(View.INVISIBLE);
        sendVerficationCode.setVisibility(View.VISIBLE);
        phonenumber.setVisibility(View.VISIBLE);


    }
    public void signInWithPhoneCredentials(PhoneAuthCredential phoneAuthCredential)
    {
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    String currentUserId=auth.getCurrentUser().getUid();
                    DatabaseReference UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
                    UserRef.child(currentUserId).child("device_token").setValue(deviceToken);
                    Toast.makeText(getApplicationContext(), "you are successfully logged In", Toast.LENGTH_SHORT).show();
                    SendUserToMainActivity();
                }
                else
                {
                    String message =task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error : " +  message, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                progressDialog.cancel();

            }
        });
    }
    private void SendUserToMainActivity() {
        Intent intent =new Intent(PhnoLoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}