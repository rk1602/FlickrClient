package com.example.rk.flickrclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rk.flickrclient.R;
import com.example.rk.flickrclient.activities.PhotoDetailActivity;
import com.example.rk.flickrclient.application.FlickrClientApplication;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RK on 10/20/2016.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private static final String TAG = PhotoAdapter.class.getSimpleName();
    private Context context;
    private PhotoList photos;
    private List<String> photoUrls = new ArrayList<>();

    public PhotoAdapter(Context context, PhotoList photoList) {
        this.photos = photoList;
        this.context = context;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "on create view");
        PhotoViewHolder viewHolder;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View itemView = inflater.inflate(R.layout.itemview_layout, parent, false);
        viewHolder = new PhotoViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {
        final String photoUrl = photos.get(position).getMediumUrl();
        //BitmapFactory.decodeStream(pho).
        holder.imageView.setBackgroundColor(context.getResources().getColor(R.color.abc_background_cache_hint_selector_material_dark));
        Picasso.with(context)
                .load(photoUrl).resize(500, 500).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("photo", photos.get(position));

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        PhotoViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imageView = (ImageView) itemLayoutView.findViewById(R.id.ivPhoto);
        }
    }


}
