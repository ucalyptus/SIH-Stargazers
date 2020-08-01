package com.stargazers.ncsvcemk200stargazers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.labters.documentscanner.ImageCropActivity;
import com.labters.documentscanner.helpers.ScannerConstants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.selector.JpegQualitySelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.view.FocusView;

import static com.applex.snaplingo.OcrResultActivity.resultUri;
import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.result.transformer.ResolutionTransformersKt.scaled;
import static io.fotoapparat.selector.AntiBandingModeSelectorsKt.auto;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.on;
import static io.fotoapparat.selector.FocusModeSelectorsKt.autoFocus;
import static io.fotoapparat.selector.FocusModeSelectorsKt.continuousFocusPicture;
import static io.fotoapparat.selector.FocusModeSelectorsKt.fixed;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.LensPositionSelectorsKt.front;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SelectorsKt.firstAvailable;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.lowestSensorSensitivity;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class CameraActivity extends AppCompatActivity {

    private static final String LOGGING_TAG = "Fotoapparat Example";

//    private final PermissionsDelegate permissionsDelegate = new PermissionsDelegate(this);
//    private boolean hasCameraPermission;
    ImageView preview;

    private CameraView cameraView;
    private FocusView focusView;
    private View capture;

    private Fotoapparat fotoapparat;

    boolean activeCameraBack = true;
    ImageView torchSwitch, done;
    View switchCameraButton;
    TextView count;
    private int img_count = 0;
    private ArrayList<Bitmap> images = new ArrayList<>();

    long[] flashState; //0 = off; 1 = auto; 2 = onhaha

    private String docName, imageName;
    private boolean isFirst;

    private CameraConfiguration cameraConfiguration = CameraConfiguration
            .builder()
            .photoResolution(firstAvailable(ResolutionSelectorsKt.highestResolution()))
            .focusMode(firstAvailable(
                    continuousFocusPicture(),
                    autoFocus(),
                    fixed()
            ))
            .flash(firstAvailable(
                    off()
            ))
            .jpegQuality(JpegQualitySelectorsKt.highestQuality())
            .antiBandingMode(
                    auto()
            )
            .previewResolution(firstAvailable(highestResolution()))
            .previewFpsRange(highestFps())
            .sensorSensitivity(lowestSensorSensitivity())
            .frameProcessor(new SampleFrameProcessor())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        cameraView = findViewById(R.id.cameraView);
        focusView = findViewById(R.id.focusView);
        capture = findViewById(R.id.capture);
        torchSwitch = findViewById(R.id.torchSwitch);
        switchCameraButton = findViewById(R.id.switchCamera);
        preview = findViewById(R.id.preview);
        done = findViewById(R.id.done);
//        count = findViewById(R.id.count);

        cameraView.setVisibility(View.VISIBLE);

        //for new doc
        if(getIntent().getStringExtra("boolcam") != null && getIntent().getStringExtra("boolcam").matches("1")){
            docName = String.valueOf(System.currentTimeMillis());
            isFirst = true;
        }
        //from existing doc
        else if(getIntent().getStringExtra("boolcam") != null && getIntent().getStringExtra("boolcam").matches("2")) {
            docName = getIntent().getStringExtra("prevDocName");
            isFirst = false;
        }
        else {
            isFirst = false;
            docName = getIntent().getStringExtra("prevDocName");
        }

        fotoapparat = createFotoapparat();
        fotoapparat.updateConfiguration(cameraConfiguration);

        takePictureOnClick();
        switchCameraOnClick();
        toggleTorchOnSwitch();
        zoomSeekBar();

        if(getIntent().getStringExtra("boolcam") != null) {

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ImageSaving(images, isFirst, docName, CameraActivity.this).execute();
                }
            });

            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ImageSaving(images, isFirst, docName, CameraActivity.this).execute();
                }
            });
        }
        else {
            done.setVisibility(View.GONE);
        }

    }

    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .focusView(focusView)
                .previewScaleType(ScaleType.CenterInside)
                .lensPosition(back())
                .frameProcessor(new SampleFrameProcessor())
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException e) {
                        Toast.makeText(CameraActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .flash(firstAvailable(
                        off()
                ))
                .photoResolution(firstAvailable(ResolutionSelectorsKt.highestResolution()))
                .jpegQuality(JpegQualitySelectorsKt.highestQuality())
                .previewResolution(firstAvailable(highestResolution()))
                .previewFpsRange(highestFps())
                .sensorSensitivity(lowestSensorSensitivity())

                .build();
    }

    private void zoomSeekBar() {
        SeekBar seekBar = findViewById(R.id.zoomSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fotoapparat.setZoom(progress / (float) seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void switchCameraOnClick() {

        boolean hasFrontCamera = fotoapparat.isAvailable(front());

        switchCameraButton.setVisibility(
                hasFrontCamera ? View.VISIBLE : View.GONE
        );

        if (hasFrontCamera) {
            switchCameraOnClick(switchCameraButton);
        }
    }

    private void toggleTorchOnSwitch() {

        torchSwitch.setImageResource(R.drawable.ic_flash_off_black_24dp);
        flashState = new long[]{0};//on

        fotoapparat.updateConfiguration(
                UpdateConfiguration.builder()
                        .flash(off())
                        .build()
        );

        torchSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flashState[0] == 2){//on
//                    Animation rotate = AnimationUtils.loadAnimation(CameraActivity.this, R.anim.rotate_flash360);
//                    torchSwitch.startAnimation(rotate);
                    torchSwitch.setImageResource(R.drawable.ic_flash_off_black_24dp);

                    flashState[0] = 0;//off
                    fotoapparat.updateConfiguration(
                            UpdateConfiguration.builder()
                                    .flash(off())
                                    .build()
                    );
                }
                else {//off
//                    Animation rotate = AnimationUtils.loadAnimation(CameraActivity.this, R.anim.rotate_flash360_clockwise);
//                    torchSwitch.startAnimation(rotate);
                    torchSwitch.setImageResource(R.drawable.ic_flash_on_black_24dp);

                    flashState[0] = 2;//auto
                    fotoapparat.updateConfiguration(
                            UpdateConfiguration.builder()
                                    .flash(on())
                                    .build()
                    );
                }
            }
        });

    }

    private void switchCameraOnClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeCameraBack = !activeCameraBack;
                fotoapparat.switchTo(
                        activeCameraBack ? back() : front(),
                        cameraConfiguration
                );
            }
        });
    }

    private void takePictureOnClick() {
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePicture();
                Toast toast = Toast.makeText(CameraActivity.this,"Hold steady",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,240);
                toast.show();
            }
        });
    }

    private void takePicture() {
        PhotoResult photoResult = fotoapparat.takePicture();
//        photoResult.saveToFile(new File(Environment.getExternalStorageDirectory()+"/SnapLingo/.documents", docName))
//                .whenDone(new WhenDoneListener<Unit>() {
//                    @Override
//                    public void whenDone(@org.jetbrains.annotations.Nullable Unit unit) {
//                        if(getIntent().getStringExtra("boolcam") != null) {
//
//                            File f =  new File(Environment.getExternalStorageDirectory()+"/SnapLingo/.documents", docName);
//                            f.mkdirs();
//                            if(f.exists()){
//                                f.mkdir();
//                            }
//
//                            Bitmap bitmap = bitmapPhoto.bitmap;
//
//                            //fix orientation
//                            int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
//                            if(orientation == Configuration.ORIENTATION_PORTRAIT) {
//                                Matrix matrix = new Matrix();
//                                matrix.postRotate(90);
//                                bitmap = Bitmap.createBitmap(bitmap, 0 ,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true ) ;
//                            }
//                            //fix orientation
//
//                            //Save to file
//                            File imageFile = new File(Environment.getExternalStorageDirectory() + "/SnapLingo/.documents/"+docName+"/", imageName);
//                            try (FileOutputStream out = new FileOutputStream(imageFile)){
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            //Save to file
//
//                            Intent intent = new Intent(CameraActivity.this, ViewPager.class);
//                            if(isFirst){
//                                intent.putExtra("from", "1");//first time from camera
//                            }
//                            else {
//                                intent.putExtra("from", "3");//new Image addition from camera
//                                String pos = getIntent().getStringExtra("imageCount");
//                                intent.putExtra("pos", pos);
//                            }
//                            intent.putExtra("docName", docName);
//                            startActivity(intent);
//                            finish();
//                        }
//
//                    }
//                });
        photoResult.toBitmap(scaled(0.3f))
                .whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(@org.jetbrains.annotations.Nullable BitmapPhoto bitmapPhoto) {

                        if (bitmapPhoto == null) {
                            Log.e(LOGGING_TAG, "Couldn't capture photo.");
                            return;
                        }
                        if(getIntent().getStringExtra("boolcam") != null) {


                            File f =  new File(Environment.getExternalStorageDirectory()+"/SnapLingo/.documents", docName);
                            f.mkdirs();
                            if(f.exists()){
                                f.mkdir();
                            }

                            Bitmap bitmap = bitmapPhoto.bitmap;

                            //fix orientation
                            int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
                            if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                bitmap = Bitmap.createBitmap(bitmap, 0 ,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true ) ;
                            }
                            //fix orientation

                            ScannerConstants.selectedImageBitmap = bitmap;
                            Intent intent = new Intent(CameraActivity.this, ImageCropActivity.class);
                            intent.putExtra("From", "Camera");
                            startActivity(intent);
                        }

                        //OCR//
                        else {
//                        bitmapPhoto = -bitmapPhoto.rotationDegrees;
                            //////////Convert Bitmap to Uri////////////
                            Bitmap bitmap = bitmapPhoto.bitmap;
    //                        preview.setImageBitmap(bitmap);

                            int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
                            if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                bitmap = Bitmap.createBitmap(bitmap, 0 ,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true ) ;
                            }

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,String.valueOf(System.currentTimeMillis()),null);
                            //////////Convert Bitmap to Uri////////////

                            CropImage.activity(Uri.parse(path))
                                .setActivityTitle("SnapCrop")
                                .setAllowRotation(TRUE)
                                .setAllowCounterRotation(TRUE)
                                .setAllowFlipping(TRUE)
                                .setAutoZoomEnabled(TRUE)
                                .setMultiTouchEnabled(FALSE)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(CameraActivity.this);
                        }

                    }
                });
