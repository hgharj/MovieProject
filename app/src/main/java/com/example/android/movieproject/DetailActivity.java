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
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.movieproject.utils.Controller;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.TrailerModel;
import com.example.android.movieproject.utils.UserReviewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
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
    @BindView(R.id.detail_scroll_view)
    ScrollView mScrollView;
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
    private static final String TRAILERS_RECYCLER_LAYOUT="trailers-recycler-layout";
    private static final String TRAILERS_RECYCLER_DATA="trailers-recycler-data";
    private static final String REVIEWS_RECYCLER_LAYOUT="reviews-recycler-layout";
    private static final String REVIEWS_RECYCLER_DATA="reviews-recycler-data";
    private static final String MOVIE_ID="movie-id";
    private static final String MOVIE_TITLE="movie-title";
    private static final String POSTER_URL="poster-url";
    private static final String RELEASE_DATE="release-date";
    private static final String VOTE_AVERAGE="vote-average";
    private static final String PLOT="plot";
    private static final String TOGGLE="toggle-button-value";
    private static final String SCROLL_POSITION="scroll-position";
    private ArrayList<TrailerModel> mTrailers;
    private ArrayList<UserReviewModel> mReviews;
    private final Context mContext = this;
    private TrailerListAdapter mTrailerAdapter;
    private UserReviewListAdapter mUserReviewAdapter;
    private String mApiKey = BuildConfig.ApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);

        mTrailers = new ArrayList<TrailerModel>();
        mReviews = new ArrayList<UserReviewModel>();

        mTrailerAdapter = new TrailerListAdapter(new ArrayList<TrailerModel>(), mContext, new TrailerListAdapter.OnItemClickListener() {
            //Pass movie data into the intent so that the detail screen can access it.
            @Override
            public void onItemClick(TrailerModel trailer) {
                Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(R.string.vnb_youtube + trailer.getKey()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(R.string.trailer_url + trailer.getKey()));
                try {
                    mContext.startActivity(youTubeIntent);
                } catch (ActivityNotFoundException ex) {
                    mContext.startActivity(webIntent);
                }
            }
        });
        mUserReviewAdapter = new UserReviewListAdapter(new ArrayList<UserReviewModel>());

        if (savedInstanceState != null){
            mMovieId = savedInstanceState.getLong(MOVIE_ID);
            mMovieTitle = savedInstanceState.getString(MOVIE_TITLE);
            mPosterUrl = savedInstanceState.getString(POSTER_URL);
            mReleaseDate = savedInstanceState.getString(RELEASE_DATE);
            mVoteAverage = savedInstanceState.getFloat(VOTE_AVERAGE);
            mPlot =savedInstanceState.getString(PLOT);
            toggleButton.setChecked(savedInstanceState.getBoolean(TOGGLE));

            Parcelable savedTrailerRecyclerViewState = savedInstanceState.getParcelable(TRAILERS_RECYCLER_LAYOUT);
            mTrailerRecyclerView.getLayoutManager().onRestoreInstanceState(savedTrailerRecyclerViewState);

            ArrayList<TrailerModel> savedTrailerData = savedInstanceState.getParcelableArrayList(TRAILERS_RECYCLER_DATA);

            if (savedTrailerData != null && !savedTrailerData.isEmpty()) {
                mTrailers.clear();
                mTrailers.addAll(savedTrailerData);
                mTrailerAdapter.clear();
                mTrailerAdapter.addAll(savedTrailerData);
                mNoTrailerTextView.setVisibility(View.GONE);
            } else {
                mNoTrailerTextView.setText(R.string.no_trailers);
                mNoTrailerTextView.setVisibility(View.VISIBLE);
            }
            mTrailerRecyclerView.setAdapter(mTrailerAdapter);

            Parcelable savedReviewRecyclerViewState = savedInstanceState.getParcelable(REVIEWS_RECYCLER_LAYOUT);
            mUserReviewRecyclerView.getLayoutManager().onRestoreInstanceState(savedReviewRecyclerViewState);
            ArrayList<UserReviewModel> savedReviewData = savedInstanceState.getParcelableArrayList(REVIEWS_RECYCLER_DATA);
            if (savedReviewData != null && !savedReviewData.isEmpty()) {
                mReviews.clear();
                mReviews.addAll(savedReviewData);
                mUserReviewAdapter.clear();
                mUserReviewAdapter.addAll(savedReviewData);
                mNoUserReviewTextView.setVisibility(View.GONE);
            } else {
                mNoUserReviewTextView.setText(R.string.no_reviews);
                mNoUserReviewTextView.setVisibility(View.VISIBLE);
            }
            mUserReviewRecyclerView.setAdapter(mUserReviewAdapter);
            handleToggleButton(savedInstanceState.getBoolean(TOGGLE));
            DisplayData(true);
        } else {
            Intent data = getIntent();
            MovieModel movie = data.getParcelableExtra(MOVIE);

            mMovieId = movie.getId();
            mMovieTitle = movie.getTitle();
            mPosterUrl = movie.getPosterUrl();
            mReleaseDate = movie.getReleaseDate();
            mVoteAverage = movie.getVoteAverage();
            mPlot = movie.getOverview();
            toggleButton.setChecked(false);

            new selectMovieTask().execute();
            DisplayData(false);
        }
    }

    private void handleToggleButton(Boolean movieFound){
        final Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_star_white_18);

        if (movieFound) {
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
                final Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_star_white_18);
                if (isChecked) {
                    d.setColorFilter(getResources().getColor(R.color.colorProgressTint), PorterDuff.Mode.MULTIPLY);
                    toggleButton.setBackgroundDrawable(d);
                    new insertMovieTask().execute();
                } else {
                    d.setColorFilter(getResources().getColor(R.color.colorBlank), PorterDuff.Mode.MULTIPLY);
                    toggleButton.setBackgroundDrawable(d);
                    new deleteMovieTask().execute();
                }
            }
        });
    }

    private void DisplayData(Boolean fromSavedInstance) {
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

            if (!fromSavedInstance) loadTrailersAndUserReviews();
        } catch (Exception e) {
            Log.v(LOG_TAG, e.toString());
        }
    }

    private void loadTrailersAndUserReviews() {
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
                mTrailers.clear();
                mTrailers.addAll(trailerModels);
                mTrailerAdapter.addAll(trailerModels);
                mNoTrailerTextView.setVisibility(View.GONE);
            } else {
                mNoTrailerTextView.setText(R.string.no_trailers);
                mNoTrailerTextView.setVisibility(View.VISIBLE);
            }
            mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        }

        @Override
        protected List<TrailerModel> doInBackground(Void... voids) {
            Controller controller = new Controller();
            return controller.getTrailers(mMovieId, mApiKey);
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
                mReviews.clear();
                mReviews.addAll(userReviewModels);
                mUserReviewAdapter.addAll(userReviewModels);
                mNoUserReviewTextView.setVisibility(View.GONE);
            } else {
                mNoUserReviewTextView.setText(R.string.no_reviews);
                mNoUserReviewTextView.setVisibility(View.VISIBLE);
            }
            mUserReviewRecyclerView.setAdapter(mUserReviewAdapter);
            mUserReviewRecyclerView.addItemDecoration(new DividerItemDecoration(mUserReviewRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }

        @Override
        protected List<UserReviewModel> doInBackground(Void... voids) {
            Controller controller = new Controller();
            return controller.getUserReviews(mMovieId, mApiKey);
        }
    }

    private class selectMovieTask extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            handleToggleButton(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
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
    }

    private class insertMovieTask extends AsyncTask<Void,Void,Uri>{
        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            if (uri != null){
                Toast.makeText(mContext, R.string.add_favorite_movie, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.add_favorite_movie_unsuccessful, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(MovieEntry._ID, mMovieId);
            values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
            values.put(MovieEntry.COLUMN_MOVIE_POSTER, mPosterUrl);
            values.put(MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
            values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
            values.put(MovieEntry.COLUMN_OVERVIEW, mPlot);

            // Insert a new row for each movie into the provider using the ContentResolver.
            return getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        }
    }

    private class deleteMovieTask extends AsyncTask<Void,Void,Integer>{
        @Override
        protected void onPostExecute(Integer rowsDeleted) {
            super.onPostExecute(rowsDeleted);
            if (rowsDeleted > 0) {
                Toast.makeText(mContext, R.string.delete_favorite_movie, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.delete_favorite_movie_failed, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return getContentResolver().delete(MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build(), null, null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRAILERS_RECYCLER_LAYOUT, mTrailerRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(TRAILERS_RECYCLER_DATA, mTrailers);
        outState.putParcelable(REVIEWS_RECYCLER_LAYOUT, mUserReviewRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(REVIEWS_RECYCLER_DATA, mReviews);
        outState.putLong(MOVIE_ID,mMovieId);
        outState.putString(MOVIE_TITLE,mMovieTitle);
        outState.putString(POSTER_URL,mPosterUrl);
        outState.putString(RELEASE_DATE,mReleaseDate);
        outState.putFloat(VOTE_AVERAGE,mVoteAverage);
        outState.putString(PLOT,mPlot);
        outState.putBoolean(TOGGLE,toggleButton.isChecked());
        outState.putIntArray(SCROLL_POSITION,
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray(SCROLL_POSITION);
        if(position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }
}
