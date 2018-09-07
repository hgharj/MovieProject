package com.example.android.movieproject;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.movieproject.provider.MovieContract;
import com.example.android.movieproject.utils.MovieUtils;
import com.squareup.picasso.Picasso;

import static com.example.android.movieproject.provider.MovieContract.BASE_CONTENT_URI;
import static com.example.android.movieproject.provider.MovieContract.PATH_MOVIES;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int INVALID_MOVIE_ID = -1;
    public static final String EXTRA_MOVIE_ID = "com.example.android.mygarden.extra.MOVIE_ID";
    long mMovieId;
    String mMovieTitle;
    String mPosterUrl;
    String mReleaseDate;
    float mVoteAverage;
    String mPlot;
    TextView mMovieTitle_tv;
    ImageView mPoster_tv;
    TextView mReleaseDate_tv;
    RatingBar mVoteAverage_rb;
    TextView mVoteAverageDesc_tv;
    TextView mPlot_tv;

    public static final String LOG_TAG = DetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Intent data = getIntent();
        mMovieId = data.getLongExtra(EXTRA_MOVIE_ID, INVALID_MOVIE_ID);
        mMovieTitle = data.getStringExtra("MOVIE_TITLE");
        mPosterUrl = data.getStringExtra("POSTER_URL");
        mReleaseDate = data.getStringExtra("RELEASE_DATE");
        mVoteAverage = data.getFloatExtra("VOTE_AVERAGE",0);
        mPlot = data.getStringExtra("PLOT");

        mMovieTitle_tv = findViewById(R.id.movie_title_tv);
        mPoster_tv = findViewById(R.id.poster_iv);
        mReleaseDate_tv = findViewById(R.id.release_date_tv);
        mVoteAverage_rb = findViewById(R.id.vote_avg);
        mVoteAverageDesc_tv = findViewById(R.id.vote_avg_desc_tv);
        mPlot_tv = findViewById(R.id.plot_tv);

        DisplayData(mMovieTitle,mPosterUrl,mReleaseDate,mVoteAverage,mPlot);
    }

    public void DisplayData(String title, String url, String releaseDate, float voteAverage, String plot){
        mMovieTitle_tv.setText(title);
        Picasso.with(this).load(url)
                .placeholder(R.drawable.imageunavailabe)
                .error(R.drawable.imageunavailabe)
                .into(mPoster_tv);
        mReleaseDate_tv.setText(releaseDate);
        mVoteAverage_rb.setRating(Float.parseFloat(Float.toString(voteAverage)));
        mVoteAverageDesc_tv.setText(String.valueOf(voteAverage));
        mPlot_tv.setText(plot);
    }
    public void onBackButtonClick(View view) {
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri SINGLE_MOVIE_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build(), mMovieId);
        return new CursorLoader(this, SINGLE_MOVIE_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) return;
        cursor.moveToFirst();
        int movieTitleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        int moviePosterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        int releaseDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int voteAverageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int plotIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT);

        String movieTitle = cursor.getString(movieTitleIndex);
        String movieImgRes = cursor.getString(moviePosterIndex);
        String releaseDate = cursor.getString(releaseDateIndex);
        float voteAverage = cursor.getFloat(voteAverageIndex);
        String plot = cursor.getString(plotIndex);

        ImageView imgPoster = findViewById(R.id.poster_iv);
        Picasso.with(this).load(movieImgRes)
                .placeholder(R.drawable.imageunavailabe)
                .error(R.drawable.imageunavailabe)
                .into(imgPoster);

        ((TextView) findViewById(R.id.movie_title_tv)).setText(String.valueOf(mMovieId));
        ((TextView) findViewById(R.id.release_date_tv)).setText(releaseDate);

        ((RatingBar) findViewById(R.id.vote_avg)).setRating(Float.valueOf(voteAverage));

        ((TextView) findViewById(R.id.plot_tv)).setText(plot);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