//        photoResult.toBitmap(scaled(0.30f))
//                .whenDone(new WhenDoneListener<BitmapPhoto>() {
//                    @Override
//                    public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
//                        if (bitmapPhoto == null) {
//                            Log.e(LOGGING_TAG, "Couldn't capture photo.");
//                            return;
//                        }
////                        bitmapPhoto = -bitmapPhoto.rotationDegrees;
//                        //////////Convert Bitmap to Uri////////////
////                        BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
//                        Bitmap bitmap = bitmapPhoto.bitmap;
////                        preview.setImageBitmap(bitmap);
//
//                        int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
//                        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(90);
//                            bitmap = Bitmap.createBitmap(bitmap, 0 ,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true ) ;
//                        }
//
//                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
//                        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,String.valueOf(System.currentTimeMillis()),null);
//                        //////////Convert Bitmap to Uri////////////
//
//                        if(getIntent().getStringExtra("boolcam") != null && getIntent().getStringExtra("boolcam").matches("1")) {
////                            mImagesUri.clear();
////                            mImagesUri.add(path);
//                            Intent intent = new Intent(CameraActivity.this, ViewPager.class);
//                            intent.putExtra("docName", docName);
//                            intent.putExtra("fromCamera", "true");
////                                intent.putExtra("firstTime", getIntent().getStringExtra("firstTime"));
////                                intent.putStringArrayListExtra("ImageList", mImagesUri);
//                            startActivity(intent);
//                            finish();
//
////                            if (mImagesUri.size() > 0) {
////                                Intent intent = new Intent(CameraActivity.this, ViewPager.class);
////                                intent.putExtra("docName", docName);
////                                intent.putExtra("fromCamera", "true");
//////                                intent.putExtra("firstTime", getIntent().getStringExtra("firstTime"));
//////                                intent.putStringArrayListExtra("ImageList", mImagesUri);
////                                startActivity(intent);
////                                finish();
////                            }
//                        }
//                        else if(getIntent().getStringExtra("boolcam") != null && getIntent().getStringExtra("boolcam").matches("2")) {
//                            mImagesUri.clear();
//                            mImagesUri.addAll(Objects.requireNonNull(getIntent().getStringArrayListExtra("orgImgList")));
//                            int size = mImagesUri.size();
//                            mImagesUri.add(path);
//                            if (mImagesUri.size() > 0) {
//                                Intent intent = new Intent(CameraActivity.this, ViewPager.class);
//                                intent.putExtra("fromCamera", "True");
//                                intent.putExtra("bool", "2");
//                                intent.putExtra("pos", String.valueOf(size));
//                                intent.putExtra("prevDocName", getIntent().getStringExtra("prevDocName"));
//                                intent.putExtra("docBool", getIntent().getStringExtra("docBool"));
//                                intent.putStringArrayListExtra("ImageList", mImagesUri);
//                                intent.putIntegerArrayListExtra("imgBool", getIntent().getIntegerArrayListExtra("imgBool"));
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
//                        else {
//                            image_uri = Uri.parse(path);
//
//                            CropImage.activity(image_uri)
//                                    .setActivityTitle("SnapCrop")
//                                    .setAllowRotation(TRUE)
//                                    .setAllowCounterRotation(TRUE)
//                                    .setAllowFlipping(TRUE)
//                                    .setAutoZoomEnabled(TRUE)
//                                    .setMultiTouchEnabled(FALSE)
//                                    .setGuidelines(CropImageView.Guidelines.ON)
//                                    .start(CameraActivity.this);
//                        }
//
//                    }
//                });
    }
    /////////Camera & GALLERY////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try{
                    resultUri = result.getUri();
                }
                catch (Exception e){
                    Toast.makeText(CameraActivity.this, (CharSequence) e,Toast.LENGTH_SHORT).show();
                }

                ImageView imageView = findViewById(R.id.result);
                imageView.setImageURI(resultUri);
