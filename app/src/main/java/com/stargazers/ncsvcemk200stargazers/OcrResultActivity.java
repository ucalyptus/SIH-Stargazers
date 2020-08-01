package com.stargazers.ncsvcemk200stargazers;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.style.URLSpan;
import android.util.SparseArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class OcrResultActivity extends AppCompatActivity {

    private ConnectivityManager cm;

    private Dialog mydialogue;

//    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
//    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    private RelativeLayout relativeLayout;
//    private DatabaseHelper myDB;

    static String OCRtext="";
    public static int itemID = -1;

    public static EditText mResultEt;
    private TextView imgprev;

    public static Uri image_uri;

    public static Uri resultUri;

    private PhotoView pv;
    Bitmap bitmap;

    private ImageButton cpy_btn;
//    private LinearLayout btnLocate,btnTranslate, btnSearch;
    private LinearLayout fullView ;
    private Button btnVisit;
    private CardView cd;

    private Uri path;


    private int sel = 5; //like normal editing


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_result);

        Toolbar tb =  findViewById(R.id.toolb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        /////////////////AD///////////////////////
//
//        mAdView = findViewById(R.id.ad_view);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        //////////////////AD///////////////////
        cm = (ConnectivityManager) OcrResultActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        mydialogue = new Dialog(this);

//        myDB = new DatabaseHelper(this);


        relativeLayout= findViewById(R.id.rel_main) ;

//        btnLocate = findViewById(R.id.button1);
//        btnTranslate =  findViewById(R.id.button2);
//        btnSearch =  findViewById(R.id.button3);
        btnVisit =  findViewById(R.id.visitButton);
        cpy_btn =  findViewById(R.id.etbtn_copy);
        fullView = findViewById(R.id.expand);
//        linkPreviewShort = findViewById(R.id.link_preview);
        cd = findViewById(R.id.card);
        imgprev = findViewById(R.id.imgprev) ;
        mResultEt =  findViewById(R.id.resultEt);
        pv =  findViewById(R.id.photo_view);
        pv.setZoomable(true);
        pv.setAdjustViewBounds(FALSE);
        pv.setFitsSystemWindows(TRUE);


        if(pv.getImageMatrix()==null){
            imgprev.setVisibility(View.GONE);
            pv.setVisibility(View.GONE);
        }
        else {
            imgprev.setVisibility(View.VISIBLE);
            pv.setVisibility(View.VISIBLE);
        }


//        btnLocate.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                if(mResultEt.length()!=0) {
//                    databaseAdder(0);
//                    Locate();
//                }
//                else{
//                    Toast.makeText(OcrResultActivity.this,"Field empty...",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });


        /////////////////////LOCATE/////////////////////////



        ///////////////////TRANSLATE////////////////////////

//        btnTranslate.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                if(mResultEt.length()!=0) {
//                    databaseAdder(0);
//                    Translate();
//                }
//                else{
//                    Toast.makeText(OcrResultActivity.this,"Field empty...",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        ////////////////////TRANSLATE///////////////////////


        ///////////////////SEARCH////////////////////////

//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if(mResultEt.length()!=0) {
//                    databaseAdder(0);
//                    Search();
//                }
//                else{
//                    Toast.makeText(OcrResultActivity.this,"Field empty...",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        ////////////////////SEARCH///////////////////////


        //////////////////////COPY///////////////////////////
        cpy_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(mResultEt.length()!=0) {
//                    databaseAdder(0);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    String pdfText =mResultEt.getText().toString().replaceAll("\n"," ");
                    ClipData clip = ClipData.newPlainText("Copied",pdfText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(OcrResultActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(OcrResultActivity.this,"Field empty...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //////////////////////COPY///////////////////////////


        ////////////////////////FULLSCREEN//////////////////////


//        fullView.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                 Intent intent= new Intent(OcrResultActivity.this, DocView.class);
//                    ActivityOptionsCompat optionsCompat= ActivityOptionsCompat.makeSceneTransitionAnimation(OcrResultActivity.this,mResultEt, ViewCompat.getTransitionName(mResultEt));
//                    intent.putExtra("OCRtext",mResultEt.getText().toString());
//                    startActivity(intent, optionsCompat.toBundle());
//
//            }
//        });

        ////////////////////////FULLSCREEN//////////////////////


        //////////////////////PHOTOVIEW///////////////////////////

        pv.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(OcrResultActivity.this, "Tap & hold to crop", Toast.LENGTH_SHORT).show();
            }
        });

        pv.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {

                CropImage.activity(image_uri)
                        .setActivityTitle("SnapCrop")
                        .setCropMenuCropButtonTitle("Set")
                        .setAllowRotation(TRUE)
                        .setAllowCounterRotation(TRUE)
                        .setAllowFlipping(TRUE)
                        .setAutoZoomEnabled(TRUE)
                        .setMultiTouchEnabled(FALSE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(OcrResultActivity.this);
                return true;
            }
        });

        //////////////////////PHOTOVIEW/////////////////////////



        Intent intent = getIntent();
        if(intent.getStringExtra("selection") != null){
            sel = Integer.parseInt(intent.getStringExtra("selection"));
        }
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null)       //SHARED CONTENT////////////////////
        {   btnVisit.setVisibility(View.GONE);
            pv.setVisibility(View.VISIBLE);
            imgprev.setVisibility(View.VISIBLE);

            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                 if (sharedText != null) {
                     mResultEt.setText(sharedText);
                     if(mResultEt.getUrls().length>0){

                     }
                     OCRtext=mResultEt.getText().toString();
//                     databaseAdder(0);

                 }
            }
            else
                if (type.startsWith("image/")) {
                    image_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (image_uri != null) {

                        CropImage.activity(image_uri)
                                .setActivityTitle("SnapCrop")
                                .setCropMenuCropButtonTitle("Set")
                                .setAllowRotation(TRUE)
                                .setAllowCounterRotation(TRUE)
                                .setAllowFlipping(TRUE)
                                .setAutoZoomEnabled(TRUE)
                                .setMultiTouchEnabled(FALSE)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(this);

                    }
            }

        }

        else if(sel == 3){                 //RECEIVING FROM HISTORY

            btnVisit.setVisibility(View.GONE);
            pv.setVisibility(View.GONE);
            imgprev.setVisibility(View.GONE);
            mResultEt.setMinHeight(150);
            String selItem;
            Intent recvIntent = getIntent();
            selItem = recvIntent.getStringExtra("name");
            mResultEt.setText(selItem);
            OCRtext=mResultEt.getText().toString();
            if (mResultEt.length()!=0) {
                if(mResultEt.getUrls().length>0){
                    URLSpan urlSnapItem = mResultEt.getUrls()[0];
                    String url = urlSnapItem.getURL();

                }
//                databaseAdder(0);
            }
        }

        else if(sel == 2)             // FROM SCAN ACTIVITY
        {
            pv.setVisibility(View.GONE);
            imgprev.setVisibility(View.GONE);
            mResultEt.setMinHeight(150);
            Intent revintent = getIntent();
            final String str = revintent.getStringExtra("text");
            mResultEt.setText(str);

            if (mResultEt.length()!=0) {
                if(mResultEt.getUrls().length>0){
                    URLSpan urlSnapItem = mResultEt.getUrls()[0];
                    String url = urlSnapItem.getURL();

                }
//                databaseAdder(0);
            }
        }

        else if(sel == 1)                  //GALLERY OR CAMERA
        {
            btnVisit.setVisibility(View.GONE);
            pv.setVisibility(View.VISIBLE);
            imgprev.setVisibility(View.VISIBLE);

            pv.setImageURI(resultUri);

            Intent recvIntent = getIntent();
            OCRtext = recvIntent.getStringExtra("Text");
            mResultEt.setText(OCRtext);

            if(mResultEt.getUrls().length>0) {
                URLSpan urlSnapItem = mResultEt.getUrls()[0];
                String url = urlSnapItem.getURL();

            }

        }
        else if(sel == 5){ ////Edit button
            mResultEt.setHint("Start typing...");
            pv.setVisibility(View.GONE);
            imgprev.setVisibility(View.GONE);
            btnVisit.setVisibility(View.GONE);
            mResultEt.setMinHeight(150);
        }

        else if(sel == 6){
            btnVisit.setVisibility(View.GONE);
            if(intent.getStringExtra("path") != null){
                image_uri = Uri.fromFile(new File(getIntent().getStringExtra("path")));
                if (image_uri != null) {

                    CropImage.activity(image_uri)
                            .setActivityTitle("SnapCrop")
                            .setCropMenuCropButtonTitle("Set")
                            .setAllowRotation(TRUE)
                            .setAllowCounterRotation(TRUE)
                            .setAllowFlipping(TRUE)
                            .setAutoZoomEnabled(TRUE)
                            .setMultiTouchEnabled(FALSE)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);

                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }

        }
    }


    ////////////////////////FUNCTIONS//////////////////////


