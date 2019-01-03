package com.example.android.movieproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ContentUris;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.movieproject.utils.Controller;
import com.example.android.movieproject.utils.MovieDBService;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.MovieModel;

import com.example.android.movieproject.provider.MovieContract;
import com.example.android.movieproject.utils.MovieUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.movieproject.provider.MovieContract.BASE_CONTENT_URI;
import static com.example.android.movieproject.provider.MovieContract.PATH_MOVIES;
import static com.example.android.movieproject.provider.MovieContract.MovieEntry;

public class MainActivity extends AppCompatActivity
        implements
//        LoaderManager.LoaderCallbacks<List<MovieModel>>,
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
SharedPreferences.OnSharedPreferenceChangeListener{
    private static final int MOVIE_LOADER_ID = 100;
    private MovieListAdapter mAdapter;
    private MovieCursorAdapter mMovieCursorAdapter;
    private FavoriteMovieListAdapter mFavoriteMovieAdapter;

    private AsyncTask mGetMovies;
    private Context context;
    
    @BindView(R.id.movie_recycler_view) RecyclerView mMovieRecyclerView;
    @BindView(R.id.empty_view) TextView mEmptyStateTextView;
    @BindView(R.id.title_view) TextView mTitleTextView;

    @BindView(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String SEARCH_POPULAR="popular";
    private static final String SEARCH_HIGHEST_RATED="vote";
    private static final String SEARCH_FAVORITES="favorite";
    private static final String KEY_STRING="api_key";
    private static final String KEY_SORT="sort_by";

    private String[] mProjection = {
            MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_TITLE,
            MovieEntry.COLUMN_MOVIE_POSTER,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        context=this;
//        mEmptyStateTextView = findViewById(R.id.empty_view);

//        mMovieRecyclerView = findViewById(R.id.movie_recycler_view);

        loadScreen();
    }

    private void loadScreen(){
        final Context context = this;
        ArrayList movies = new ArrayList<MovieModel>();

        mAdapter = new MovieListAdapter(movies, new MovieListAdapter.OnItemClickListener() {
            //Pass movie data into the intent so that the detail screen can access it.
            @Override
            public void onItemClick(MovieModel movie) {
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra("Movie",movie);
                startActivity(detailIntent);
            }
        });

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
//                    getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                    if (getPreference().contains("favorite")){

                        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                    } else {
                        new getMoviesTask().execute();
                    }
                }
            });
        } else if (getPreference().contains("favorite")) {
//            mFavoriteMovieAdapter = new FavoriteMovieListAdapter(context, null, new FavoriteMovieListAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(MovieModel movie) {
//                    Intent detailIntent = new Intent(context, DetailActivity.class);
//                    detailIntent.putExtra("Movie",movie);
//                    startActivity(detailIntent);
//                }
//            });

            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        }
            else {
            // Otherwise, display error
            // Update empty state with no connection error message
            mMovieRecyclerView.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private String getPreference(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        return orderBy;
    }

    private NetworkInfo checkNetworkConnection(){

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        return connMgr.getActiveNetworkInfo();
    }

    private class getMoviesTask extends AsyncTask<Void, Void, List<MovieModel>>{

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            super.onPostExecute(movies);

            // Clear the adapter of previous movie data
            if (mAdapter != null)
                mAdapter.clear();

            // If there is a valid list of {@link Movie}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (movies != null && !movies.isEmpty()) {
                mAdapter.addAll(movies);
                mEmptyStateTextView.setVisibility(View.GONE);
            } else {
                mEmptyStateTextView.setText(R.string.no_movies);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
            }

            if (getPreference().contains("vote")){
                mTitleTextView.setText(R.string.settings_order_by_highest_rated_desc_label);
            } else if (getPreference().contains("popular")){
                mTitleTextView.setText(R.string.settings_order_by_most_popular_desc_label);
            }

            mMovieRecyclerView.setAdapter(mAdapter);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

            mMovieRecyclerView.setLayoutManager(layoutManager);
            mMovieRecyclerView.setHasFixedSize(true);
//            mMovieRecyclerView.addItemDecoration(new DividerItemDecoration(mMovieRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }



        @Override
        protected List<MovieModel> doInBackground(Void... voids) {
//            String movieUrl = getString(R.string.movie_url);
//            String endPointTopRated = getString(R.string.endpoint_top_rated);
//            String endPointPopular = getString(R.string.endpoint_popular);

            String apiKey = "78f8b58674adbaa0bf92f4de4e9a6dc3";//BuildConfig.ApiKey;

//            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//
//            String orderBy = sharedPrefs.getString(
//                    getString(R.string.settings_order_by_key),
//                    getString(R.string.settings_order_by_default)
//            );

            Controller controller = new Controller();

//            if (orderBy.contains(SEARCH_POPULAR)) {
//                movieUrl += endPointPopular;
//                controller.getMovieList(orderBy,apiKey);
//            } else if(orderBy.contains(SEARCH_HIGHEST_RATED)) {
//                movieUrl += endPointTopRated;
//            } else {
//                return controller.getFavoriteMovies(orderBy,apiKey);
//            }

//            if (orderBy.contains(SEARCH_POPULAR) || orderBy.contains(SEARCH_HIGHEST_RATED)){
                return controller.getMovieList(getPreference(),apiKey);
//            } else {

//                return controller.getFavoriteMovies(orderBy,apiKey);
//            }

        }
    }

