package com.batsworks.simplewebview.observable;

import android.os.AsyncTask;
import com.batsworks.simplewebview.model.YoutubeModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request extends AsyncTask<String, Integer, Object> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected Object doInBackground(String... strings) {
        try {
            String json = null;

            URL urlLink = new URL(strings[0]);

            HttpURLConnection connection = (HttpURLConnection) urlLink.openConnection();
            System.out.println(connection.getResponseCode());

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
            String current;
            while ((current = reader.readLine()) != null) {
                if (current.contains("format")) {
                    json += current;
                }
            }

            if (json == null)
                throw new RuntimeException();

            json = json.substring(json.indexOf("streamingData"), json.indexOf("captions"));
            json = json.substring(0, json.indexOf("playbackTracking") - 2);

            if (json.indexOf("playerAds") > 0) {
                json = json.substring(0, json.indexOf("playerAds") - 2);
            }
            json = json.substring(json.indexOf("{"));

            json = json.replace("\\u0026", "&");

            return mapper.readValue(json, YoutubeModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
