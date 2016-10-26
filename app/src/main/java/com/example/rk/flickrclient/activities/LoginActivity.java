package com.example.rk.flickrclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.rk.flickrclient.R;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RK on 10/24/2016.
 */
public class LoginActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        webView = (WebView) findViewById(R.id.mainWebView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final LoginTask loginTask = new LoginTask();
        loginTask.execute();

    }

    private class LoginTask extends AsyncTask<Void, Void, OAuthToken> {

        @Override
        protected OAuthToken doInBackground(Void... params) {
            OAuthToken oAuthToken = null;
            try {
                oAuthToken = FlickClient.getFlickrInstance().getOAuthInterface().getRequestToken("http://flickrClient.com/test.php");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            }
            return oAuthToken;
        }

        @Override
        protected void onPostExecute(final OAuthToken oAuthToken) {
            super.onPostExecute(oAuthToken);

            try {
                URL url = FlickClient.getFlickrInstance().getOAuthInterface().buildAuthenticationUrl(Permission.WRITE, oAuthToken);
                webView.loadUrl(url.toString());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        webView.setVisibility(View.VISIBLE);
                        if ( url.contains("http://flickrclient.com/test.php")) {
                            Map<String, String> queryMap = getQueryMap(url);

                            AccessTokenTask accessTokenTask = new AccessTokenTask();
                            accessTokenTask.execute(oAuthToken.getOauthToken(), oAuthToken.getOauthTokenSecret(), queryMap.get("oauth_verifier"));
                        }
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private class AccessTokenTask extends AsyncTask<String, Void, OAuth> {

        @Override
        protected OAuth doInBackground(String... params) {
            OAuth oAuth = null;
            try {
                oAuth = FlickClient.getFlickrInstance().getOAuthInterface().getAccessToken(params[0], params[1], params[2]);
            } catch (IOException | FlickrException e) {
                e.printStackTrace();
            }
            return oAuth;
        }

        @Override
        protected void onPostExecute(OAuth oAuth) {
            super.onPostExecute(oAuth);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", oAuth);
            setResult(RESULT_OK,resultIntent);
            LoginActivity.this.finish();
        }
    }

    private Map<String, String> getQueryMap(String url) {
        Log.d("querymap", url);
        String[] params = url.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }

        return map;
    }


}