//    public void Search(){
//        if(cm.getActiveNetworkInfo() != null) {
//            if (mResultEt.length() != 0) {
//                try {
//                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                    String term = mResultEt.getText().toString();
//                    intent.putExtra(SearchManager.QUERY, term);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    // TODO: handle exception
//                }
//
//            } else {
//                Toast.makeText(OcrResultActivity.this, "No text found :(", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else{
//            Toast.makeText(OcrResultActivity.this, "Please check your internet connection and try again...", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void Translate(){
//        if(cm.getActiveNetworkInfo() != null) {
//            if (mResultEt.length() != 0) {
//
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("Copied", mResultEt.getText().toString());
//                clipboard.setPrimaryClip(clip);
//                String s=mResultEt.getText().toString().replaceAll(" ","+");
//                Intent intent = new Intent(OcrResultActivity.this, WebViewTranslate.class);
//                intent.putExtra("bool", "3");
//                intent.putExtra("text",s);
//                startActivity(intent);
//            } else {
//                Toast.makeText(OcrResultActivity.this, "No text found :(", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else{
//            Toast.makeText(OcrResultActivity.this, "Please check your internet connection and try again...", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void Locate(){
//        if(cm.getActiveNetworkInfo() != null) {
//            if (mResultEt.length() != 0) {
//                Uri gmmIntentUri = Uri.parse("geo:0,0?z=15&q=" + Uri.encode(mResultEt.getText().toString()));
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
//            } else {
//                Toast.makeText(OcrResultActivity.this, "Field Empty", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else{
//            Toast.makeText(OcrResultActivity.this, "Please check your internet connection and try again...", Toast.LENGTH_SHORT).show();
//        }
//    }




