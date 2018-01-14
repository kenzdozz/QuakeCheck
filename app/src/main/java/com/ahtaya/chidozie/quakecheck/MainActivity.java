package com.ahtaya.chidozie.quakecheck;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<EarthQuake>> {

    private static final String QUAKE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    ArrayList<EarthQuake> earthQuakes;
    ListView listView;
    QuakeAdapter quakeAdapter;

    ProgressBar progressBar;
    LinearLayout emptyList;
    LinearLayout noNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        earthQuakes = new ArrayList<>();

        quakeAdapter = new QuakeAdapter(this, earthQuakes);

        listView = findViewById(R.id.quake_list);
        progressBar = findViewById(R.id.loading_progress);
        emptyList = findViewById(R.id.empty_list);
        noNetwork = findViewById(R.id.no_network);
        emptyList.setVisibility(View.GONE);
        noNetwork.setVisibility(View.GONE);

        findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetwork()) {
                    progressBar.setVisibility(View.VISIBLE);
                    getSupportLoaderManager().initLoader(1, null, MainActivity.this).forceLoad();
                }
            }
        });

        if(checkNetwork()) {
            getSupportLoaderManager().initLoader(1, null, this).forceLoad();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkNetwork() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                emptyList.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                listView.setEmptyView(noNetwork);
                return false;
            }
        }
        return true;
    }

    @Override
    public Loader<ArrayList<EarthQuake>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMag = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String limit = sharedPreferences.getString(getString(R.string.limit_key),
                getString(R.string.limit_default_value));
        String orderby = sharedPreferences.getString(getString(R.string.orderby_key),
                getString(R.string.orderby_default));

        Uri baseUri = Uri.parse(QUAKE_URL);
        Uri.Builder uriBuider = baseUri.buildUpon();
        uriBuider.appendQueryParameter("format", "geojson");
        uriBuider.appendQueryParameter("orderby", orderby);
        uriBuider.appendQueryParameter("limit", limit);
        uriBuider.appendQueryParameter("minmagnitude", minMag);
        
        return new LoadQuakes(this, uriBuider.toString());
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<EarthQuake>> loader) {
        listView.setAdapter(new QuakeAdapter(this, new ArrayList<EarthQuake>()));
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<EarthQuake>> loader, ArrayList<EarthQuake> earthQuakes) {

        progressBar.setVisibility(View.GONE);
        emptyList.setVisibility(View.GONE);
        noNetwork.setVisibility(View.GONE);
        listView.setEmptyView(emptyList);
        if (earthQuakes == null){
            Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
            return;
        }
        quakeAdapter = new QuakeAdapter(this, earthQuakes);
        listView.setAdapter(quakeAdapter);
    }

}
