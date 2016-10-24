package com.example.rk.flickrclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rk.flickrclient.R;
import com.googlecode.flickrjandroid.photos.Photo;
import com.squareup.picasso.Picasso;

/**
 * Created by RK on 10/22/2016.
 */
public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        CardView cardView = (CardView) findViewById(R.id.card_view);
        TextView title = (TextView) findViewById(R.id.title);
        TextView favs = (TextView) findViewById(R.id.favs);
        TextView comments =  (TextView) findViewById(R.id.comments);

        ImageView imageView = (ImageView) findViewById(R.id.photoDetail);
        Photo photo = (Photo) getIntent().getExtras().get("photo");

        Picasso.with(getBaseContext()).load(photo.getMediumUrl()).resize(cardView.getWidth(),1500).into(imageView);
        if (title != null) {
            title.setText(photo.getTitle());
        }
        favs.setText("Favs");
        comments.setText("Comments");
        photo.getOwner().getUsername();
        photo.getComments();
        photo.getFavorites();
        photo.getComments();

    }
}