//    private void savePdf(){
//        TextView save;
//        final EditText fileName;
//        TextView ext ;
//
//        mydialogue = new Dialog(OcrResultActivity.this);
//        mydialogue.setContentView(R.layout.dialog_file_name);
//        mydialogue.setCanceledOnTouchOutside(TRUE);
//        mydialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ext = mydialogue.findViewById(R.id.extension);
//        ext.setText(getString(R.string.pdf));
//
//        save=  mydialogue.findViewById(R.id.save);
//        fileName =  mydialogue.findViewById(R.id.fname);
//        final String fName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) ;
//        fileName.setHint(fName);
//
//        save.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (fileName.length() != 0) {
//                    final Document mDoc = new Document();
//                    final String filName=fileName.getText().toString().trim().replaceAll(" ","_");
//                    File f=  new File(Environment.getExternalStorageDirectory()+"/SnapLingo","Pdf");
//                    f.mkdirs();
//                    final String filPath = Environment.getExternalStorageDirectory() + "/SnapLingo/Pdf/" + filName + ".pdf";
//                    try {
//                        PdfWriter.getInstance(mDoc, new FileOutputStream(filPath));
//                        mDoc.open();
//                        String pdfText = mResultEt.getText().toString().replaceAll("\n", " ");
//                        mDoc.add(new Paragraph(pdfText));
//                        mDoc.close();
//
//                        postMenuDialog = new BottomSheetDialog(OcrResultActivity.this);
//                        postMenuDialog.setContentView(R.layout.dialog_share_pdf);
//                        postMenuDialog.setCanceledOnTouchOutside(TRUE);
//                        postMenuDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        postMenuDialog.findViewById(R.id.share)
//                                .setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        File file = new File(filPath);
//                                        if (file.exists()) {
//                                            path = FileProvider.getUriForFile(
//                                                    OcrResultActivity.this,
//                                                    "com.applex.snaplingo.fileprovider",
//                                                    file);
//                                            Intent intent = new Intent();
//                                            intent.setAction(Intent.ACTION_SEND);
//                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
//                                            intent.putExtra(Intent.EXTRA_STREAM, path);
//                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                            intent.setType("Document/*");
//                                            startActivity(Intent.createChooser(intent, "SHARE"));
//
//                                        } else {
//                                            Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                        }
//                                        postMenuDialog.dismiss();
//                                    }
//                                });
//
//                        postMenuDialog.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                    File file = new File(filPath);
//                                    if (file.exists()) {
//                                        Uri uri = FileProvider.getUriForFile(
//                                                OcrResultActivity.this,
//                                                "com.applex.snaplingo.fileprovider",
//                                                file);
//
//                                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                        intent.setDataAndType(uri, "application/pdf");
//                                        Intent newintent = Intent.createChooser(intent, "Open File");
//
//                                        try {
//                                            newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(newintent);
//
//                                        } catch (ActivityNotFoundException e) {
//                                            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    }
//                                    else {
//                                        Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                    }
//
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        Objects.requireNonNull(postMenuDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        postMenuDialog.show();
//
//                    } catch (Exception e) {
//                        Toast.makeText(OcrResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else{
//                    final Document mDoc = new Document();
//                    final String filName = fName;
//                    File f = new File(Environment.getExternalStorageDirectory()+"/SnapLingo","Pdf");
//                    f.mkdirs();
//                    final String filPath = Environment.getExternalStorageDirectory() + "/SnapLingo/Pdf/" + filName + ".pdf";
//                    try {
//                        PdfWriter.getInstance(mDoc, new FileOutputStream(filPath));
//                        mDoc.open();
//                        String pdfText = mResultEt.getText().toString().replaceAll("\n", " ");
//                        mDoc.add(new Paragraph(pdfText));
//                        mDoc.close();
//
//                        postMenuDialog = new BottomSheetDialog(OcrResultActivity.this);
//                        postMenuDialog.setContentView(R.layout.dialog_share_pdf);
//                        postMenuDialog.setCanceledOnTouchOutside(TRUE);
//                        postMenuDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        postMenuDialog.findViewById(R.id.share)
//                                .setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        File file = new File(filPath);
//                                        if (file.exists()) {
//                                            path = FileProvider.getUriForFile(
//                                                    OcrResultActivity.this,
//                                                    "com.applex.snaplingo.fileprovider",
//                                                    file);
//                                            Intent intent = new Intent();
//                                            intent.setAction(Intent.ACTION_SEND);
//                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
//                                            intent.putExtra(Intent.EXTRA_STREAM, path);
//                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                            intent.setType("Document/*");
//                                            startActivity(Intent.createChooser(intent, "SHARE"));
//
//                                        } else {
//                                            Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                        }
//                                        postMenuDialog.dismiss();
//
//                                    }
//                                });
//
//                        postMenuDialog.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                File file = new File(filPath);
//                                if (file.exists()) {
//                                    Uri uri = FileProvider.getUriForFile(
//                                            OcrResultActivity.this,
//                                            "com.applex.snaplingo.fileprovider",
//                                            file);
//
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                    intent.setDataAndType(uri, "application/pdf");
//                                    Intent newintent = Intent.createChooser(intent, "Open File");
//
//                                    try {
//                                        newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(newintent);
//
//                                    } catch (ActivityNotFoundException e) {
//                                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                                else {
//                                    Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                }
//
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        Objects.requireNonNull(postMenuDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        postMenuDialog.show();
//
//
//                    } catch (Exception e) {
//                        Toast.makeText(OcrResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                mydialogue.dismiss();
//                flmenu.close(true);
//            }
//        });
//        mydialogue.show();
//
//    }
//
//    private void saveDoc(){
//
//        TextView save;
//        final EditText fileName;
//        TextView ext ;
//
//        mydialogue = new Dialog(OcrResultActivity.this);
//        mydialogue.setContentView(R.layout.dialog_file_name);
//        mydialogue.setCanceledOnTouchOutside(TRUE);
//        mydialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ext =  mydialogue.findViewById(R.id.extension);
//        ext.setText(getString(R.string.txt));
//
//        save=  mydialogue.findViewById(R.id.save);
//        fileName =  mydialogue.findViewById(R.id.fname);
//        final String fName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
//        fileName.setHint(fName);
//
//        save.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                if(fileName.length()!=0){
//                    final String filName=fileName.getText().toString().trim().replaceAll(" ","_")+".txt";
//                    String filPath;
//                    FileOutputStream fos= null;
//
//                    try {
//                        String docText =mResultEt.getText().toString().replaceAll("\n"," ");
//                        File f=  new File(Environment.getExternalStorageDirectory()+"/SnapLingo","Text doc");
//                        f.mkdirs();
//                        final File file = new File(Environment.getExternalStorageDirectory()+"/SnapLingo/Text doc",filName);
//                        fos= new FileOutputStream(file);
//                        fos.write(docText.getBytes());
//                        fos.close();
//                        filPath = getFilesDir()+"/"+filName;
//
//                        final String finalFilPath = filPath;
//
//                        postMenuDialog = new BottomSheetDialog(OcrResultActivity.this);
//                        postMenuDialog.setContentView(R.layout.dialog_share_doc);
//                        postMenuDialog.setCanceledOnTouchOutside(TRUE);
//                        postMenuDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        postMenuDialog.findViewById(R.id.share)
//                                .setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if (file.exists()) {
//                                            path = FileProvider.getUriForFile(
//                                                    OcrResultActivity.this,
//                                                    "com.applex.snaplingo.fileprovider",
//                                                    file);
//                                            Intent intent = new Intent();
//                                            intent.setAction(Intent.ACTION_SEND);
//                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
//                                            intent.putExtra(Intent.EXTRA_STREAM, path);
//                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                            intent.setType("Document/*");
//                                            startActivity(Intent.createChooser(intent, "SHARE"));
//
//                                        } else {
//                                            Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                        }
//                                        postMenuDialog.dismiss();
//
//                                    }
//                                });
//
//                        postMenuDialog.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (file.exists()) {
//                                    Uri uri = FileProvider.getUriForFile(
//                                            OcrResultActivity.this,
//                                            "com.applex.snaplingo.fileprovider",
//                                            file);
//
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                    intent.setData(uri);
//                                    Intent newintent = Intent.createChooser(intent, "Open File");
//
//                                    try {
//                                        newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(newintent);
//
//                                    } catch (ActivityNotFoundException e) {
//                                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                                else {
//                                    Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                }
//
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        Objects.requireNonNull(postMenuDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        postMenuDialog.show();
//
////                        Snackbar snackbar = Snackbar.make(relativeLayout , "Doc. has been saved", Snackbar.LENGTH_LONG)
////                                .setAction("Share", new View.OnClickListener() {
////
////                                    public void onClick(View v) {
////                                        if(file.exists()) {
////                                            path = FileProvider.getUriForFile(
////                                                    TextEditorActivity.this,
////                                                    "com.applex.snaplingo.fileprovider",
////                                                    file);
////                                            Intent intent = new Intent();
////                                            intent.setAction(Intent.ACTION_SEND);
////                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
////                                            intent.putExtra(Intent.EXTRA_STREAM, path);
////                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                                            intent.setType("Document/*");
////                                            startActivity(Intent.createChooser(intent, "SHARE"));
////
////                                        }
////                                        else{
////                                            Toast.makeText(TextEditorActivity.this,filName+" missing "+ finalFilPath,Toast.LENGTH_LONG).show();
////                                        }
////                                    }
////                                });
////                        snackbar.show();
//
//                    }
//                    catch (Exception e){
//                        Toast.makeText(OcrResultActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                    finally {
//                        if(fos!=null){
//                            try {
//                                fos.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                else{
//                    final String filName=fName+".txt";
//
//                    String filPath;
//                    FileOutputStream fos= null;
//
//                    try {
//                        String pdfText =mResultEt.getText().toString().replaceAll("\n"," ");
//
//                        File f=  new File(Environment.getExternalStorageDirectory()+"/SnapLingo","Text doc");
//                        f.mkdirs();
//                        final File file = new File(Environment.getExternalStorageDirectory()+"/SnapLingo/Text doc",filName);
//
//                        fos= new FileOutputStream(file);
//                        fos.write(pdfText.getBytes());
//                        fos.close();
//                        filPath = getFilesDir()+"/"+filName;
//
//                        final String finalFilPath = filPath;
//
//
//                        postMenuDialog = new BottomSheetDialog(OcrResultActivity.this);
//                        postMenuDialog.setContentView(R.layout.dialog_share_doc);
//                        postMenuDialog.setCanceledOnTouchOutside(TRUE);
//                        postMenuDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        postMenuDialog.findViewById(R.id.share)
//                                .setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if (file.exists()) {
//                                            path = FileProvider.getUriForFile(
//                                                    OcrResultActivity.this,
//                                                    "com.applex.snaplingo.fileprovider",
//                                                    file);
//                                            Intent intent = new Intent();
//                                            intent.setAction(Intent.ACTION_SEND);
//                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
//                                            intent.putExtra(Intent.EXTRA_STREAM, path);
//                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                            intent.setType("Document/*");
//                                            startActivity(Intent.createChooser(intent, "SHARE"));
//
//                                        } else {
//                                            Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                        }
//
//                                        postMenuDialog.dismiss();
//
//                                    }
//                                });
//
//                        postMenuDialog.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (file.exists()) {
//                                    Uri uri = FileProvider.getUriForFile(
//                                            OcrResultActivity.this,
//                                            "com.applex.snaplingo.fileprovider",
//                                            file);
//
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                    intent.setData(uri);
//                                    Intent newintent = Intent.createChooser(intent, "Open File");
//
//                                    try {
//                                        newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(newintent);
//
//                                    } catch (ActivityNotFoundException e) {
//                                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                                else {
//                                    Toast.makeText(OcrResultActivity.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
//                                }
//
//                                postMenuDialog.dismiss();
//                            }
//                        });
//
//                        Objects.requireNonNull(postMenuDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        postMenuDialog.show();
//
//
//                    }
//                    catch (Exception e){
//                        Toast.makeText(OcrResultActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                    finally {
//                        if(fos!=null){
//                            try {
//                                fos.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                mydialogue.dismiss();
//                flmenu.close(true);
//            }
//        });
//        mydialogue.show();
//
//    }
//
//    private void GenerateQR(String string) {
//        mydialogue = new Dialog(OcrResultActivity.this);
//        mydialogue.setContentView(R.layout.qrdialog);
//        mydialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ImageView qrImagevew = mydialogue.findViewById(R.id.qrImageview);
//        ImageView qrshare = mydialogue.findViewById(R.id.shareQr);
//
//        QRGEncoder qrgEncoder;
//        WindowManager manager=(WindowManager)getSystemService(WINDOW_SERVICE);
//        Display display = manager.getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        display.getSize(point);
//        int width=point.x;
//        int height=point.y;
//        int smallerdimension = width<height ? width:height;
//        qrgEncoder = new QRGEncoder(string, null, QRGContents.Type.TEXT, smallerdimension);
//
//        try {
//            bitmap = qrgEncoder.encodeAsBitmap();
//            qrImagevew.setImageBitmap(bitmap);
//            flmenu.close(true);
//
//        }
//        catch (WriterException e) {
//
//        }
//        mydialogue.show();
//        qrshare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("*/*");
//
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
//                        bitmap, String.valueOf(System.currentTimeMillis()), null);
//                Uri imageUri =  Uri.parse(path);
//                share.putExtra(Intent.EXTRA_TEXT, "Generate QRs with SnapLingo! If you haven't downloaded yet, click here: https://play.google.com/store/apps/details?id=com.applex.snaplingo ");
//                share.putExtra(Intent.EXTRA_STREAM, imageUri);
//                startActivity(Intent.createChooser(share, "Share QR"));
//
//            }
//        });
//    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//                Toast.makeText(getApplicationContext(), "here", Toast.LENGTH_SHORT).show();
                resultUri = result.getUri();
                pv.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) pv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        if (i != items.size() - 1) {
                            sb.append("\n");
                        }

                    }
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "+error", Toast.LENGTH_SHORT).show();
        }

    }


    //////////////DATABASE ACTIONS//////////////////

