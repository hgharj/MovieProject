package com.example.android.movieproject;

import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.movieproject.utils.Controller;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.TrailerModel;
import com.example.android.movieproject.utils.UserReviewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
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
    private static final String LOG_TAG = DetailActivity.class.getName();
    private long mMovieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);
        Intent data = getIntent();
        MovieModel movie = data.getParcelableExtra("Movie");

        mMovieId = movie.getId();
        String movieTitle = movie.getTitle();
        String posterUrl = movie.getPosterUrl();
        String releaseDate = movie.getReleaseDate();
        float voteAverage = movie.getVoteAverage();
        String plot = movie.getOverview();

        DisplayData(mMovieId, movieTitle, posterUrl, releaseDate, voteAverage, plot);
    }

    private AsyncTask mGetTrailers;
    static AsyncTask mGetUserReviews;
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
}
