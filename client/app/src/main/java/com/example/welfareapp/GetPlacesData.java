package com.example.welfareapp;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GetPlacesData extends AsyncTask<Object, String, String> {
    private String googlePlaceData, url;
    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try{
            googlePlaceData = downloadUrl.ReadTheURL(url);
        }
        catch(IOException err){
            err.printStackTrace();
        }
        return googlePlaceData;
    }
    @Override
    protected void onPostExecute(String s)
    {
//        List<HashMap<String, String>> nearByPlacesList = null;
//        DataParser dataParser = new DataParser();
//        nearByPlacesList = dataParser.parse(s);
//        DisplayNearbyPlaces(nearByPlacesList);
    }
}