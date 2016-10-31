package com.example.rk.flickrclient.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rk.flickrclient.R;
import com.example.rk.flickrclient.adapters.PhotoAdapter;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;
    final String apiKey = "cbd86b000b9fa0af1de7bf7c82fcc051";
    PhotoAdapter photoAdapter;
    PhotoList photoList = new PhotoList();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, getIntent().getAction());
        progressBar = (ProgressBar) findViewById(R.id.loading);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, photoList);
        recyclerView.setAdapter(photoAdapter);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            photoList.clear();
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, query);
            progressBar.setVisibility(View.VISIBLE);
            new FetchPhotosTask().execute(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public PhotoList performRequest(final String query) {

        try {
            Flickr flickr = FlickClient.getFlickrInstance();
            OAuthToken authToken = flickr.getOAuthInterface().getRequestToken("flickrClient://callback");
           URL url =  flickr.getOAuthInterface().buildAuthenticationUrl(Permission.WRITE, authToken);
            OAuth oAuth = flickr.getOAuthInterface().getAccessToken(authToken.getOauthToken(),authToken.getOauthTokenSecret(),null);
            Log.e(TAG,oAuth.toString());
            Log.d(TAG,url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        }
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setText(query);
        PhotoList photos = new PhotoList();
        try {
            photos = FlickClient.getFlickrInstance().getPhotosInterface().search(searchParameters, 100, 1);
            Log.d(TAG, photos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return photos;
    }

    class FetchPhotosTask extends AsyncTask<String, Void, PhotoList> {

        @Override
        protected PhotoList doInBackground(String... params) {
            return performRequest(params[0]);
        }

        @Override
        protected void onPostExecute(PhotoList photos) {
            super.onPostExecute(photos);
            Log.e(TAG, photos.toString());
            Log.e(TAG, String.valueOf(photos.getTotal()));
           // retrievePhotos(photos);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            photoList.addAll(photos);
            photoAdapter.notifyDataSetChanged();

        }
    }
}
