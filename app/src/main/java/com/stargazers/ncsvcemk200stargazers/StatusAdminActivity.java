package com.stargazers.ncsvcemk200stargazers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stargazers.ncsvcemk200stargazers.adapters.AdminAdapter;
import com.stargazers.ncsvcemk200stargazers.adapters.StatusAdapter;
import com.stargazers.ncsvcemk200stargazers.models.ApplicationModel;
import com.stargazers.ncsvcemk200stargazers.models.StatusModel;

import java.util.ArrayList;

public class StatusAdminActivity extends AppCompatActivity {

    private TextView existingDetails;
    private RecyclerView progressList;
    ApplicationModel applicationModel;
    private LinearLayoutManager layoutManager;

    private StatusAdapter statusAdapter;

    String appID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_admin);

        //existing
        progressList = findViewById(R.id.progressList);
        existingDetails = findViewById(R.id.applicationDetails);

        if(getIntent().getStringExtra("applicationID") != null) {
            appID = getIntent().getStringExtra("applicationID");
        }
        buildRecyclerView();
    }

    private void buildRecyclerView() {
        layoutManager = new LinearLayoutManager(StatusAdminActivity.this);
        progressList.setLayoutManager(layoutManager);
        FirebaseFirestore.getInstance().collection("Applications/"+ appID+"/Statuses")
                .orderBy("timestamp")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<StatusModel> statuses = new ArrayList<>();

                for(QueryDocumentSnapshot document : task.getResult()) {
                    StatusModel status = document.toObject(StatusModel.class);
                    statuses.add(status);
                }

                statusAdapter = new StatusAdapter(statuses, StatusAdminActivity.this);
                progressList.setAdapter(statusAdapter);
            }
        });
    }
}