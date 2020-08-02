package com.stargazers.ncsvcemk200stargazers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    public static String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

    }

    @Override
    public void handleResult(final Result result) {

        code = result.getText();
//        Toast.makeText(getApplicationContext(), code, Toast.LENGTH_LONG).show();
//        ScannerActivity.super.onBackPressed();

        Intent intent = new Intent(ScannerActivity.this, NewBeneficiary1.class);
        intent.putExtra("result", code);
        startActivity(intent);
        finish();

    }


    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

}
