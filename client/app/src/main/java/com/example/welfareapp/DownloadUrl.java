package com.example.welfareapp;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl
{
    public String ReadTheURL(String placeURL) throws IOException
    {
        String Data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try
        {
            URL url = new URL(placeURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuffer.append(line);
            }

            Data = stringBuffer.toString();
            bufferedReader.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            inputStream.close();
            httpURLConnection.disconnect();
        }

        return Data;
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
        Log.d("MapActivity DownloadUrl class", "url = " + googleURL.toString());
        return googleURL.toString();
    }
}