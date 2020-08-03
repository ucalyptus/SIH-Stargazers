package com.stargazers.ncsvcemk200stargazers.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stargazers.ncsvcemk200stargazers.AdminPanelActivity;
import com.stargazers.ncsvcemk200stargazers.R;
import com.stargazers.ncsvcemk200stargazers.StatusAdminActivity;
import com.stargazers.ncsvcemk200stargazers.models.ApplicationModel;
import com.stargazers.ncsvcemk200stargazers.models.StatusModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ImageViewHolder>{

    private ArrayList<ApplicationModel> mList;
    private Context mContext;

    public AdminAdapter(ArrayList<ApplicationModel> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent,false);
        return new ImageViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        ApplicationModel currentItem = mList.get(position);

        holder.name.setText(currentItem.getAadhaarModel().getName());
        holder.id.setText(currentItem.getAadhaarModel().getUid());
        holder.address.setText(currentItem.getAadhaarModel().getBuildingNo()+" "+ currentItem.getAadhaarModel().getStreet()+" ,"+currentItem.getAadhaarModel().getVtc()
                    +" ,"+currentItem.getAadhaarModel().getState() +" \nPin : "+currentItem.getAadhaarModel().getPin());

        SimpleDateFormat sfd = new SimpleDateFormat("dd MMMM, yyyy");
        String date = sfd.format(currentItem.getTimestamp().toDate());
        holder.date.setText(date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StatusAdminActivity.class);
                intent.putExtra("applicationID", currentItem.getApplicationID());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        TextView date, name, id, address;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.id);
            date = itemView.findViewById(R.id.date);
            address = itemView.findViewById(R.id.address);

        }
    }
}
