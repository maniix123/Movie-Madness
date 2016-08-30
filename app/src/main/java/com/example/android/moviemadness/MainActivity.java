package com.example.android.moviemadness;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public String sort_mode = "unsorted";
    public List<String> urll = new ArrayList<String>();
    public GridView gridview;
    public String baseUrl;
    public String apiKey = "02905bb18b5b26adf05d70a6bb9bc6a3";
    public Retrieve retrieve;
    public JSONObject jsonRootObject;
    public JSONArray jsonArray;
    ImageAdapter adapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_popular) {
            sort_mode = "popular";
            baseUrl = "http://api.themoviedb.org/3/movie/".concat(sort_mode).concat("?api_key=");
            retrieve = new Retrieve();
            retrieve.execute(baseUrl.concat(apiKey));
            return true;
        }
        if (id == R.id.sort_rate) {
            sort_mode = "top_rated";
            baseUrl = "http://api.themoviedb.org/3/movie/".concat(sort_mode).concat("?api_key=");
            retrieve = new Retrieve();
            retrieve.execute(baseUrl.concat(apiKey));
            return true;
        }
        if (id == R.id.sort_upcoming) {
            sort_mode = "upcoming";
            baseUrl = "http://api.themoviedb.org/3/movie/".concat(sort_mode).concat("?api_key=");
            retrieve = new Retrieve();
            retrieve.execute(baseUrl.concat(apiKey));
            return true;
        }
        if (id == R.id.sort_nowPlaying) {
            sort_mode = "now_playing";
            baseUrl = "http://api.themoviedb.org/3/movie/".concat(sort_mode).concat("?api_key=");
            retrieve = new Retrieve();
            retrieve.execute(baseUrl.concat(apiKey));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridview = (GridView) findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                try {
                    intent.putExtra("json", jsonArray.getJSONObject(position).toString());
                } catch (JSONException e) {
                    Log.e("Start intent", "JSON Array", e);
                }
                startActivity(intent);
            }
        });
        if (sort_mode.equals("unsorted")) {
            sort_mode = "popular";
        }
        baseUrl = "http://api.themoviedb.org/3/movie/".concat(sort_mode).concat("?api_key=");
        retrieve = new Retrieve();
        retrieve.execute(baseUrl.concat(apiKey));
    }


    public class Retrieve extends AsyncTask<String, Void, String> {
        String MovieJsonStr;
        protected String doInBackground(String... urls) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                MovieJsonStr = buffer.toString();
                return MovieJsonStr;
            } catch (IOException e) {
                Log.e("Retrieve", "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Retrieve", "Error closing stream", e);
                    }
                }
            }
        }

        protected void onPostExecute(String result) {
            getPoster(result);
        }
    }

    public void getPoster(String mJsonStr) {
        try {
            if (mJsonStr == null) {
                new AlertDialog.Builder(this).setTitle("Error").setMessage("Internet not Available").show();
                return;
            }
            jsonRootObject = new JSONObject(mJsonStr);
            jsonArray = jsonRootObject.optJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                urll.add(i, jsonObject.optString("poster_path"));
            }
            adapter = new ImageAdapter(this, urll);
            gridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("Json fragment", "JSON Exception", e);
        }
    }
}