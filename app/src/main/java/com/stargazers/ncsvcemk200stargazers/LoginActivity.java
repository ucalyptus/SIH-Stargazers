package com.stargazers.ncsvcemk200stargazers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stargazers.ncsvcemk200stargazers.models.UserModel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;

public class LoginActivity extends AppCompatActivity {

    private EditText mPhoneNo,mCode;
    Dialog myDialogue;
    String verificationId;

    IntroPref introPref;

    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        introPref = new IntroPref(LoginActivity.this);

        mCode = findViewById(R.id.code);
        mPhoneNo = findViewById(R.id.phone);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.super.onBackPressed();
            }
        });

        myDialogue = new Dialog(LoginActivity.this);

        myDialogue.setContentView(R.layout.dialog_otp_progress);
        myDialogue.setCanceledOnTouchOutside(FALSE);
        myDialogue.findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = myDialogue.findViewById(R.id.otp_dialog);
                if(et.getText().toString().length()==6){
                    verifyCode(et.getText().toString());
                }
                else
                    Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_SHORT).show();
            }
        });
        Objects.requireNonNull(myDialogue.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mCode.getText().toString();

                String number = mPhoneNo.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    mPhoneNo.setError("Valid number is required");
                    mPhoneNo.requestFocus();
                    return;
                }
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if(number.matches(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+91",""))){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("phone",mPhoneNo.getText().toString().trim());
                        startActivity(intent);
                    }
                    else {
                        FirebaseAuth.getInstance().signOut();
                        String phoneNumber = code + number;
                        myDialogue.show();
                        sendVerificationCode(phoneNumber);
                    }
                }
                else {
                    String phoneNumber = code + number;
                    myDialogue.show();
                    sendVerificationCode(phoneNumber);
                }

            }
        });
    }

    /////////////////SEND NO FOR VERIFICATION/////////////
    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                EditText et = myDialogue.findViewById(R.id.otp_dialog);
                et.setText(code);
//                if(et.getText().toString().length()==6){
//                    verifyCode(et.getText().toString());
//                }
//                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            myDialogue.dismiss();
        }
    };
    /////////////////SEND NO FOR VERIFICATION/////////////

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel userModel = new UserModel();
                            userModel.setAccountType(introPref.getAccountType());
                            userModel.setPhoneNo(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            FirebaseFirestore.getInstance().document("User/"+FirebaseAuth.getInstance().getUid())
                                    .set(userModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra("phone",mPhoneNo.getText().toString().trim());
                                                startActivity(intent);
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                                            }
                                            myDialogue.dismiss();

                                        }
                                    });


                        } else {
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            myDialogue.dismiss();
                        }
                    }

                });
    }

}