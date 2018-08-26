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
import android.widget.TextView;

import com.example.android.movieproject.provider.MovieContract;

import static com.example.android.movieproject.provider.MovieContract.BASE_CONTENT_URI;
import static com.example.android.movieproject.provider.MovieContract.PATH_MOVIES;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int SINGLE_LOADER_ID = 200;
    public static final String EXTRA_MOVIE_ID = "com.example.android.mygarden.extra.MOVIE_ID";
    long mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        mMovieId = getIntent().getLongExtra(EXTRA_MOVIE_ID, MovieContract.INVALID_MOVIE_ID);
        // This activity displays single plant information that is loaded using a cursor loader
        getSupportLoaderManager().initLoader(SINGLE_LOADER_ID, null, this);
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
        int movieImgRes = cursor.getInt(moviePosterIndex);
        long releaseDate = cursor.getLong(releaseDateIndex);
        double voteAverage = cursor.getDouble(voteAverageIndex);
        String voteDescription = cursor.getString(moviePosterIndex);
        String plot = cursor.getString(plotIndex);

        ((ImageView) findViewById(R.id.movie_list_item_image)).setImageResource(movieImgRes);
        ((TextView) findViewById(R.id.movie_title_tv)).setText(String.valueOf(mMovieId));
        ((TextView) findViewById(R.id.plant_age_number)).setText(
                String.valueOf(MovieUtils.getDisplayAgeInt(timeNow - createdAt))
        );
        ((TextView) findViewById(R.id.plant_age_unit)).setText(
                MovieUtils.getDisplayAgeUnit(this, timeNow - createdAt)
        );
        ((TextView) findViewById(R.id.last_watered_number)).setText(
                String.valueOf(MovieUtils.getDisplayAgeInt(timeNow - wateredAt))
        );
        ((TextView) findViewById(R.id.last_watered_unit)).setText(
                MovieUtils.getDisplayAgeUnit(this, timeNow - wateredAt)
        );
        int waterPercent = 100 - ((int) (100 * (timeNow - wateredAt) / MovieUtils.MAX_AGE_WITHOUT_WATER));
        ((WaterLevelView) findViewById(R.id.water_level)).setValue(waterPercent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
