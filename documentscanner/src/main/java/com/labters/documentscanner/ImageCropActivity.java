package com.labters.documentscanner;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.labters.documentscanner.base.DocumentScanActivity;
import com.labters.documentscanner.helpers.ScannerConstants;
import com.labters.documentscanner.libraries.PolygonView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageCropActivity extends DocumentScanActivity {

    private FrameLayout holderImageCrop;
    private PolygonView polygonView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Bitmap cropImage;

    private OnClickListener onRotateRightClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showProgressBar();
            disposable.add(
                    Observable.fromCallable(() -> {
                        cropImage = rotateBitmap(cropImage, 90);
                        return false;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        hideProgressBar();
                        startCropping(cropImage);
                    })
            );
        }
    };

    private OnClickListener onRotateLeftClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showProgressBar();
            disposable.add(
                    Observable.fromCallable(() -> {
                        cropImage = rotateBitmap(cropImage, -90);
                        return false;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        hideProgressBar();
                        startCropping(cropImage);
                    })
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        cropImage = ScannerConstants.selectedImageBitmap;

        //fix orientation
        int orientation = ImageCropActivity.this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            cropImage = Bitmap.createBitmap(cropImage, 0 ,0,cropImage.getWidth(), cropImage.getHeight(),matrix,true ) ;
        }
        //fix orientation

        if (ScannerConstants.selectedImageBitmap != null)
            initView();
        else {
            Toast.makeText(this, ScannerConstants.imageError, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected FrameLayout getHolderImageCrop() {
        return holderImageCrop;
    }

    @Override
    protected ImageView getImageView() {
        return imageView;
    }

    @Override
    protected PolygonView getPolygonView() {
        return polygonView;
    }

    @Override
    protected void showProgressBar() {
        RelativeLayout rlContainer = findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideProgressBar() {
        RelativeLayout rlContainer = findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void showError() {
        Toast.makeText(this, ScannerConstants.cropError, Toast.LENGTH_LONG).show();
    }

    private void setViewInteract(View view, boolean canDo) {
        view.setEnabled(canDo);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setViewInteract(((ViewGroup) view).getChildAt(i), canDo);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        ImageView done = findViewById(R.id.done);
        ImageView btnClose = findViewById(R.id.btnClose);
        holderImageCrop = findViewById(R.id.holderImageCrop);
        imageView = findViewById(R.id.imageView);
        LinearLayout rotateLeft = findViewById(R.id.rotate_left);
        LinearLayout rotateRight = findViewById(R.id.rotate_right);
        polygonView = findViewById(R.id.polygonView);
        progressBar = findViewById(R.id.progressBar);

        Bitmap scaledBitmap = scaledBitmap(cropImage, cropImage.getWidth(), cropImage.getHeight());
        imageView.setImageBitmap(scaledBitmap);

        if (progressBar.getIndeterminateDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getIndeterminateDrawable().setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
        else if (progressBar.getProgressDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getProgressDrawable().setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);

        rotateRight.setOnClickListener(onRotateRightClick);
        rotateLeft.setOnClickListener(onRotateLeftClick);

        startCropping(cropImage);

        if(getIntent().getStringExtra("From") != null && Objects.requireNonNull(getIntent().getStringExtra("From")).matches("Camera")) {
            done.setOnClickListener(v -> {
                ScannerConstants.bitmap = getCroppedImage(cropImage);
                ImageCropActivity.super.onBackPressed();
            });
            btnClose.setOnClickListener(v -> {
                ScannerConstants.bitmap = null;
                ImageCropActivity.super.onBackPressed();
            });
        }
        else {
            done.setOnClickListener(v -> {
                ScannerConstants.position = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("position")));
                saveToInternalStorage(getCroppedImage(cropImage));
                ImageCropActivity.super.onBackPressed();
            });
            btnClose.setOnClickListener(v -> {
                ImageCropActivity.super.onBackPressed();
            });
        }
    }

    @Override
    public void onBackPressed() {
        ScannerConstants.bitmap = null;
        super.onBackPressed();
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {

        File imageFile = new File(getIntent().getStringExtra("path"), Objects.requireNonNull(getIntent().getStringExtra("imageName")));
        try (FileOutputStream out = new FileOutputStream(imageFile)){
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File compressedImageFile = null;
        try {
            compressedImageFile = new Compressor(ImageCropActivity.this).compressToFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(compressedImageFile))){
            try (OutputStream outputStream = new FileOutputStream(imageFile)){
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0){
                    outputStream.write(buf, 0 ,len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}