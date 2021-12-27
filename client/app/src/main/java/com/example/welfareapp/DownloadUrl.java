package com.example.welfareapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import noman.googleplaces.PlaceType;

public class DownloadUrl
{
    public void ReadUrl(String url){
        try{
            JsonObjectRequest urlData = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        Log.v("MapActivity url test ",response.toString());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Log.v("MapActivity url test Exception error", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.v("MapActivity url test Volley Error", error.toString());
                }
            });
            urlData.setShouldCache(false);
            AppHelper.requestQueue.add(urlData);
        }
        catch(Exception err){
            err.printStackTrace();
            Log.e("MapActivity download read the url error occur", err.getMessage());
        }

    }
    public String getUrl(String placeType, int searchRadius, String placeAPIKey, LatLng position){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        double latitide = position.latitude;
        double longitude = position.longitude;
        googleURL.append("location=" + latitide + "," + longitude);
        googleURL.append("&radius=" + searchRadius);
        googleURL.append("&type=" + placeType);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + placeAPIKey);
        googleURL.append("&rankby=distance");
        Log.d("MapActivity DownloadUrl class", "url = " + googleURL.toString());
        return googleURL.toString();
    }
}