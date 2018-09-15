package com.example.android.movieproject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.movieproject.utils.NetworkUtils;

import java.util.List;

class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String mUrl;

    public MovieLoader(Context context, String url){
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if(mUrl == null) {
            return null;
        }

        List<Movie> movies = NetworkUtils.fetchMovieData(mUrl);
        return movies;
    }
}
