package com.example.android.movieproject;

import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.movieproject.utils.Controller;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.TrailerModel;
import com.example.android.movieproject.utils.UserReviewModel;
import com.squareup.picasso.Picasso;

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

    private static final String LOG_TAG = DetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);
        Intent data = getIntent();
        MovieModel movie = data.getParcelableExtra("Movie");

        long movieId = movie.getId();
        String movieTitle = movie.getTitle();
        String posterUrl = movie.getPosterUrl();
        String releaseDate = movie.getReleaseDate();
        float voteAverage = movie.getVoteAverage();
        String plot = movie.getOverview();

        DisplayData(movieId, movieTitle, posterUrl, releaseDate, voteAverage, plot);
    }

    static AsyncTaskLoader mGetTrailers;
    static AsyncTaskLoader mGetUserReviews;
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

            mGetTrailers = new AsyncTaskLoader<List<TrailerModel>>(this) {
                @Override
                public List<TrailerModel> loadInBackground() {
                    String apiKey = "78f8b58674adbaa0bf92f4de4e9a6dc3";
                    Controller controller = new Controller();
                    return controller.getTrailers(movieId,apiKey);
                }
            };

            mGetTrailers.forceLoad();

            mGetUserReviews = new AsyncTaskLoader<List<UserReviewModel>>(this) {
                @Override
                public List<UserReviewModel> loadInBackground() {
                    String apiKey = "78f8b58674adbaa0bf92f4de4e9a6dc3";
                    Controller controller = new Controller();
                    return controller.getReviews(movieId,apiKey);
                }
            };

            mGetUserReviews.forceLoad();
        }
        catch (Exception e) {
            Log.v(LOG_TAG,e.toString());
        }

    }
}
