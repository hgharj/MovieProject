package com.example.android.movieproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;

import static com.example.android.movieproject.provider.MovieContract.BASE_CONTENT_URI;
import static com.example.android.movieproject.provider.MovieContract.PATH_MOVIES;
import static com.example.android.movieproject.provider.MovieContract.MovieEntry;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIE_LOADER_ID = 100;
    private MovieListAdapter mAdapter;

    private RecyclerView mMovieRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.movie_recycler_view);
        recyclerView.setLayoutManager(
                new GridLayoutManager(this,4)
        );

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri MOVIE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        return new CursorLoader(this, MOVIE_URI, null,
                null, null, MovieEntry.COLUMN_RELEASE_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void onMovieClick(View view) {
        ImageView imgView = (ImageView) view.findViewById(R.id.movie_list_item_image);
        long movieId = (long) imgView.getTag();
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(intent);
    }
}
