package com.labters.documentscanner;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.labters.documentscanner.base.DocumentScanActivity;
import com.labters.documentscanner.base.ImageProcessor;
import com.labters.documentscanner.base.OpenNoteMessage;
import com.labters.documentscanner.base.ScannedDocument;
import com.labters.documentscanner.helpers.ImageUtils;
import com.labters.documentscanner.helpers.ScannerConstants;
import com.labters.documentscanner.libraries.PolygonView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import java.io.ByteArrayOutputStream;
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

public class ApplyFiltersActivity extends DocumentScanActivity {

    private boolean isInverted, isContrast1, isContrast2;
    private FrameLayout holderImageCrop;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Bitmap cropImage;
    private ImageView btnDone, btnClose;
    private TextView text1, text2, text3, text4;
    LinearLayout filter4, filter2, filter3, filter1;
    ImageView filter4img, filter2img, filter3img, filter1img;
    private ImageProcessor mImageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_filters);

        cropImage = ScannerConstants.selectedImageBitmap;
        isInverted = false;
        isContrast1 = false;
        isContrast2 = false;
        if (ScannerConstants.selectedImageBitmap != null)
            initView();
        else {
            Toast.makeText(this, ScannerConstants.imageError, Toast.LENGTH_LONG).show();
            finish();
        }

        btnClose.setOnClickListener(v -> {
            ApplyFiltersActivity.super.onBackPressed();
            ScannerConstants.position = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("position")));

        });

        btnDone.setOnClickListener(v -> {
            ScannerConstants.position = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("position")));
            saveToInternalStorage(cropImage);
            ApplyFiltersActivity.super.onBackPressed();
        });

        filter1.setOnClickListener(v -> {
            filter1img.setImageResource(R.drawable.ic_filter_normal_selected);
            filter2img.setImageResource(R.drawable.ic_filter_b_and_w_24px);
            filter3img.setImageResource(R.drawable.ic_photo_filter_highcont);
            filter4img.setImageResource(R.drawable.ic_baseline_gradient_24);
            text1.setTypeface(Typeface.DEFAULT_BOLD);
            text2.setTypeface(Typeface.DEFAULT);
            text3.setTypeface(Typeface.DEFAULT);
            text4.setTypeface(Typeface.DEFAULT);

            text1.setTextColor(getResources().getColor(R.color.colorAccent));
            text2.setTextColor(getResources().getColor(R.color.colorGrey));
            text3.setTextColor(getResources().getColor(R.color.colorGrey));
            text4.setTextColor(getResources().getColor(R.color.colorGrey));

            Bitmap scaledBitmap = scaledBitmap(ScannerConstants.selectedImageBitmap, holderImageCrop.getWidth(), holderImageCrop.getHeight());
            imageView.setImageBitmap(scaledBitmap);
        });

        filter2.setOnClickListener(v -> {
            filter1img.setImageResource(R.drawable.ic_filter_normal);
            filter2img.setImageResource(R.drawable.ic_filter_b_and_w_selected);
            filter3img.setImageResource(R.drawable.ic_photo_filter_highcont);
            filter4img.setImageResource(R.drawable.ic_baseline_gradient_24);
            text1.setTypeface(Typeface.DEFAULT);
            text2.setTypeface(Typeface.DEFAULT_BOLD);
            text3.setTypeface(Typeface.DEFAULT);
            text4.setTypeface(Typeface.DEFAULT);

            text1.setTextColor(getResources().getColor(R.color.colorGrey));
            text2.setTextColor(getResources().getColor(R.color.colorAccent));
            text3.setTextColor(getResources().getColor(R.color.colorGrey));
            text4.setTextColor(getResources().getColor(R.color.colorGrey));

            showProgressBar();
            disposable.add(
                    Observable.fromCallable(() -> {
                        isContrast1 = false;
                        isContrast2 = false;
                        invertColor();
                        return false;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        hideProgressBar();
                        Bitmap scaledBitmap = scaledBitmap(cropImage, holderImageCrop.getWidth(), holderImageCrop.getHeight());
                        imageView.setImageBitmap(scaledBitmap);
                    })
            );
        });

        filter3.setOnClickListener(v -> {
            filter1img.setImageResource(R.drawable.ic_filter_normal);
            filter2img.setImageResource(R.drawable.ic_filter_b_and_w_24px);
            filter3img.setImageResource(R.drawable.ic_photo_filter_highcont_selected);
            filter4img.setImageResource(R.drawable.ic_baseline_gradient_24);
            text1.setTypeface(Typeface.DEFAULT);
            text2.setTypeface(Typeface.DEFAULT);
            text3.setTypeface(Typeface.DEFAULT_BOLD);
            text4.setTypeface(Typeface.DEFAULT);

            text1.setTextColor(getResources().getColor(R.color.colorGrey));
            text2.setTextColor(getResources().getColor(R.color.colorGrey));
            text3.setTextColor(getResources().getColor(R.color.colorAccent));
            text4.setTextColor(getResources().getColor(R.color.colorGrey));
            showProgressBar();
            disposable.add(
                    Observable.fromCallable(() -> {
                        isInverted = false;
                        isContrast2 = false;
                        applyContrast1();
                        return false;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        hideProgressBar();
                        Bitmap scaledBitmap = scaledBitmap(cropImage, holderImageCrop.getWidth(), holderImageCrop.getHeight());
                        imageView.setImageBitmap(scaledBitmap);
                    })
            );
        });

        filter4.setOnClickListener(v -> {
            filter1img.setImageResource(R.drawable.ic_filter_normal);
            filter2img.setImageResource(R.drawable.ic_filter_b_and_w_24px);
            filter3img.setImageResource(R.drawable.ic_photo_filter_highcont);
            filter4img.setImageResource(R.drawable.ic_baseline_gradient_selected24);
            text1.setTypeface(Typeface.DEFAULT);
            text2.setTypeface(Typeface.DEFAULT);
            text3.setTypeface(Typeface.DEFAULT);
            text4.setTypeface(Typeface.DEFAULT_BOLD);

            text1.setTextColor(getResources().getColor(R.color.colorGrey));
            text2.setTextColor(getResources().getColor(R.color.colorGrey));
            text3.setTextColor(getResources().getColor(R.color.colorGrey));
            text4.setTextColor(getResources().getColor(R.color.colorAccent));
            showProgressBar();
            disposable.add(
                    Observable.fromCallable(() -> {
                        isInverted = false;
                        isContrast1 = false;
                        applyContrast2();
                        return false;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        hideProgressBar();
                        Bitmap scaledBitmap = scaledBitmap(cropImage, holderImageCrop.getWidth(), holderImageCrop.getHeight());
                        imageView.setImageBitmap(scaledBitmap);
                    })
            );
        });
    }

    private void initView() {
        btnDone = findViewById(R.id.btnDone);
        btnClose = findViewById(R.id.btnClose);
        holderImageCrop = findViewById(R.id.holderImageCrop);
        imageView = findViewById(R.id.imageView);

        filter4 = findViewById(R.id.filter4);
        filter3 = findViewById(R.id.filter3);
        filter1 = findViewById(R.id.filter1);
        filter2 = findViewById(R.id.filter2);

        filter4img = findViewById(R.id.filter4img);
        filter3img = findViewById(R.id.filter3img);
        filter1img = findViewById(R.id.filter1img);
        filter2img = findViewById(R.id.filter2img);
        text1 = findViewById(R.id.filter1_text);
        text2 = findViewById(R.id.filter2_text);
        text3 = findViewById(R.id.filter3_text);
        text4 = findViewById(R.id.filter4_text);

        progressBar = findViewById(R.id.progressBar);

        showProgressBar();

//        Bitmap bitmap = scaledBitmap(cropImage, ScannerConstants.selectedImageBitmap.getWidth(), ScannerConstants.selectedImageBitmap.getHeight());
//        imageView.setImageBitmap(bitmap);

        disposable.add(
                Observable.fromCallable(() -> {
                    isInverted = false;
                    isContrast2 = false;
                    applyContrast1();
                    return false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    hideProgressBar();
                    Bitmap scaledBitmap = scaledBitmap(cropImage, holderImageCrop.getWidth(), holderImageCrop.getHeight());
                    imageView.setImageBitmap(scaledBitmap);
                })
        );

        text3.setTextColor(getResources().getColor(R.color.colorAccent));
        filter3img.setImageResource(R.drawable.ic_photo_filter_highcont_selected);
        text3.setTypeface(Typeface.DEFAULT_BOLD);

        if (progressBar.getIndeterminateDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(ScannerConstants.progressColor), android.graphics.PorterDuff.Mode.MULTIPLY);
        else if (progressBar.getProgressDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getProgressDrawable().setColorFilter(Color.parseColor(ScannerConstants.progressColor), android.graphics.PorterDuff.Mode.MULTIPLY);

        hideProgressBar();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i("OpenCV", "OpenCV loaded successfully");
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        if (mImageProcessor == null) {
            mImageProcessor = new ImageProcessor(this);
        }
    }

    private void invertColor()// BW filter
    {
        if (!isInverted) {
            cropImage = ScannerConstants.selectedImageBitmap.copy(ScannerConstants.selectedImageBitmap.getConfig(), true);
            Bitmap bmpMonochrome = Bitmap.createBitmap(cropImage.getWidth(), cropImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmpMonochrome);
            ColorMatrix ma = new ColorMatrix();
            ma.setSaturation(0);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(ma));
            canvas.drawBitmap(cropImage, 0, 0, paint);
            cropImage = bmpMonochrome.copy(bmpMonochrome.getConfig(), true);

            int width = cropImage.getWidth();
            int height = cropImage.getHeight();
            // create output bitmap

            // create a mutable empty bitmap
            Bitmap bmOut = Bitmap.createBitmap(width, height, cropImage.getConfig());

            // create a canvas so that we can draw the bmOut Bitmap from source bitmap
            Canvas c = new Canvas();
            c.setBitmap(bmOut);

            // draw bitmap to bmOut from src bitmap so we can modify it
            c.drawBitmap(cropImage, 0, 0, new Paint(Color.BLACK));

            // color information
            int A, R, G, B;
            int pixel;
            // get contrast value
            double contrast = Math.pow((100 + 60.0) / 100, 2);

            // scan through all pixels
            for(int x = 0; x < width; ++x) {
                for(int y = 0; y < height; ++y) {
                    // get pixel color
                    pixel = cropImage.getPixel(x, y);
                    A = Color.alpha(pixel);
                    // apply filter contrast for every channel R, G, B
                    R = Color.red(pixel);
                    R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                    if(R < 0) { R = 0; }
                    else if(R > 255) { R = 255; }

                    G = Color.green(pixel);
                    G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                    if(G < 0) { G = 0; }
                    else if(G > 255) { G = 255; }

                    B = Color.blue(pixel);
                    B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                    if(B < 0) { B = 0; }
                    else if(B > 255) { B = 255; }

                    // set new pixel color to output bitmap
                    bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                }
            }
            cropImage = bmOut.copy(bmOut.getConfig(), true);

        } else {
            cropImage = cropImage.copy(cropImage.getConfig(), true);
        }
        isInverted = !isInverted;
    }

    public void saveDocument(ScannedDocument scannedDocument) {
        Mat doc = (scannedDocument.processed != null) ? scannedDocument.processed : scannedDocument.original;
        cropImage = ImageUtils.matToBitmap(doc);
    }

    private void applyContrast1() //enhance filter
    {
        if(!isContrast1) {
            // image size

            cropImage = ScannerConstants.selectedImageBitmap.copy(ScannerConstants.selectedImageBitmap.getConfig(), true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            cropImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] data = out.toByteArray();

            Mat mat = new Mat(new Size(cropImage.getWidth(), cropImage.getHeight()), CvType.CV_8U);
            mat.put(0, 0, data);

            Message msg = mImageProcessor.obtainMessage();
            msg.obj = new OpenNoteMessage("pictureTaken", mat);
            mImageProcessor.sendMessage(msg);
//            int width = cropImage.getWidth();
//            int height = cropImage.getHeight();
//            // create output bitmap
//
//            // create a mutable empty bitmap
//            Bitmap bmOut = Bitmap.createBitmap(width, height, cropImage.getConfig());
//
//            // create a canvas so that we can draw the bmOut Bitmap from source bitmap
//            Canvas c = new Canvas();
//            c.setBitmap(bmOut);
//
//            // draw bitmap to bmOut from src bitmap so we can modify it
//            c.drawBitmap(cropImage, 0, 0, new Paint(Color.BLACK));
//
//
//            // color information
//            int A, R, G, B;
//            int pixel;
//            // get contrast value
//            double contrast = Math.pow((80 + 50.0) / 100, 2);
//
//            // scan through all pixels
//            for(int x = 0; x < width; ++x) {
//                for(int y = 0; y < height; ++y) {
//                    // get pixel color
//                    pixel = cropImage.getPixel(x, y);
//                    A = Color.alpha(pixel);
//                    // apply filter contrast for every channel R, G, B
//                    R = Color.red(pixel);
//                    R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//                    if(R < 0) { R = 0; }
//                    else if(R > 255) { R = 255; }
//
//                    G = Color.green(pixel);
//                    G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//                    if(G < 0) { G = 0; }
//                    else if(G > 255) { G = 255; }
//
//                    B = Color.blue(pixel);
//                    B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//                    if(B < 0) { B = 0; }
//                    else if(B > 255) { B = 255; }
//
//                    // set new pixel color to output bitmap
//                    bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//                }
//            }
//            cropImage = bmOut.copy(bmOut.getConfig(), true);
//            float contrast = 2.0f, brightness = -10.0f;
//            ColorMatrix cm = new ColorMatrix(new float[]
//                    {
//                            contrast, 0, 0, 0, brightness,
//                            0, contrast, 0, 0, brightness,
//                            0, 0, contrast, 0, brightness,
//                            0, 0, 0, 1, 0
//                    });
//
//            Bitmap ret = Bitmap.createBitmap(cropImage.getWidth(), cropImage.getHeight(), Bitmap.Config.ARGB_8888);
//
//            Canvas canvas = new Canvas(ret);
//
//            Paint paint = new Paint();
//            paint.setColorFilter(new ColorMatrixColorFilter(cm));
//            canvas.drawBitmap(cropImage, 0, 0, paint);
        } else {
            cropImage = cropImage.copy(cropImage.getConfig(), true);
        }
        isContrast1 = !isContrast1;
    }

    private void applyContrast2() //modern filter
    {
        if(!isContrast2) {
            // image size
            cropImage = ScannerConstants.selectedImageBitmap.copy(ScannerConstants.selectedImageBitmap.getConfig(), true);

            int width = cropImage.getWidth();
            int height = cropImage.getHeight();
            // create output bitmap

            // create a mutable empty bitmap
            Bitmap bmOut = Bitmap.createBitmap(width, height, cropImage.getConfig());

            // create a canvas so that we can draw the bmOut Bitmap from source bitmap
            Canvas c = new Canvas();
            c.setBitmap(bmOut);

            // draw bitmap to bmOut from src bitmap so we can modify it
            c.drawBitmap(cropImage, 0, 0, new Paint(Color.BLACK));


            // color information
            int A, R, G, B;
            int pixel;
            // get contrast value
            double contrast = Math.pow((100 + 45.0) / 100, 2);

            // scan through all pixels
            for(int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    // get pixel color
                    pixel = cropImage.getPixel(x, y);
                    A = Color.alpha(pixel);
                    // apply filter contrast for every channel R, G, B
                    R = Color.red(pixel);
                    R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                    if (R < 0) {
                        R = 0;
                    } else if (R > 255) {
                        R = 255;
                    }

                    G = Color.green(pixel);
                    G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                    if (G < 0) {
                        G = 0;
                    } else if (G > 255) {
                        G = 255;
                    }

                    B = Color.blue(pixel);
                    B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                    if (B < 0) {
                        B = 0;
                    } else if (B > 255) {
                        B = 255;
                    }

                    // set new pixel color to output bitmap
                    bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                }
            }
            cropImage = bmOut.copy(bmOut.getConfig(), true);
        } else {
            cropImage = cropImage.copy(cropImage.getConfig(), true);
        }
        isContrast2 = !isContrast2;
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
    protected PolygonView getPolygonView() { return null; }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void showProgressBar() {
        RelativeLayout rlContainer = findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, false);
        progressBar.setVisibility(View.VISIBLE);
        if (progressBar.getIndeterminateDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getIndeterminateDrawable().setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
        else if (progressBar.getProgressDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getProgressDrawable().setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
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

    private void saveToInternalStorage(Bitmap bitmapImage) {

        File imageFile = new File(getIntent().getStringExtra("path"), Objects.requireNonNull(getIntent().getStringExtra("imageName")));
        try (FileOutputStream out = new FileOutputStream(imageFile)){
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File compressedImageFile = null;
        try {
            compressedImageFile = new Compressor(ApplyFiltersActivity.this).compressToFile(imageFile);
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