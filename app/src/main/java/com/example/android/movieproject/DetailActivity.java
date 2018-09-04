package com.example.android.movieproject;

import android.content.ContentUris;
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

    public static final String LOG_TAG = DetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        mMovieId = getIntent().getLongExtra(EXTRA_MOVIE_ID, INVALID_MOVIE_ID);

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
        int voteDescriptionIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_DESCRIPTION);
        int plotIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT);

        String movieTitle = cursor.getString(movieTitleIndex);
        String movieImgRes = cursor.getString(moviePosterIndex);
        long releaseDate = cursor.getLong(releaseDateIndex);
        float voteAverage = cursor.getFloat(voteAverageIndex);
        String voteDescription = cursor.getString(voteDescriptionIndex);
        String plot = cursor.getString(plotIndex);

        ImageView imgPoster = findViewById(R.id.poster_iv);
        Picasso.with(this).load(movieImgRes)
                .placeholder(R.drawable.imageunavailabe)
                .error(R.drawable.imageunavailabe)
                .into(imgPoster);

        ((TextView) findViewById(R.id.movie_title_tv)).setText(String.valueOf(mMovieId));
        ((TextView) findViewById(R.id.release_date_tv)).setText(MovieUtils.getReleaseDateAsString(this,releaseDate));

        ((RatingBar) findViewById(R.id.vote_avg)).setRating(Float.valueOf(voteAverage));

        ((TextView) findViewById(R.id.vote_desc_tv)).setText(voteDescription);
        ((TextView) findViewById(R.id.plot_tv)).setText(plot);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
