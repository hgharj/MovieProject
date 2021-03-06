package com.example.android.movieproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.android.movieproject.utils.Controller;
import com.example.android.movieproject.utils.MovieModel;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.example.android.movieproject.provider.MovieContract.BASE_CONTENT_URI;
import static com.example.android.movieproject.provider.MovieContract.MovieEntry;
import static com.example.android.movieproject.provider.MovieContract.PATH_MOVIES;

public class MainActivity extends AppCompatActivity
        implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int MOVIE_LOADER_ID = 100;
    private MovieListAdapter mAdapter;
    private FavoriteMovieListAdapter mFavoriteMovieAdapter;
    private Context context;

    @BindView(R.id.movie_recycler_view)
    RecyclerView mMovieRecyclerView;
    @BindView(R.id.empty_view)
    TextView mEmptyStateTextView;
    @BindView(R.id.title_view)
    TextView mTitleTextView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String[] mProjection = {
            MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_TITLE,
            MovieEntry.COLUMN_MOVIE_POSTER,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW
    };
    private static final String MOVIE = "Movie";
    private static final String FAVORITE = "favorite";
    private static final String VOTE = "vote";
    private static final String POPULAR = "popular";

    private static final String RECYCLER_LAYOUT = "recycler-layout";
    private static final String RECYCLER_LIST_DATA = "recycler-list-data";
    private static final String RECYCLER_CURSOR_DATA = "recycler-cursor-data";
    private ArrayList<MovieModel> mMovies;
    private Cursor mMovieCursor;
    String mApiKey = BuildConfig.ApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        context = this;
        ArrayList movies = new ArrayList<MovieModel>();
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        mMovieRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MovieListAdapter(movies, new MovieListAdapter.OnItemClickListener() {
            //Pass movie data into the intent so that the detail screen can access it.
            @Override
            public void onItemClick(MovieModel movie) {
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra(MOVIE, movie);
                startActivity(detailIntent);
            }
        });

        mFavoriteMovieAdapter = new FavoriteMovieListAdapter(context, null, new FavoriteMovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieModel movie) {
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra(MOVIE, movie);
                startActivity(detailIntent);
            }
        });

        if (getPreference().contains(FAVORITE)) {
            mMovieRecyclerView.setAdapter(mFavoriteMovieAdapter);
            if (savedInstanceState != null) {
                Parcelable savedRecyclerViewState = savedInstanceState.getParcelable(RECYCLER_LAYOUT);
                mMovieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
                mMovieCursor = savedInstanceState.getParcelable(RECYCLER_CURSOR_DATA);
                if (mMovieCursor != null){
                    mFavoriteMovieAdapter.swapCursor(mMovieCursor);
                }
                else {
                    loadScreen();
                }
            } else {
                // initialize the list to a new empty list
                mFavoriteMovieAdapter.swapCursor(null);

                // kick off the data fetching task
                loadScreen();
            }
        } else {
            mMovieRecyclerView.setAdapter(mAdapter);
            if (savedInstanceState != null) {
                Parcelable savedRecyclerViewState = savedInstanceState.getParcelable(RECYCLER_LAYOUT);
                mMovieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
                mMovies = savedInstanceState.getParcelableArrayList(RECYCLER_LIST_DATA);
                mAdapter.addAll(mMovies);
            } else {
                // initialize the list to a new empty list
                mAdapter.clear();

                // kick off the data fetching task
                loadScreen();
            }
        }
    }

    private void loadScreen() {
        // If there is a network connection, fetch data
        if (checkNetworkConnection() != null && checkNetworkConnection().isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    // Fetching data from server
                    if (getPreference().contains(FAVORITE)) {
                        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                    } else {
                        new getMoviesTask().execute();
                    }
                }
            });
        } else if (getPreference().contains(FAVORITE)) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            mMovieRecyclerView.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private String getPreference() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
    }

    private NetworkInfo checkNetworkConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        return connMgr.getActiveNetworkInfo();
    }

    private class getMoviesTask extends AsyncTask<Void, Void, List<MovieModel>> {
        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            super.onPostExecute(movies);
            // Clear the adapter of previous movie data
            if (mAdapter != null)
                mAdapter.clear();

            // If there is a valid list of {@link Movie}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (movies != null && !movies.isEmpty()) {
                mMovies= new ArrayList<>();
                mMovies.addAll(movies);
                mAdapter.addAll(movies);
                mMovieRecyclerView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setVisibility(View.GONE);
            } else {
                mEmptyStateTextView.setText(R.string.no_movies);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
            }

            if (getPreference().contains(VOTE)) {
                mTitleTextView.setText(R.string.settings_order_by_highest_rated_desc_label);
            } else if (getPreference().contains(POPULAR)) {
                mTitleTextView.setText(R.string.settings_order_by_most_popular_desc_label);
            }

            mMovieRecyclerView.setAdapter(mAdapter);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

            mMovieRecyclerView.setLayoutManager(layoutManager);
            mMovieRecyclerView.setHasFixedSize(true);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        protected List<MovieModel> doInBackground(Void... voids) {
            Controller controller = new Controller();
            return controller.getMovieList(getPreference(), mApiKey);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        return new CursorLoader(this, movieUri, mProjection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mTitleTextView.setText(R.string.settings_order_by_favorite_desc_label);

        if (cursor != null && cursor.getCount() > 0) {
            mMovieCursor = cursor;
            mEmptyStateTextView.setVisibility(View.GONE);
            mMovieRecyclerView.setVisibility(View.VISIBLE);
            cursor.moveToFirst();

        } else {
            mEmptyStateTextView.setText(R.string.no_movies);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        mFavoriteMovieAdapter.swapCursor(cursor);
        mMovieRecyclerView.setAdapter(mFavoriteMovieAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteMovieAdapter.swapCursor(null);
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

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        loadScreen();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(RECYCLER_LAYOUT, mMovieRecyclerView.getLayoutManager().onSaveInstanceState());
        if (getPreference().contains(FAVORITE)) {
            if (mMovieCursor!=null){
            savedInstanceState.putParcelable(RECYCLER_CURSOR_DATA, mMovieCursor.getExtras());}
        } else {
            savedInstanceState.putParcelableArrayList(RECYCLER_LIST_DATA, mMovies);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
