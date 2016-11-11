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
import android.text.TextUtils;
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
    int currentPage = 1;
    int totalPages;
    String query;
    boolean onGoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, getIntent().getAction());
        progressBar = (ProgressBar) findViewById(R.id.loading);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, photoList);
        recyclerView.setAdapter(photoAdapter);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
                int lastItem = linearLayoutManager.findLastVisibleItemPosition();
                if ((firstItem + lastItem) / 2 >= photoAdapter.getItemCount() - 75 && !onGoing) {
                    currentPage++;
                    Log.e(TAG, " count " + photoAdapter.getItemCount() + " " + currentPage+" "+totalPages);

                    if (currentPage <= totalPages) {
                        new FetchPhotosTask(query, currentPage).execute();
                    }
                }

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            currentPage = 1;
            totalPages = 0;
            photoList.clear();
            if (!query.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                new FetchPhotosTask(query, 1).execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                photoList.clear();
                totalPages = 0;
                currentPage = 1;
                query = "";
                photoAdapter.notifyDataSetChanged();

                return false;
            }
        });
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

    public PhotoList performRequest(final String query, int currentPage) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setText(query);
        PhotoList photos = new PhotoList();
        try {
            photos = FlickClient.getFlickrInstance().getPhotosInterface().search(searchParameters, 100, currentPage);
            totalPages = photos.getPages();
            Log.d(TAG, photos.toString());
        } catch (IOException | FlickrException | JSONException e) {
            e.printStackTrace();
        }
        return photos;
    }

    class FetchPhotosTask extends AsyncTask<Void, Void, PhotoList> {

        private final String query;
        private final int currentPage;

        FetchPhotosTask(String query, int currentPage) {
            this.query = query;
            this.currentPage = currentPage;
        }

        @Override
        protected PhotoList doInBackground(Void... params) {
            onGoing = true;
            return performRequest(query, currentPage);
        }

        @Override
        protected void onPostExecute(PhotoList photos) {
            super.onPostExecute(photos);
            Log.e(TAG, photos.toString());
            Log.e(TAG, String.valueOf(photos.getTotal()));
            onGoing = false;
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            photoList.addAll(photos);
            photoAdapter.notifyDataSetChanged();

        }
    }
}