//                imageView.setRotation(-bitmapPhoto.rotationDegrees);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                }
                else {
                    com.google.android.gms.vision.Frame frame = new com.google.android.gms.vision.Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        if (i != items.size() - 1) {
                            sb.append("\n");
                        }
                    }

                    Intent intent = new Intent(CameraActivity.this, OcrResultActivity.class);
                    intent.putExtra("Text",sb.toString().trim());
                    intent.putExtra("selection", "1");
                    startActivity(intent);
                    finish();
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "+error", Toast.LENGTH_SHORT).show();
        }
    }

    /////////Camera & GALLERY////////////////

    @Override
    protected void onStart() {
        super.onStart();
//        if (hasCameraPermission) {
            fotoapparat.start();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (hasCameraPermission) {
            fotoapparat.stop();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ScannerConstants.bitmap != null) {
            img_count = img_count + 1;
            count.setVisibility(View.VISIBLE);
            count.setText(String.valueOf(img_count));

            done.setVisibility(View.VISIBLE);
            preview.setImageBitmap(ScannerConstants.bitmap);
            images.add(ScannerConstants.bitmap);
            ScannerConstants.bitmap = null;
        }
    }

    private static class SampleFrameProcessor implements FrameProcessor {
        @Override
        public void process(@NotNull Frame frame) {
            // Perform frame processing, if needed
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ImageSaving extends AsyncTask<Void, Void, Void> {

        private ArrayList<Bitmap> images;
        private boolean isFirst;
        private String docName;
        private ProgressDialog dialog;

        public ImageSaving(ArrayList<Bitmap> images, boolean isFirst, String docName, CameraActivity activity) {
            this.images = images;
            this.isFirst = isFirst;
            this.docName = docName;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Processing your Images");
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i = 0; i < images.size(); i++) {

                if(isFirst) {
                    imageName = i + ".jpg";
                }
                else {
                    if(getIntent().getStringExtra("imageCount") != null){
                        imageName = (Integer.parseInt(getIntent().getStringExtra("imageCount")) + i) +".jpg";
                    }
                    else {
                        imageName = i + ".jpg";
                    }
                }

                //Save to file
                File imageFile = new File(Environment.getExternalStorageDirectory() + "/SnapLingo/.documents/"+docName+"/", imageName);

                try (FileOutputStream out = new FileOutputStream(imageFile)){
                    images.get(i).compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File compressedImageFile = null;
                try {
                    compressedImageFile = new Compressor(CameraActivity.this).compressToFile(imageFile);
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
                //Save to file
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Intent intent = new Intent(CameraActivity.this, ViewPager.class);
            if (isFirst) {
                intent.putExtra("from", "1");//first time from camera
            } else {
                intent.putExtra("from", "3");//new Image addition from camera
                String pos = getIntent().getStringExtra("imageCount");
                intent.putExtra("pos", pos);
            }
            intent.putExtra("docName", docName);
            startActivity(intent);
            finish();
        }
    }
}
