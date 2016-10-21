package com.example.rk.flickrclient.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rk.flickrclient.R;
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
    private PhotoList photos;
    private List<String> photoUrls = new ArrayList<>();

    public PhotoAdapter(PhotoList zipCodes) {
        this.photos = zipCodes;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"on create view");
        PhotoViewHolder viewHolder;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View itemView = inflater.inflate(R.layout.itemview_layout, parent, false);
        viewHolder = new PhotoViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        final String photoUrl = photos.get(position).getUrl();
        holder.imageView.setImageResource(android.R.color.transparent);
        Picasso.with(FlickrClientApplication.getInstance())
                .load(photoUrl)
                .resize(50, 50) // here you resize your image to whatever width and height you like
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setPhotoList(PhotoList photoList) {
        photos.clear();
        photos.addAll(photoList);
        notifyDataSetChanged();

    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
ImageView imageView;

        PhotoViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imageView = (ImageView) itemLayoutView.findViewById(R.id.ivPhoto);
        }
    }
}
