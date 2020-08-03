package com.stargazers.ncsvcemk200stargazers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stargazers.ncsvcemk200stargazers.adapters.StatusAdapter;
import com.stargazers.ncsvcemk200stargazers.models.ApplicationModel;
import com.stargazers.ncsvcemk200stargazers.models.StatusModel;
import com.thekhaeng.pushdownanim.PushDownAnim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    private CardView newRecord;

    private NestedScrollView newAwaas, existing;

    private ProgressBar progressBar;


    private TextView existingDetails;
    private RecyclerView progressList;
    ApplicationModel applicationModel;
    private LinearLayoutManager layoutManager;
    private StatusAdapter statusAdapter;
    private Button newStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Awaas App");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        navigationView.setNavigationItemSelectedListener(this);

        IntroPref introPref = new IntroPref(this);

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        newAwaas = findViewById(R.id.newAwaas);
        existing = findViewById(R.id.existing);

        checkStatus();
        //new
        newRecord = findViewById(R.id.newRecord);
        PushDownAnim.setPushDownAnimTo(newRecord)
                .setScale(PushDownAnim.MODE_STATIC_DP, 6)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, NewBeneficiary1.class);
                    startActivity(intent);
                });
        //new

        //existing
        progressList = findViewById(R.id.progressList);
        existingDetails = findViewById(R.id.applicationDetails);
        newStatus = findViewById(R.id.newStatus);
        newStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatusUpdateActivity.class));
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id =item.getItemId();

        if(id==R.id.about_us){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(intent);
                }
            },200);
        }
//        if(id==R.id.privacy){
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(MainActivity.this, PrivacyPolicy.class);
////                    intent.putExtra("text","http://applex.in/elementor-50424/");
////                    intent.putExtra("bool","2");
//                    startActivity(intent);
//                }
//            },200);
//        }


        if(id==R.id.log_out){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Log out")
                            .setMessage("Do you want to continue?")
                            .setPositiveButton("Log out", (dialog, which) -> {
                                FirebaseFirestore.getInstance()
                                        .collection("Users/"+ FirebaseAuth.getInstance().getUid()+"/AccessToken/")
                                        .document("Token").delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(MainActivity.this, PickAccountTypeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                finish();
                                            }
                                        });

                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .setCancelable(true)
                            .show();
                }
            },200);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void checkStatus() {
        FirebaseFirestore.getInstance().document("Applications/" + FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        newAwaas.setVisibility(View.GONE);
                        existing.setVisibility(View.VISIBLE);
                        applicationModel = new ApplicationModel();
                        applicationModel = task.getResult().toObject(ApplicationModel.class);
                        applicationModel.setApplicationID(task.getResult().getId());
                        SimpleDateFormat sfd = new SimpleDateFormat("dd MMMM, yyyy");
                        String date = sfd.format(applicationModel.getTimestamp().toDate());
                        existingDetails.setText("Application ID :\n"+applicationModel.getApplicationID()+"\n\nDate : "+date);
                        buildRecyclerView();
                    }
                    else {
                        newAwaas.setVisibility(View.VISIBLE);
                        existing.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    newAwaas.setVisibility(View.VISIBLE);
                    existing.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void buildRecyclerView() {
        layoutManager = new LinearLayoutManager(MainActivity.this);
        progressList.setLayoutManager(layoutManager);
        FirebaseFirestore.getInstance().collection("Applications/"+FirebaseAuth.getInstance().getUid()+"/Statuses")
                .orderBy("timestamp")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<StatusModel> statuses = new ArrayList<>();

                for(QueryDocumentSnapshot document : task.getResult()) {
                    StatusModel status = document.toObject(StatusModel.class);
                    statuses.add(status);
                }

                statusAdapter = new StatusAdapter(statuses, MainActivity.this);
                progressList.setAdapter(statusAdapter);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus();
    }
}