package com.stargazers.ncsvcemk200stargazers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stargazers.ncsvcemk200stargazers.models.UserModel;

//import com.applex.snaplingo.preferences.IntroPref;

public class Splash extends AppCompatActivity {

    private static final long Splash_time_out = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        IntroPref introPref = new IntroPref(this);

//        ImageView imageView = findViewById(R.id.img);
//        Animation animation_up = AnimationUtils.loadAnimation(Splash.this,R.anim.rotate);
//        Animation animation_down = AnimationUtils.loadAnimation(splash.this,R.anim.fab_slide_in_from_right);

//        imageView.startAnimation(animation_up);

        if (introPref.isFirstTimeLaunch()) {
            new Handler().postDelayed(() -> {
                introPref.setIsFirstTimeLaunch(false);
                startActivity(new Intent(Splash.this, Walkthrough.class));
                finish();
            },Splash_time_out);

        }
        else {
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                new Handler().postDelayed(() -> {
                    FirebaseFirestore.getInstance().document("Users/"+FirebaseAuth.getInstance().getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            UserModel userModel = new UserModel();
                            userModel = task.getResult().toObject(UserModel.class);
                            if(userModel.getAccountType() == 1){
                                introPref.setAccountType(1);
                            }
                            else {
                                introPref.setAccountType(0);
                            }
                        }
                    });
                    Intent homeIntent = new Intent(Splash.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }, Splash_time_out);
            }
            else {
                new Handler().postDelayed(() -> {
                    Intent homeIntent = new Intent(Splash.this, PickAccountTypeActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }, Splash_time_out);
            }

        }
    }
}
