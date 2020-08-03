package com.stargazers.ncsvcemk200stargazers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stargazers.ncsvcemk200stargazers.adapters.AdminAdapter;
import com.stargazers.ncsvcemk200stargazers.models.ApplicationModel;

import java.util.ArrayList;

public class AdminPanelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;

    private ProgressBar progressBar;
    private RecyclerView progressList;
    private LinearLayoutManager layoutManager;
    private AdminAdapter adminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
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


        //existing
        progressList = findViewById(R.id.progressList);
        buildRecyclerView();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id =item.getItemId();

        if(id==R.id.about_us){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(AdminPanelActivity.this, AboutUs.class);
                    startActivity(intent);
                }
            },200);
        }

        if(id==R.id.log_out){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminPanelActivity.this);
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
                                                Intent intent = new Intent(AdminPanelActivity.this, PickAccountTypeActivity.class);
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


//    private void checkStatus() {
//        FirebaseFirestore.getInstance().document("Applications/" + FirebaseAuth.getInstance().getUid())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    if(task.getResult().exists()){
//                        newAwaas.setVisibility(View.GONE);
//                        existing.setVisibility(View.VISIBLE);
//                        applicationModel = new ApplicationModel();
//                        applicationModel = task.getResult().toObject(ApplicationModel.class);
//                        applicationModel.setApplicationID(task.getResult().getId());
//                        SimpleDateFormat sfd = new SimpleDateFormat("dd MMMM, yyyy");
//                        String date = sfd.format(applicationModel.getTimestamp().toDate());
//                        existingDetails.setText("Name : "+applicationModel.getAadhaarModel().getName()+"\nAadhaar ID : "+applicationModel.getAadhaarModel().getUid()+"\n\nDate : "+date);
//                        buildRecyclerView();
//                    }
//                    else {
//                        newAwaas.setVisibility(View.VISIBLE);
//                        existing.setVisibility(View.GONE);
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }
//                else {
//                    newAwaas.setVisibility(View.VISIBLE);
//                    existing.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//    }

    private void buildRecyclerView() {
        layoutManager = new LinearLayoutManager(AdminPanelActivity.this);
        progressList.setLayoutManager(layoutManager);
        FirebaseFirestore.getInstance().collection("Applications")
                .orderBy("timestamp")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<ApplicationModel> applications = new ArrayList<>();

                for(QueryDocumentSnapshot document : task.getResult()) {
                    ApplicationModel applicationModel = document.toObject(ApplicationModel.class);
                    applications.add(applicationModel);
                }

                adminAdapter = new AdminAdapter(applications, AdminPanelActivity.this);
                progressList.setAdapter(adminAdapter);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildRecyclerView();
//        checkStatus();
    }
}