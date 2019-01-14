package com.example.android.movieproject;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.movieproject.utils.Controller;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.TrailerModel;
import com.example.android.movieproject.utils.UserReviewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.movieproject.provider.MovieContract.MovieEntry;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.movie_title_tv)
    TextView mMovieTitle_tv;
    @BindView(R.id.poster_iv)
    ImageView mPoster_iv;
    @BindView(R.id.release_date_tv)
    TextView mReleaseDate_tv;
    @BindView(R.id.vote_avg)
    RatingBar mVoteAverage_rb;
    @BindView(R.id.vote_avg_desc_tv)
    TextView mVoteAverageDesc_tv;
    @BindView(R.id.plot_tv)
    TextView mPlot_tv;
    @BindView(R.id.trailer_recycler_view)
    RecyclerView mTrailerRecyclerView;
    @BindView(R.id.no_trailers)
    TextView mNoTrailerTextView;
    @BindView(R.id.user_review_recycler_view)
    RecyclerView mUserReviewRecyclerView;
    @BindView(R.id.no_user_reviews)
    TextView mNoUserReviewTextView;
    @BindView(R.id.favorite_tb)
    ToggleButton toggleButton;
    private static final String LOG_TAG = DetailActivity.class.getName();
    private long mMovieId;
    private String mMovieTitle;
    private String mPosterUrl;
    private String mReleaseDate;
    private float mVoteAverage;
    private String mPlot;
    private String[] mProjection = {
            MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_TITLE,
            MovieEntry.COLUMN_MOVIE_POSTER,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW
    };
    private static final String MOVIE="Movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);
        Intent data = getIntent();
        MovieModel movie = data.getParcelableExtra(MOVIE);

        mMovieId = movie.getId();
        mMovieTitle = movie.getTitle();
        mPosterUrl = movie.getPosterUrl();
        mReleaseDate = movie.getReleaseDate();
        mVoteAverage = movie.getVoteAverage();
        mPlot = movie.getOverview();
        final Context context = this;
        toggleButton.setChecked(false);

        final Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_star_white_18);
        if (selectMovie()) {
            d.setColorFilter(getResources().getColor(R.color.colorProgressTint), PorterDuff.Mode.MULTIPLY);
            toggleButton.setBackgroundDrawable(d);
            toggleButton.setChecked(true);
        } else {
            d.setColorFilter(getResources().getColor(R.color.colorBlank), PorterDuff.Mode.MULTIPLY);
            toggleButton.setBackgroundDrawable(d);
            toggleButton.setChecked(false);
        }

        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_star_white_18));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    d.setColorFilter(getResources().getColor(R.color.colorProgressTint), PorterDuff.Mode.MULTIPLY);
                    toggleButton.setBackgroundDrawable(d);
                    insertMovie();
                    Toast.makeText(context, R.string.add_favorite_movie, Toast.LENGTH_SHORT).show();
                } else {
                    d.setColorFilter(getResources().getColor(R.color.colorBlank), PorterDuff.Mode.MULTIPLY);
                    toggleButton.setBackgroundDrawable(d);
                    deleteMovie();
                    Toast.makeText(context, R.string.delete_favorite_movie, Toast.LENGTH_SHORT).show();
                }
            }
        });
        DisplayData();
    }

    private TrailerListAdapter mTrailerAdapter;
    private UserReviewListAdapter mUserReviewAdapter;
    private String apiKey = BuildConfig.ApiKey;

    private void DisplayData() {
        try {
            mMovieTitle_tv.setText(mMovieTitle);
            Picasso.with(this).load(mPosterUrl)
                    .placeholder(R.drawable.imageunavailabe)
                    .error(R.drawable.imageunavailabe)
                    .into(mPoster_iv);
            mReleaseDate_tv.setText(mReleaseDate);
            //voteAverage is divided by 2 because there are only 5 starts whereas voteAverage goes up to 10.
            //Therefore 2 voteAverage points equate to 1 star.
            mVoteAverage_rb.setRating(mVoteAverage / 2);
            mVoteAverageDesc_tv.setText(String.valueOf(mVoteAverage / 2));
            mPlot_tv.setText(mPlot);

            loadTrailersAndUserReviews();
        } catch (Exception e) {
            Log.v(LOG_TAG, e.toString());
        }
    }

    private void loadTrailersAndUserReviews() {
        final Context context = this;
        ArrayList trailers = new ArrayList<TrailerModel>();
        final ArrayList userReviews = new ArrayList<UserReviewModel>();

        mTrailerAdapter = new TrailerListAdapter(trailers, context, new TrailerListAdapter.OnItemClickListener() {
            //Pass movie data into the intent so that the detail screen can access it.
            @Override
            public void onItemClick(TrailerModel trailer) {
                Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(R.string.vnb_youtube + trailer.getKey()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(R.string.trailer_url + trailer.getKey()));
                try {
                    context.startActivity(youTubeIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }
            }
        });

        mUserReviewAdapter = new UserReviewListAdapter(userReviews);

        // If there is a network connection, fetch data
        if (checkNetworkConnection() != null && checkNetworkConnection().isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            new getTrailersTask().execute();
            new getUserReviewsTask().execute();
        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            mTrailerRecyclerView.setVisibility(View.GONE);
            mNoTrailerTextView.setText(R.string.no_trailers);
            mNoTrailerTextView.setVisibility(View.VISIBLE);

            mUserReviewRecyclerView.setVisibility(View.GONE);
            mNoUserReviewTextView.setText(R.string.no_reviews);
            mNoUserReviewTextView.setVisibility(View.VISIBLE);
        }
    }

    private NetworkInfo checkNetworkConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        return connMgr.getActiveNetworkInfo();
    }

    private class getTrailersTask extends AsyncTask<Void, Void, List<TrailerModel>> {
        @Override
        protected void onPostExecute(List<TrailerModel> trailerModels) {
            super.onPostExecute(trailerModels);
            // Clear the adapter of previous movie data
            if (mTrailerAdapter != null)
                mTrailerAdapter.clear();

            // If there is a valid list of {@link TrailerModel}s, then add them to the adapter's
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
            return controller.getTrailers(mMovieId, apiKey);
        }
    }

    private class getUserReviewsTask extends AsyncTask<Void, Void, List<UserReviewModel>> {
        @Override
        protected void onPostExecute(List<UserReviewModel> userReviewModels) {
            super.onPostExecute(userReviewModels);
            // Clear the adapter of previous movie data
            if (mUserReviewAdapter != null)
                mUserReviewAdapter.clear();

            // If there is a valid list of {@link UserModel}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (userReviewModels != null && !userReviewModels.isEmpty()) {
                mUserReviewAdapter.addAll(userReviewModels);
                mNoUserReviewTextView.setVisibility(View.GONE);
            } else {
                mNoUserReviewTextView.setText(R.string.no_movies);
                mNoUserReviewTextView.setVisibility(View.VISIBLE);
            }
            mUserReviewRecyclerView.setAdapter(mUserReviewAdapter);
            mUserReviewRecyclerView.addItemDecoration(new DividerItemDecoration(mUserReviewRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }

        @Override
        protected List<UserReviewModel> doInBackground(Void... voids) {
            Controller controller = new Controller();
            return controller.getUserReviews(mMovieId, apiKey);
        }
    }

    public Boolean selectMovie() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, mMovieId);
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
        values.put(MovieEntry.COLUMN_MOVIE_POSTER, mPosterUrl);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
        values.put(MovieEntry.COLUMN_OVERVIEW, mPlot);

        String[] selectionArgs = {Long.toString(mMovieId)};
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build(), mProjection, null, selectionArgs, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    public void insertMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, mMovieId);
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
        values.put(MovieEntry.COLUMN_MOVIE_POSTER, mPosterUrl);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
        values.put(MovieEntry.COLUMN_OVERVIEW, mPlot);

        // Insert a new row for each movie into the provider using the ContentResolver.
        getContentResolver().insert(MovieEntry.CONTENT_URI, values);
    }

    private void deleteMovie() {
        // Only perform the delete if this is an existing movie.
        // Call the ContentResolver to delete the movie at the given content URI.
        int rowsDeleted = getContentResolver().delete(MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build(), null, null);

        if (rowsDeleted > 0) {
            Toast.makeText(this, R.string.delete_favorite_movie_successful, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.delete_favorite_movie_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
