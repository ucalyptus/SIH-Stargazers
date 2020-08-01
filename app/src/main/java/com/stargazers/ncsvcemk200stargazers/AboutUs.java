package com.stargazers.ncsvcemk200stargazers;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class AboutUs extends AppCompatActivity {
//    TextView tvad ;
//    TextView tvsb ;
//    TextView tvss ;
//    TextView tvsm ;
//    TextView tvas ;
//    TextView tvam ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar tb = findViewById(R.id.toolb);
        tb.setTitle("About Us");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);


//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        mAdView = findViewById(R.id.ad_view);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);


//        tvad = (TextView)findViewById(R.id.textViewad);
//        tvsb = (TextView)findViewById(R.id.textViewsb);
//        tvss = (TextView)findViewById(R.id.textViewss);
//        tvsm = (TextView)findViewById(R.id.textViewsm);
//        tvas = (TextView)findViewById(R.id.textViewas);
//        tvam = (TextView)findViewById(R.id.textViewam);
//
//        tvad.setMovementMethod(new ScrollingMovementMethod());
//        tvsb.setMovementMethod(new ScrollingMovementMethod());
//        tvss.setMovementMethod(new ScrollingMovementMethod());
//        tvsm.setMovementMethod(new ScrollingMovementMethod());
//        tvas.setMovementMethod(new ScrollingMovementMethod());
//        tvam.setMovementMethod(new ScrollingMovementMethod());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
                super.onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
}
