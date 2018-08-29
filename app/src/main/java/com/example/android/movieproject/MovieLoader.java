package com.example.android.movieproject;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.movieproject.utils.NetworkUtils;

import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String mUrl;

    public MovieLoader(Context context, String url){
        super(context);
        mUrl=url;
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
