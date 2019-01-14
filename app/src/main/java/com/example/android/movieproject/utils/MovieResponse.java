package com.example.android.movieproject.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jcgray on 10/1/18.
 */

public class MovieResponse {
    @SerializedName("results")
    @Expose
    private List<MovieModel> movieList;

    public List<MovieModel> getMovieList() {
        return movieList;
    }
}
