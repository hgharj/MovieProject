package com.example.android.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final int INVALID_MOVIE_ID = -1;
    private long mMovieId;
    private String mMovieTitle;
    private String mPosterUrl;
    private String mReleaseDate;
    private float mVoteAverage;
    private String mPlot;
    private TextView mMovieTitle_tv;
    private ImageView mPoster_tv;
    private TextView mReleaseDate_tv;
    private RatingBar mVoteAverage_rb;
    private TextView mVoteAverageDesc_tv;
    private TextView mPlot_tv;

    private static final String LOG_TAG = DetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Intent data = getIntent();
        mMovieId = data.getLongExtra(Movie.MOVIE_ID, INVALID_MOVIE_ID);
        mMovieTitle = data.getStringExtra(Movie.MOVIE_TITLE);
        mPosterUrl = data.getStringExtra(Movie.POSTER_URL);
        mReleaseDate = data.getStringExtra(Movie.RELEASE_DATE);
        mVoteAverage = data.getFloatExtra(Movie.VOTE_AVERAGE, 0);
        mPlot = data.getStringExtra(Movie.PLOT);

        mMovieTitle_tv = findViewById(R.id.movie_title_tv);
        mPoster_tv = findViewById(R.id.poster_iv);
        mReleaseDate_tv = findViewById(R.id.release_date_tv);
        mVoteAverage_rb = findViewById(R.id.vote_avg);
        mVoteAverageDesc_tv = findViewById(R.id.vote_avg_desc_tv);
        mPlot_tv = findViewById(R.id.plot_tv);

        DisplayData(mMovieTitle, mPosterUrl, mReleaseDate, mVoteAverage, mPlot);
    }

    private void DisplayData(String title, String url, String releaseDate, float voteAverage, String plot) {
        try {
            mMovieTitle_tv.setText(title);
            Picasso.with(this).load(url)
                    .placeholder(R.drawable.imageunavailabe)
                    .error(R.drawable.imageunavailabe)
                    .into(mPoster_tv);
            mReleaseDate_tv.setText(releaseDate);
            //voteAverage is divided by 2 because there are only 5 starts whereas voteAverage goes up to 10.
            //Therefore 2 voteAverage points equate to 1 star.
            mVoteAverage_rb.setRating(voteAverage / 2);
            mVoteAverageDesc_tv.setText(String.valueOf(voteAverage / 2));
            mPlot_tv.setText(plot);
        }
        catch (Exception e) {
            Log.v(LOG_TAG,e.toString());
        }

    }
}
