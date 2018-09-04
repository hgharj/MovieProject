package com.example.android.movieproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.drm.DrmStore;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieproject.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.movieproject.provider.MovieContract.BASE_CONTENT_URI;
import static com.example.android.movieproject.provider.MovieContract.PATH_MOVIES;
import static com.example.android.movieproject.provider.MovieContract.MovieEntry;

public class MainActivity extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<Cursor>
        implements LoaderManager.LoaderCallbacks<List<Movie>>
{
    private static final int MOVIE_LOADER_ID = 100;
    private MovieListAdapter mAdapter;

    private RecyclerView mMovieRecyclerView;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;
    public static final String LOG_TAG = MainActivity.class.getName();
    private String movieUrl = "https://api.themoviedb.org/3/movie/popular?";
    private String apiKey = "78f8b58674adbaa0bf92f4de4e9a6dc3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        ArrayList movies = new ArrayList<Movie>();
//        mAdapter = new MovieListAdapter();
        mAdapter = new MovieListAdapter(movies, new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Toast.makeText(getBaseContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Intent detailIntent = new Intent(context,DetailActivity.class);
                detailIntent.putExtra("MOVIE_ID",movie.getMovieId());

                startActivity(detailIntent);

            }
        });

        mMovieRecyclerView = (RecyclerView)findViewById(R.id.movie_recycler_view);


        // Create a new adapter that takes an empty list of movies as input
//        mAdapter = new MovieListAdapter(this, null);


//        mMovieRecyclerView.setAdapter(new MovieListAdapter(new List<Movie>, new MovieListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Movie movie) {
//                Toast.makeText(getBaseContext(), "Item Clicked", Toast.LENGTH_LONG).show();
//            }
//        }));

//        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,null,this);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
//        movieListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected movie.
//        mMovieRecyclerView.setOnClickListener(new AdapterView.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Find the current movie that was clicked on
//                android.graphics.Movie currentMovie = mAdapter.);
//
//                // Convert the String URL into a URI object (to pass into the Intent constructor)
//                Uri movieUri = Uri.parse(currentMovie.getUrl());
//
//                // Create a new intent to view the movie URI
//                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, movieUri);
//
//                // Send the intent to launch a new activity
//                startActivity(websiteIntent);
//            }
//        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getSupportLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Loader loader = loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mProgressBar.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Uri MOVIE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
//        return new CursorLoader(this, MOVIE_URI, null,
//                null, null, MovieEntry.COLUMN_RELEASE_DATE);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        cursor.moveToFirst();
//        mAdapter.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader) {
//        //mAdapter.clear();
//        mAdapter.swapCursor(null);
//    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(movieUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api_key", apiKey);
        uriBuilder.appendQueryParameter("sort_by", orderBy);

        // Create a new loader for the given URL
        return new MovieLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, final List<Movie> movies) {
        // Clear the adapter of previous movie data
        if (mAdapter != null)
            mAdapter.clear();

        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (movies != null && !movies.isEmpty()) {
//            updateUi(movies);
            mAdapter.addAll(movies);
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);
        } else {
            mEmptyStateTextView.setText(R.string.no_movies);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }

        mMovieRecyclerView.setAdapter(mAdapter);
        mMovieRecyclerView.setLayoutManager(
                new GridLayoutManager(this,2)
        );
        mMovieRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> movies) {
        mAdapter.clear();
    }

    public void onMovieClick(View view) {
        ImageView imgView = (ImageView) view.findViewById(R.id.movie_list_item_image);
        long movieId = (long) imgView.getTag();
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
}