//    @Override
//    public Loader<List<MovieModel>> onCreateLoader(int id, Bundle args) {
//
//        String movieUrl = getString(R.string.movie_url);
//        String endPointTopRated = getString(R.string.endpoint_top_rated);
//        String endPointPopular = getString(R.string.endpoint_popular);
//
//        String apiKey = "78f8b58674adbaa0bf92f4de4e9a6dc3";//BuildConfig.ApiKey;
//
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//
//        String orderBy = sharedPrefs.getString(
//                getString(R.string.settings_order_by_key),
//                getString(R.string.settings_order_by_default)
//        );
//
//        if (orderBy.contains(SEARCH_POPULAR)) {
//            movieUrl += endPointPopular;
//        } else if(orderBy.contains(SEARCH_HIGHEST_RATED)) {
//            movieUrl += endPointTopRated;
//        } else {
//
//        }
//
////        Uri baseUri = Uri.parse(movieUrl);
////        Uri.Builder uriBuilder = baseUri.buildUpon();
////
////        uriBuilder.appendQueryParameter(KEY_STRING, apiKey);
////        uriBuilder.appendQueryParameter(KEY_SORT, orderBy);
//
//        // Create a new loader for the given URL
//        return new MovieLoader(this, orderBy, apiKey);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<List<MovieModel>> loader, final List<MovieModel> movies) {
//        // Clear the adapter of previous movie data
//        if (mAdapter != null)
//            mAdapter.clear();
//
//        // If there is a valid list of {@link Movie}s, then add them to the adapter's
//        // data set. This will trigger the ListView to update.
//        if (movies != null && !movies.isEmpty()) {
//            mAdapter.addAll(movies);
//            mEmptyStateTextView.setVisibility(View.GONE);
//        } else {
//            mEmptyStateTextView.setText(R.string.no_movies);
//            mEmptyStateTextView.setVisibility(View.VISIBLE);
//        }
//
//        mMovieRecyclerView.setAdapter(mAdapter);
//        GridLayoutManager layoutManager = new GridLayoutManager(this, getSpanCount(), GridLayoutManager.VERTICAL, false);
//
//        mMovieRecyclerView.setLayoutManager(layoutManager);
//        mMovieRecyclerView.setHasFixedSize(true);
//        mMovieRecyclerView.addItemDecoration(new DividerItemDecoration(mMovieRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
//        if (mSwipeRefreshLayout.isRefreshing()) {
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
//    }



//    @Override
////    public void onLoaderReset(Loader<List<MovieModel>> movies) {
////        mAdapter.clear();
////    }

        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
                //MovieContract.MovieEntry.CONTENT_URI;
//        Uri SINGLE_MOVIE_URI = ContentUris.withAppendedId(
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build(), mMovieId);
        return new CursorLoader(this, movieUri, mProjection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        if (cursor == null || cursor.getCount() < 1) return;
//        cursor.moveToFirst();
//        int movieTitleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
//        int moviePosterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
//        int releaseDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
//        int voteAverageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
//        int plotIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
//
//        String movieTitle = cursor.getString(movieTitleIndex);
//        String movieImgRes = cursor.getString(moviePosterIndex);
//        String releaseDate = cursor.getString(releaseDateIndex);
//        float voteAverage = cursor.getFloat(voteAverageIndex);
//        String plot = cursor.getString(plotIndex);
//
//        ImageView imgPoster = findViewById(R.id.poster_iv);
//        Picasso.with(this).load(movieImgRes)
//                .placeholder(R.drawable.imageunavailabe)
//                .error(R.drawable.imageunavailabe)
//                .into(imgPoster);
//
//        ((TextView) findViewById(R.id.movie_title_tv)).setText(String.valueOf(mMovieId));
//        ((TextView) findViewById(R.id.release_date_tv)).setText(MovieUtils.convertYYYY_MM_DD_MiddleEndian(releaseDate));
//
//        ((RatingBar) findViewById(R.id.vote_avg)).setRating(Float.valueOf(voteAverage));
//
//        ((TextView) findViewById(R.id.plot_tv)).setText(plot);

        mTitleTextView.setText(R.string.settings_order_by_favorite_desc_label);

        if (cursor != null && cursor.getCount()>0) {
            mEmptyStateTextView.setVisibility(View.GONE);
            cursor.moveToFirst();

        } else {
            mEmptyStateTextView.setText(R.string.no_movies);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }


        mFavoriteMovieAdapter = new FavoriteMovieListAdapter(context, cursor, new FavoriteMovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieModel movie) {
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra("Movie",movie);
                startActivity(detailIntent);
            }
        });

        mFavoriteMovieAdapter.swapCursor(cursor);



        // Clear the adapter of previous movie data
//        if (mFavoriteMovieAdapter != null)
//            mFavoriteMovieAdapter.swapCursor(null);

//        // If there is a valid list of {@link Movie}s, then add them to the adapter's
//        // data set. This will trigger the ListView to update.
//        if (movies != null && !movies.isEmpty()) {
//            mAdapter.addAll(movies);
//            mEmptyStateTextView.setVisibility(View.GONE);
//        } else {
//            mEmptyStateTextView.setText(R.string.no_movies);
//            mEmptyStateTextView.setVisibility(View.VISIBLE);
//        }

        mMovieRecyclerView.setAdapter(mFavoriteMovieAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);
//        mMovieRecyclerView.addItemDecoration(new DividerItemDecoration(mMovieRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
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
//        if (checkNetworkConnection() != null && checkNetworkConnection().isConnected()) {
//            mSwipeRefreshLayout.setRefreshing(true);
//            mMovieRecyclerView.setVisibility(View.VISIBLE);
//            loadScreen();
//        } else {
//            // Otherwise, display error
//            // Update empty state with no connection error message
//            mEmptyStateTextView.setVisibility(View.VISIBLE);
//            mEmptyStateTextView.setText(R.string.no_internet);
//            mSwipeRefreshLayout.setRefreshing(false);
//            mMovieRecyclerView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
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
