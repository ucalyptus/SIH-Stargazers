package com.stargazers.ncsvcemk200stargazers.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stargazers.ncsvcemk200stargazers.R;
import com.stargazers.ncsvcemk200stargazers.models.StatusModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ImageViewHolder>{

    private ArrayList<StatusModel> mList;
    private Context mContext;

    public StatusAdapter(ArrayList<StatusModel> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_status, parent,false);
        return new ImageViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        StatusModel currentItem = mList.get(position);

        if(position == mList.size()-1){
            holder.down.setVisibility(View.GONE);
        }
        else {
            holder.down.setVisibility(View.VISIBLE);

        }

        if(position == 0){
            holder.up.setVisibility(View.GONE);
        }
        else {
            holder.up.setVisibility(View.VISIBLE);
        }

        holder.count.setText(Integer.toString(position+1));

        holder.stageName.setText(currentItem.getStageName());
        SimpleDateFormat sfd = new SimpleDateFormat("dd MMMM, yyyy");
        String date = sfd.format(currentItem.getTimestamp().toDate());
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        TextView stageName, date, count;
        View up, down;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.number);
            stageName = itemView.findViewById(R.id.stage);
            date = itemView.findViewById(R.id.date);

            up = itemView.findViewById(R.id.up);
            down = itemView.findViewById(R.id.down);

        }
    }
}
