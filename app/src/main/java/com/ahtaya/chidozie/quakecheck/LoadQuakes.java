package com.ahtaya.chidozie.quakecheck;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


class LoadQuakes extends AsyncTaskLoader<ArrayList<EarthQuake>> {

    String mQuake_url;
    LoadQuakes(Context context, String quake_url) {
        super(context);
        mQuake_url = quake_url;
    }

    @Override
    public ArrayList<EarthQuake> loadInBackground() {
        ArrayList<EarthQuake> earthQuakes = new ArrayList<>();
        String jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream;

        try {
            URL url = new URL(mQuake_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                earthQuakes = ExtractQuakes(jsonResponse);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return earthQuakes;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    private ArrayList<EarthQuake> ExtractQuakes(String jsonResponse) throws JSONException {

        ArrayList<EarthQuake> earthQuakes = new ArrayList<>();

        JSONObject rootObject = new JSONObject(jsonResponse);
        JSONArray featuresArray = rootObject.getJSONArray("features");
        for (int i = 0; i < featuresArray.length(); i++){
            JSONObject featuresObject = featuresArray.getJSONObject(i);
            JSONObject propertiesObject = featuresObject.getJSONObject("properties");
            String mag = propertiesObject.getString("mag");
            String place = propertiesObject.getString("place");
            String time = propertiesObject.getString("time");
            Date date = new Date(Long.parseLong(time));
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            String myDate = dateFormat.format(date) +"\n" + timeFormat.format(date);
            earthQuakes.add(new EarthQuake(mag, place, myDate));
        }
        return earthQuakes;
//        QuakeAdapter quakeAdapter = new QuakeAdapter(this, earthQuakes);
//        listView.setAdapter(quakeAdapter);
    }
}
