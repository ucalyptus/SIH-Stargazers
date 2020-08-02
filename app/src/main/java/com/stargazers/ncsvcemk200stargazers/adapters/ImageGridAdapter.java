package com.stargazers.ncsvcemk200stargazers.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.stargazers.ncsvcemk200stargazers.R;

import java.util.ArrayList;

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder>{

    private ArrayList<String> mList;
    private Context mContext;
    private ImageGridAdapter.OnClickListener mListener;

    public ImageGridAdapter(ArrayList<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;

    }

    public interface OnClickListener {
        void onClickListener(int position);
    }

    public void onClickListener(ImageGridAdapter.OnClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid, parent,false);
        return new ImageViewHolder(v, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        String imageUri = mList.get(position);

        ViewTreeObserver vto = holder.image.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                holder.image.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight = holder.image.getMeasuredHeight();
                int finalWidth = holder.image.getMeasuredWidth();
                Picasso.get().load(Uri.parse(imageUri))
                        .resize(finalWidth, finalHeight)
                        .placeholder(R.drawable.image_background_grey)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .priority(Picasso.Priority.HIGH)
                        .centerCrop().into(holder.image);
                return true;
            }
        });

//        holder.image.setOnClickListener(v -> {
//            Intent intent= new Intent(mContext, ViewPager.class);
//            intent.putExtra("from", "2"); //from grid adapter
//            intent.putExtra("docName", ActivityGrid.document_name);
//            intent.putExtra("pos", String.valueOf(position));
//            mContext.startActivity(intent);
//        });

        holder.count.setText(Integer.toString(position+1));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView count;
        ImageButton delete;
        CardView grid_page;

        public ImageViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            count = itemView.findViewById(R.id.count);
            image = itemView.findViewById(R.id.grid_image);
            delete = itemView.findViewById(R.id.delete_image);
            grid_page = itemView.findViewById(R.id.grid_page);

            delete.setOnClickListener(v -> {
                if(onClickListener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION ){
                        onClickListener.onClickListener(position);
                    }
                }
            });
        }
    }
}
