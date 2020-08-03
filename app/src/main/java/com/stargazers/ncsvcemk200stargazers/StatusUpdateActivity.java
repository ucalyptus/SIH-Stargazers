package com.stargazers.ncsvcemk200stargazers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.labters.documentscanner.helpers.ScannerConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.stargazers.ncsvcemk200stargazers.util.NetworkClient;
import com.stargazers.ncsvcemk200stargazers.util.UploadApis;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StatusUpdateActivity extends AppCompatActivity {

    Button takePic, proceed;
    ImageView pic;

    private static final int CAMERA_REQUEST_CODE = 200;
    String[] cameraPermission;
    String[] storagePermission;
    private static final int STORAGE_REQUEST_CODE = 400;

    String label;

    private ProgressDialog dialog;

    private Bitmap bitmap;
    private byte[] by;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);

        cameraPermission = new String[]{Manifest.permission.CAMERA};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        takePic = findViewById(R.id.take_aadhaar_image);
        pic = findViewById(R.id.aadhaar_image);
        proceed = findViewById(R.id.proceed);

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission() || !checkStoragePermission()) {
                    requestCameraPermission();
                    requestStoragePermission();
                }
                else {
                    Intent intent = new Intent(StatusUpdateActivity.this, CameraActivity.class);
                    intent.putExtra("type", "house");
                    startActivity(intent);
                }
            }
        });

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
                        Intent intent = new Intent(StatusUpdateActivity.this, CameraActivity.class);
                        intent.putExtra("type", "doc");
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(this,"permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /////////////////////PERMISSIONS////////////////////

    private void uploadImage() {
//        File imageFile = new File(Environment.getExternalStorageDirectory() + "/Awaas/", "house.jpg");

//        File compressedImageFile = null;
//        try {
//            compressedImageFile = new Compressor(StatusUpdateActivity.this).compressToFile(imageFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(compressedImageFile))){
//            try (OutputStream outputStream = new FileOutputStream(imageFile)) {
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = inputStream.read(buf)) > 0) {
//                    outputStream.write(buf, 0 ,len);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////            Save to file

        File file = new File(Environment.getExternalStorageDirectory()+"/Awaas/","temp.jpg");
//        Picasso.get().load(file).memoryPolicy(MemoryPolicy.NO_CACHE)
//                .into(pic);

        Retrofit retrofit = NetworkClient.getRetrofit();

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

//        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), "This is a new Image");

        UploadApis uploadApis = retrofit.create(UploadApis.class);
        Call call = uploadApis.uploadImage(parts);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("hmm", response.toString());
                ResponseBody jo = (ResponseBody) response.body();
                try {
                    JSONObject jsonObject = new JSONObject(jo.string());
                    label = jsonObject.get("label").toString();
                    Toast.makeText(StatusUpdateActivity.this, label , Toast.LENGTH_LONG).show();
//                    String label = jsonObject.get("confidence").toString();
//                    String label = jsonObject.get("s").toString();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(StatusUpdateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(ScannerConstants.selectedImageBitmap != null){

            bitmap = ScannerConstants.selectedImageBitmap;
            File imageFile = new File(Environment.getExternalStorageDirectory() + "/Awaas/", "temp.jpg");
            File compressedImageFile = null;
            try {
                compressedImageFile = new Compressor(StatusUpdateActivity.this).compressToFile(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(compressedImageFile))){
                try (OutputStream outputStream = new FileOutputStream(imageFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0 ,len);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Save to file

            uploadImage();


            File file = new File(Environment.getExternalStorageDirectory()+"/Awaas/","temp.jpg");
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            by = out.toByteArray();

            Picasso.get().load(file).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(pic);

            proceed.setClickable(true);
            proceed.setBackgroundTintList(StatusUpdateActivity.this.getResources().getColorStateList(R.color.colorProceed));
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(StatusUpdateActivity.this, "Please wait", Toast.LENGTH_LONG).show();
//                    databaseUpload();
                }
            });
            ScannerConstants.selectedImageBitmap = null;
        }
    }


    private  void databaseUpload() {
//        File imageFile = new File(Environment.getExternalStorageDirectory() + "/Awaas/", "temp.jpg");
//        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        byte[] by = out.toByteArray();
        StorageReference reference;

        Toast.makeText(StatusUpdateActivity.this, ""+by.length, Toast.LENGTH_SHORT).show();

        reference = FirebaseStorage.getInstance().getReference().child("Applications/").child(FirebaseAuth.getInstance().getUid() + "_doc");
        reference.putBytes(by).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String generatedFilePath = uri.toString();
                        NewBeneficiary1.applicationModel.setDocPic(generatedFilePath);

                        FirebaseFirestore.getInstance().document("Applications/"+FirebaseAuth.getInstance().getUid()+"/")
                                .set(NewBeneficiary1.applicationModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(StatusUpdateActivity.this, "Completed", Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                });
            }
        });

    }

}