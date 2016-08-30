package com.example.android.moviemadness;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    public ImageView imageView;
    public TextView movie_name;
    public TextView movie_info;
    public TextView rating;
    public TextView release_date;
    public String position_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(R.id.imageView);
        movie_name = (TextView) findViewById(R.id.movie_name);
        movie_info = (TextView) findViewById(R.id.movie_info);
        rating = (TextView) findViewById(R.id.rating);
        release_date = (TextView) findViewById(R.id.release_date);

        position_json = getIntent().getStringExtra("json");
        getMovieInfo(position_json);
    }

    public void getMovieInfo(String info) {
        try {
            JSONObject jsonObject = new JSONObject(info);
            Picasso.with(this)
                    .load("http://image.tmdb.org/t/p/w185/" + jsonObject.optString("poster_path"))
                    .into(imageView);
            movie_name.setText(jsonObject.optString("original_title"));
            movie_info.setText(jsonObject.optString("overview"));
            rating.append(jsonObject.optString("vote_average"));
            release_date.append(jsonObject.optString("release_date"));
        } catch (JSONException e) {
            Log.e("Json fragment", "JSON Exception", e);
        }

    }

}