//    public void AddData(String newEntry,String aDate){
//        boolean insertData = myDB.addData2(newEntry,aDate);
//        if(!insertData){
//            Toast.makeText(OcrResultActivity.this,"Something went wrong :(",Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    public void databaseAdder(int i) {
//        itemID=-1;
//        String string = mResultEt.getText().toString().replaceAll("\'","\'\'");
//        if(sel==3){
//            string = getIntent().getStringExtra("name").replaceAll("\'","\'\'");
//        }
//        Cursor cdata = myDB.getItemId(string);
//        while (cdata.moveToNext()) {
//            itemID = cdata.getInt(0);
//        }
//        if (itemID == -1 && sel != 3) {
//            Calendar calendar = Calendar.getInstance();
//            String currDate = DateFormat.getDateInstance().format(calendar.getTime());
//            AddData(mResultEt.getText().toString(), currDate);
//            string = mResultEt.getText().toString().replaceAll("\'","\'\'");
//            cdata = myDB.getItemId(string);
//            while (cdata.moveToNext()) {
//                itemID = cdata.getInt(0);
//            }
//        } else if (itemID > -1 && i == 1) {
//
//            Calendar calendar = Calendar.getInstance();
//            String currDate = DateFormat.getDateInstance().format(calendar.getTime());
//            myDB.deleteItem(itemID);
//            AddData(mResultEt.getText().toString(), currDate);
//            string = mResultEt.getText().toString().replaceAll("\'","\'\'");
//            cdata = myDB.getItemId(string);
//            while (cdata.moveToNext()) {
//                itemID = cdata.getInt(0);
//            }
//
//        }
//
//    }

    //////////////DATABASE ACTIONS//////////////////

    ////////////////////MENU/////////////////////

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){

            if(isTaskRoot()){
                startActivity(new Intent(OcrResultActivity.this, MainActivity.class));
            }
            else {
                super.onBackPressed();
            }
        }
//        else if (id == R.id.share) {
//            if (mResultEt.length() > 0) {
//                Intent shareintent = new Intent();
//                shareintent.setAction(Intent.ACTION_SEND);
//                String pdfText =mResultEt.getText().toString();
//                shareintent.putExtra(Intent.EXTRA_TEXT, pdfText);
//                shareintent.setType("text/plain");
//                startActivity(shareintent);
//
//            } else {
//                Toast.makeText(OcrResultActivity.this, "No text found :(", Toast.LENGTH_SHORT).show();
//            }
//
//        }
        return super.onOptionsItemSelected(item);
    }



    public void onBackPressed() {

        if(isTaskRoot()){
            startActivity(new Intent(OcrResultActivity.this, MainActivity.class));
        }
        else {
            super.onBackPressed();
        }


    }


    public void onPause() {
        super.onPause();

    }


    protected void onResume() {
        super.onResume();

    }


    protected void onStart() {
        super.onStart();
        if(sel == 1){
            if(resultUri== null){
                Toast.makeText(OcrResultActivity.this,"Image not Detected...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OcrResultActivity.this, MainActivity.class));
            }
        }

    }
}