package com.example.rk.flickrclient.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rk.flickrclient.R;
import com.example.rk.flickrclient.application.FlickrClientApplication;
import com.google.gson.Gson;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthTokenParameter;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;

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
    Button postButton;
    Photo photo;
    private OAuth oauth;
    EditText editText;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_detail);
        cardView = (CardView) findViewById(R.id.card_view);
        title = (TextView) findViewById(R.id.title);
        favs = (TextView) findViewById(R.id.favs);
        comments = (TextView) findViewById(R.id.comments);
        user = (TextView) findViewById(R.id.usrName);
        imageView = (ImageView) findViewById(R.id.photoDetail);
        editText = (EditText) findViewById(R.id.commentField);
        postButton = (Button) findViewById(R.id.postButton);

    }


    @Override
    protected void onResume() {
        super.onResume();
        imageView.setMinimumHeight(1500);

        photo = (Photo) getIntent().getExtras().get("photo");

        Picasso.with(getBaseContext()).load(photo.getMediumUrl()).fit().into(imageView);
        if (title != null) {
            title.setText(photo.getTitle());
        }
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCommentTask addCommentTask = new AddCommentTask();
                addCommentTask.execute();
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (sharedPreferences.contains("result")) {
                                                editText.setFocusableInTouchMode(true);
                                                editText.setFocusable(true);
                                                postButton.setEnabled(true);
                                            } else {
                                                addLoginDialog();
                                            }
                                        }
                                    }
        );
        PersonInfoTask personInfoTask = new PersonInfoTask();
        personInfoTask.execute(photo.getId());
    }

    private void addLoginDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Sign in to your account to comment.");
        dialogBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), 1);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private class PersonInfoTask extends AsyncTask<String, Void, Photo> {

        @Override
        protected Photo doInBackground(String... params) {
            Photo photo = null;
            try {
                photo = FlickClient.getFlickrInstance().getPhotosInterface().getInfo(params[0], "bac6151a238ab0df");
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
            Log.e("name",photo.getOwner().getRealName());
            user.setText(photo.getOwner().getRealName().toUpperCase());
            int favoritesCount = photo.getFavorites() == -1 ? 0 : photo.getFavorites();
            favs.setText(String.valueOf(favoritesCount) + " Favs");
            int commentsCount = photo.getComments() == -1 ? 0 : photo.getComments();
            comments.setText(String.valueOf(commentsCount) + " Comments");
        }
    }

    private class AddCommentTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String user = null;
            try {
                OAuth oAuth = new Gson().fromJson(sharedPreferences.getString("result", null), OAuth.class);
                RequestContext.getRequestContext().setOAuth(oAuth);
                user = FlickClient.getFlickrInstance().getCommentsInterface().addComment(photo.getId(), editText.getText().toString());
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
            if (user != null) {
                editText.clearFocus();
                editText.setText(null);
                Toast.makeText(getApplicationContext(), "Comment successfully posted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Comment cannot be posted due to server error.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                oauth = (OAuth) data.getExtras().get("result");
                String jsonString = new Gson().toJson(oauth);
                sharedPreferences.edit().putString("result", jsonString).apply();
            }
        }
    }
}
