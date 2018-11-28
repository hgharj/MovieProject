package com.example.android.movieproject;

import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.ContentUris;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.movieproject.utils.Controller;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.TrailerModel;
import com.example.android.movieproject.utils.UserReviewModel;
import com.example.android.movieproject.provider.MovieContract;
import com.example.android.movieproject.utils.MovieUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.movieproject.provider.MovieContract.MovieEntry;
import static com.example.android.movieproject.provider.MovieContract.BASE_CONTENT_URI;
import static com.example.android.movieproject.provider.MovieContract.PATH_MOVIES;

public class DetailActivity extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<Cursor>
{
//    private static final int INVALID_MOVIE_ID = -1;
    @BindView(R.id.movie_title_tv) TextView mMovieTitle_tv;
    @BindView(R.id.poster_iv) ImageView mPoster_tv;
    @BindView(R.id.release_date_tv) TextView mReleaseDate_tv;
    @BindView(R.id.vote_avg) RatingBar mVoteAverage_rb;
    @BindView(R.id.vote_avg_desc_tv) TextView mVoteAverageDesc_tv;
    @BindView(R.id.plot_tv) TextView mPlot_tv;

    @BindView(R.id.trailer_recycler_view) RecyclerView mTrailerRecyclerView;
    @BindView(R.id.no_trailers) TextView mNoTrailerTextView;
    @BindView(R.id.user_review_recycler_view) RecyclerView mUserReviewRecyclerView;
    @BindView(R.id.no_user_reviews) TextView mNoUserReviewTextView;

    @BindView(R.id.favorite_tb) ToggleButton toggleButton;
//    @BindView(R.id.favorite_ib) ImageButton imageButton;
    private static final String LOG_TAG = DetailActivity.class.getName();
    private long mMovieId;
    private String mMovieTitle;
    private String mPosterUrl;
    private String mReleaseDate;
    private float mVoteAverage;
    private String mPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);
        Intent data = getIntent();
        MovieModel movie = data.getParcelableExtra("Movie");

        mMovieId = movie.getId();
        mMovieTitle = movie.getTitle();
        mPosterUrl = movie.getPosterUrl();
        mReleaseDate = movie.getReleaseDate();
        mVoteAverage = movie.getVoteAverage();
        mPlot = movie.getOverview();

        final Context context = this;

//        imageButton.setImageResource(R.drawable.baseline_star_white_18);
//        final boolean favoriteBlank = true;
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (favoriteBlank) {
//                    imageButton.setColorFilter(getResources().getColor(R.color.colorProgressTint));
//                } else {
//                    imageButton.setColorFilter(getResources().getColor(R.color.colorBlank));
//
//                }
//            }
//        });

        toggleButton.setChecked(false);

//        toggleButton.setBackgroundColor(getResources().getColor(R.color.colorBlank));
//        toggleButton.setHighlightColor(getResources().getColor(R.color.colorProgressTint));
        ColorFilter colorFilter = new ColorFilter();
        final Drawable d = ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_star_white_18);
