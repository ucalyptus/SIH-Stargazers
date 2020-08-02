package com.stargazers.ncsvcemk200stargazers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stargazers.ncsvcemk200stargazers.models.AadhaarModel;
import com.stargazers.ncsvcemk200stargazers.util.AadhaarForm;

import java.util.TreeMap;

public class NewBeneficiary1 extends AppCompatActivity {

    EditText acctNo,bankName, IFSC;
    ImageView document;
    TextView qrResult;

    Button scanQR, takePic, proceed;

    private static final int CAMERA_REQUEST_CODE = 200;
    String[] cameraPermission;
    String[] storagePermission;
    private static final int STORAGE_REQUEST_CODE = 400;

    private int sel;

    AadhaarForm aadhaarForm;
    private TreeMap<String, String> JValues;

    AadhaarModel aadhaarModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beneficiary);
//        Toolbar tb = findViewById(R.id.toolbar);
//        setSupportActionBar(tb);
//
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        cameraPermission = new String[]{Manifest.permission.CAMERA};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


//        acctNo = findViewById(R.id.acctNo);
//        bankName = findViewById(R.id.bankName);
//        IFSC = findViewById(R.id.IFSC);
//
//        qrResult = findViewById(R.id.aadhaarQR);
//        acctNo = findViewById(R.id.acctNo);

//        document = findViewById(R.id.aadhaar_image);

        scanQR = findViewById(R.id.scanQR);
        takePic = findViewById(R.id.take_aadhaar_image);
        proceed = findViewById(R.id.proceed);

        if(getIntent().getStringExtra("result") != null) {
            String result = getIntent().getStringExtra("result");
            aadhaarForm = new AadhaarForm(result);
            JValues = aadhaarForm.getJValues();

            aadhaarModel = new AadhaarModel();
            aadhaarModel.setUid(JValues.get("Aadhaar No"));
            aadhaarModel.setName(JValues.get("Name"));
            aadhaarModel.setGender(JValues.get("Gender"));
            aadhaarModel.setDob(JValues.get("DOB"));
            aadhaarModel.setCareof(JValues.get("Care Of"));
            aadhaarModel.setBuildingNo(JValues.get("Building No"));
            aadhaarModel.setStreet(JValues.get("Street"));
            aadhaarModel.setVtc(JValues.get("VTC"));
            aadhaarModel.setPo(JValues.get("Post Office"));
            aadhaarModel.setDistrict(JValues.get("District"));
            aadhaarModel.setSubDistrict(JValues.get("Sub District"));
            aadhaarModel.setState(JValues.get("State"));
            aadhaarModel.setPin(JValues.get("Pincode"));

            qrResult.setText(aadhaarModel.toString());
        }


        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel = 2;
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                }
                else {
                    Intent intent = new Intent(NewBeneficiary1.this, ScannerActivity.class);
                    startActivity(intent);
                }
            }
        });

        proceed.setClickable(false);


    }

    /////////////////////PERMISSIONS////////////////////

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted ){
                        Intent intent = new Intent(NewBeneficiary1.this, ScannerActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(this,"permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /////////////////////PERMISSIONS////////////////////]


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == android.R.id.home){
//            super.onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ScannerActivity.code!=null) {
            String result = ScannerActivity.code;
            if(result.contains("xml version")){
                aadhaarForm = new AadhaarForm(result);
                JValues = aadhaarForm.getJValues();

                aadhaarModel = new AadhaarModel();
                aadhaarModel.setUid(JValues.get("Aadhaar No"));
                aadhaarModel.setName(JValues.get("Name"));
                aadhaarModel.setGender(JValues.get("Gender"));
                aadhaarModel.setDob(JValues.get("DOB"));
                aadhaarModel.setCareof(JValues.get("Care Of"));
                aadhaarModel.setBuildingNo(JValues.get("Building No"));
                aadhaarModel.setStreet(JValues.get("Street"));
                aadhaarModel.setVtc(JValues.get("VTC"));
                aadhaarModel.setPo(JValues.get("Post Office"));
                aadhaarModel.setDistrict(JValues.get("District"));
                aadhaarModel.setSubDistrict(JValues.get("Sub District"));
                aadhaarModel.setState(JValues.get("State"));
                aadhaarModel.setPin(JValues.get("Pincode"));

                qrResult.setText(aadhaarModel.toString());

                proceed.setClickable(true);
                proceed.setBackgroundTintList(NewBeneficiary1.this.getResources().getColorStateList(R.color.colorProceed));
                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            else {
                Toast.makeText(NewBeneficiary1.this, "Please scan the QR code on your Aadhaar card.", Toast.LENGTH_LONG).show();
            }

        }
    }
}