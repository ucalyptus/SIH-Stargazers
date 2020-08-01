package com.stargazers.ncsvcemk200stargazers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebView extends AppCompatActivity {

    private android.webkit.WebView wv;
    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar tb = findViewById(R.id.toolb);
        tb.setTitle("SnapLingo");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);


        progressBar=findViewById(R.id.progressBar1);

        wv = findViewById(R.id.webview) ;
        wv.setVisibility(View.INVISIBLE);
        wv.setWebChromeClient(new WebChromeClient());

        WebSettings wset = wv.getSettings();
        wset.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient(){


            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(WebView.this,"Loading...",Toast.LENGTH_SHORT).show();

            }

            public void onPageFinished(android.webkit.WebView view, String url) {

                super.onPageFinished(view, url);
                wv.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(webView.this,"Paste Text...",Toast.LENGTH_SHORT).show();

            }
        });
        Intent i = getIntent();
        final String text = i.getStringExtra("text");
//        wv.loadUrl("https://translate.google.com/#auto/hi/"+text);

//bool=1 for Contact us, 2 for Privacy Policy and 3 for Translate
        if(getIntent().getStringExtra("bool")!=null && getIntent().getStringExtra("bool").matches("1")) {
            wv.loadUrl(text);
            tb.setTitle("applex.in");
        }
//        if(getIntent().getStringExtra("bool")!=null && getIntent().getStringExtra("bool").matches("2")) {
//            wv.loadUrl(text);
//            tb.setTitle("Privacy");
//        }
        else if(getIntent().getStringExtra("bool")!=null && getIntent().getStringExtra("bool").matches("3")){
            tb.setTitle("Translate");
            wv.loadUrl("https://translate.google.com/#auto/hi/" + text);
        }
    }

    public void onBackPressed() {
        if(wv.canGoBack()){
            wv.goBack();
        }
        else {
            AlertDialog.Builder builder= new AlertDialog.Builder(WebView.this);
            builder.setTitle("Return to home")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            WebView.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Cancel",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_web, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


}