//        d.setColorFilter(getResources().getColor(R.color.colorProgressTint), PorterDuff.Mode.MULTIPLY);

        toggleButton.setBackgroundDrawable(ContextCompat. getDrawable(getApplicationContext(), R.drawable.baseline_star_white_18));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    d.setColorFilter(getResources().getColor(R.color.colorProgressTint), PorterDuff.Mode.MULTIPLY);
                    toggleButton.setBackgroundDrawable(d);
                    insertMovie();
                    Toast.makeText(context, "Added to Favorites",Toast.LENGTH_SHORT).show();
                }
                else{
                    d.setColorFilter(getResources().getColor(R.color.colorBlank), PorterDuff.Mode.MULTIPLY);
                    toggleButton.setBackgroundDrawable(d);
                    deleteMovie();
                    Toast.makeText(context, "Subtracted from Favorites",Toast.LENGTH_SHORT).show();
                }
            }
        });
        DisplayData(mMovieId, mMovieTitle, mPosterUrl, mReleaseDate, mVoteAverage, mPlot);
    }

    private AsyncTask mGetTrailers;
    private AsyncTask mGetUserReviews;
    private TrailerListAdapter mTrailerAdapter;
    private UserReviewListAdapter mUserReviewAdapter;
    public List<TrailerModel> mTrailerList;
    public List<UserReviewModel> mUserReviewList;
    private String apiKey = "78f8b58674adbaa0bf92f4de4e9a6dc3";
    private String key = "";
    private void DisplayData(final Long movieId, String title, String url, String releaseDate, float voteAverage, String plot) {
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

            loadTrailersAndUserReviews();
        }
        catch (Exception e) {
            Log.v(LOG_TAG,e.toString());
        }

    }

    private void loadTrailersAndUserReviews(){
        final Context context = this;
        ArrayList trailers = new ArrayList<TrailerModel>();
        ArrayList userReviews = new ArrayList<UserReviewModel>();

        mTrailerAdapter = new TrailerListAdapter(trailers, new TrailerListAdapter.OnItemClickListener() {
            //Pass movie data into the intent so that the detail screen can access it.
            @Override
            public void onItemClick(TrailerModel trailer) {
                Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey()));
                try {
                    context.startActivity(youTubeIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }
            }
        });

        mUserReviewAdapter = new UserReviewListAdapter(userReviews, new UserReviewListAdapter.OnItemClickListener() {
            //Pass movie data into the intent so that the detail screen can access it.
            @Override
            public void onItemClick(UserReviewModel userReview) {
//                Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + userReview.get));
//                Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/watch?v=" + key));
//                try {
//                    context.startActivity(youTubeIntent);
//                } catch (ActivityNotFoundException ex) {
//                    context.startActivity(webIntent);
//                }
            }
        });

        // If there is a network connection, fetch data
        if (checkNetworkConnection() != null && checkNetworkConnection().isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
//            mGetTrailers = new getTrailersTask().execute();
            new getTrailersTask().execute();

//            mTrailerAdapter.addAll(mTrailerList);

            new getUserReviewsTask().execute();
//            mGetUserReviews = new getUserReviewsTask().execute();
//            mUserReviewAdapter.addAll(mUserReviewList);

        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            mTrailerRecyclerView.setVisibility(View.GONE);
            mNoTrailerTextView.setText(R.string.no_internet);
            mNoTrailerTextView.setVisibility(View.VISIBLE);

            mUserReviewRecyclerView.setVisibility(View.GONE);
            mNoUserReviewTextView.setText(R.string.no_internet);
            mNoUserReviewTextView.setVisibility(View.VISIBLE);
        }
    }
    private NetworkInfo checkNetworkConnection(){

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        return connMgr.getActiveNetworkInfo();
    }

    private class getTrailersTask extends AsyncTask<Void, Void, List<TrailerModel>>{

        @Override
        protected void onPostExecute(List<TrailerModel> trailerModels) {
            super.onPostExecute(trailerModels);

            // Clear the adapter of previous movie data
            if (mTrailerAdapter != null)
                mTrailerAdapter.clear();

            // If there is a valid list of {@link Movie}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (trailerModels != null && !trailerModels.isEmpty()) {
                mTrailerAdapter.addAll(trailerModels);
                mNoTrailerTextView.setVisibility(View.GONE);
            } else {
                mNoTrailerTextView.setText(R.string.no_movies);
                mNoTrailerTextView.setVisibility(View.VISIBLE);
            }

            mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        }

        @Override
        protected List<TrailerModel> doInBackground(Void... voids) {
            Controller controller = new Controller();
            return controller.getTrailers(mMovieId,apiKey);
        }
    }

    private class getUserReviewsTask extends AsyncTask<Void, Void, List<UserReviewModel>>{

        @Override
        protected void onPostExecute(List<UserReviewModel> userReviewModels) {
            super.onPostExecute(userReviewModels);

            // Clear the adapter of previous movie data
            if (mUserReviewAdapter != null)
                mUserReviewAdapter.clear();

            // If there is a valid list of {@link Movie}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (userReviewModels != null && !userReviewModels.isEmpty()) {
                mUserReviewAdapter.addAll(userReviewModels);
                mNoUserReviewTextView.setVisibility(View.GONE);
            } else {
                mNoUserReviewTextView.setText(R.string.no_movies);
                mNoUserReviewTextView.setVisibility(View.VISIBLE);
            }

            mUserReviewRecyclerView.setAdapter(mUserReviewAdapter);
        }

        @Override
        protected List<UserReviewModel> doInBackground(Void... voids) {
            Controller controller = new Controller();
            return controller.getUserReviews(mMovieId,apiKey);
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Uri SINGLE_MOVIE_URI = ContentUris.withAppendedId(
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build(), mMovieId);
//        return new CursorLoader(this, SINGLE_MOVIE_URI, null,
//                null, null, null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }

    public void insertMovie() {
// Gets the data repository in write mode
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
        values.put(MovieEntry.COLUMN_MOVIE_POSTER, mPosterUrl);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
        values.put(MovieEntry.COLUMN_OVERVIEW, mPlot);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

//        displayDatabaseInfo();
    }

    private void deleteMovie() {
        // Only perform the delete if this is an existing pet.
//        if (mCurrentPetUri != null) {
        // Call the ContentResolver to delete the pet at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentPetUri
        // content URI already identifies the pet that we want.
        int rowsDeleted = getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);

        if (rowsDeleted > 0){
            Toast.makeText(this,R.string.delete_favorite_movie_successful,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,R.string.delete_favorite_movie_failed,Toast.LENGTH_SHORT).show();
        }
//        }
    }
}
