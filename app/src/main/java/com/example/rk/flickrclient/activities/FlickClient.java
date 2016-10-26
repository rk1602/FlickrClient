package com.example.rk.flickrclient.activities;

import com.googlecode.flickrjandroid.Flickr;

/**
 * Created by RK on 10/24/2016.
 */
public class FlickClient {
    private static Flickr flickr ;
    private static String api_key = "cbd86b000b9fa0af1de7bf7c82fcc051";
    private static String secret =  "bac6151a238ab0df";

    public static Flickr getFlickrInstance(){
        if(flickr == null ){
            return new Flickr(api_key,secret);
        }
        return flickr;

    }

    public static String getApi_key() {
        return api_key;
    }

    public static String getSecret() {
        return secret;
    }
}
