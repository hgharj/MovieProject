package com.example.android.movieproject;

public class Movies {
    private String mMovieTitle;
    private String mPosterImg;
    private String mReleaseDate;
    private float mVoteAvg;
    private String mPlot;

    public Movies(String movieTitle, String posterImage, String releaseDate, float voteAverage, String plot){
        mMovieTitle = movieTitle;
        mPosterImg = posterImage;
        mReleaseDate = releaseDate;
        mVoteAvg = voteAverage;
        mPlot = plot;
    }

    public String getMovieTitle() { return mMovieTitle;}
    public String getPosterImage() { return mPosterImg;}
    public String getReleaseDate() { return mReleaseDate;}
    public float getVoteAverage() { return mVoteAvg;}
    public String getPlot() { return mPlot;}

}
