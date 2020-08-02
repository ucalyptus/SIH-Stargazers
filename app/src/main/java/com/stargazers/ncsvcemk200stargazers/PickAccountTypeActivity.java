package com.stargazers.ncsvcemk200stargazers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class PickAccountTypeActivity extends AppCompatActivity {

    private CardView officer, benificiary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_account_type);

        officer = findViewById(R.id.Officer);
        benificiary = findViewById(R.id.Benificary);

        IntroPref introPref = new IntroPref(this);

        PushDownAnim.setPushDownAnimTo(officer)
                .setScale(PushDownAnim.MODE_STATIC_DP, 6)
                .setOnClickListener(v -> {
                    introPref.setAccountType(0);

                    Intent intent = new Intent(PickAccountTypeActivity.this, LoginActivity.class);
                    startActivity(intent);
                });

        PushDownAnim.setPushDownAnimTo(benificiary)
                .setScale(PushDownAnim.MODE_STATIC_DP, 6)
                .setOnClickListener(v -> {
                    introPref.setAccountType(1);
                    Intent intent = new Intent(PickAccountTypeActivity.this, LoginActivity.class);
                    startActivity(intent);
                });



//
//        benificiary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }
}