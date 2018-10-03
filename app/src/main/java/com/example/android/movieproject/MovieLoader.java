package com.example.android.movieproject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.movieproject.utils.MovieDBService;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.MovieResponse;
import com.example.android.movieproject.utils.NetworkUtils;

import java.util.List;

class MovieLoader extends AsyncTaskLoader<List<MovieModel>> {

    private String mUrl;
    private String mSortBy;
    private String mApiKey;

    public MovieLoader(Context context, String url){
        super(context);
        mUrl=url;
    }

    public MovieLoader(Context context, String sortBy, String apiKey){
        super(context);
        mSortBy=sortBy;
        mApiKey=apiKey;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

//    @Override
//    public List<Movie> loadInBackground() {
//        if(mUrl == null) {
//            return null;
//        }
//
//        List<Movie> movies = NetworkUtils.fetchMovieData(mUrl);
//        return movies;
//    }

    @Override
    public List<MovieModel> loadInBackground() {
        if(mSortBy == null) {
            return null;
        }

        if(mApiKey == null) {
            return null;
        }

        MovieDBService movieDBService = new MovieDBService();

        List<MovieModel> movies = movieDBService.getMovieResponse(mSortBy,mApiKey);
        return movies;
    }
}
