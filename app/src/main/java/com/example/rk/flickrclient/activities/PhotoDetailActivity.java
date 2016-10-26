package com.example.rk.flickrclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rk.flickrclient.R;
import com.example.rk.flickrclient.application.FlickrClientApplication;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthTokenParameter;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;

/**
 * Created by RK on 10/22/2016.
 */
public class PhotoDetailActivity extends AppCompatActivity {
    private CardView cardView;
    private TextView title;
    private TextView favs;
    private TextView comments;
    private TextView user;
    private ImageView imageView;
    Button loginButton;
    Photo photo;
    private OAuth oauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        cardView = (CardView) findViewById(R.id.card_view);
        title = (TextView) findViewById(R.id.title);
        favs = (TextView) findViewById(R.id.favs);
        comments = (TextView) findViewById(R.id.comments);
        user = (TextView) findViewById(R.id.usrName);
        imageView = (ImageView) findViewById(R.id.photoDetail);
        loginButton = (Button) findViewById(R.id.loginButton);
    }


    @Override
    protected void onResume() {
        super.onResume();
         photo = (Photo) getIntent().getExtras().get("photo");

        Picasso.with(getBaseContext()).load(photo.getMediumUrl()).resize(cardView.getWidth(), 1500).into(imageView);
        if (title != null) {
            title.setText(photo.getTitle());
        }

        PersonInfoTask personInfoTask = new PersonInfoTask();
        personInfoTask.execute(photo.getId());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), 1);
            }
        });
    }

    private class PersonInfoTask extends AsyncTask<String, Void,Photo>{

        @Override
        protected Photo doInBackground(String... params) {
            Photo photo = null;
            try {
                photo = FlickClient.getFlickrInstance().getPhotosInterface().getInfo(params[0],"bac6151a238ab0df");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return photo;
        }

        @Override
        protected void onPostExecute(Photo photo) {
            super.onPostExecute(photo);
            user.setText(photo.getOwner().getRealName());
            int favoritesCount = photo.getFavorites() == -1 ? 0 : photo.getFavorites();
            favs.setText(String.valueOf(favoritesCount) + " Favs");
            int commentsCount = photo.getComments() == -1 ? 0 : photo.getComments();
            comments.setText(String.valueOf(commentsCount) + " Comments");
        }
    }

    private class AddCommentTask extends AsyncTask<Void, Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            String user = null;
            try {
                RequestContext.getRequestContext().setOAuth(oauth);
                user = FlickClient.getFlickrInstance().getCommentsInterface().addComment(photo.getId(),"!!!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user;
        }


        @Override
        protected void onPostExecute(String userInfo) {
            super.onPostExecute(userInfo);
            user.setText(userInfo);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                oauth = (OAuth)data.getExtras().get("result");
               loginButton.setVisibility(View.GONE);
                AddCommentTask addCommentTask = new AddCommentTask();
                addCommentTask.execute();
            }
        }
    }
}
