package com.example.android.movieproject.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller {
    private static final String BASE_URL = "https://api.themoviedb.org/";
    private List<MovieModel> mMovieList;
    private List<TrailerModel> mTrailerList;
    private List<UserReviewModel> mReviewList;
    private static final String LOG_TAG = "Controller";
    private static final String POPULAR = "popular";
    private static final String VOTE_AVERAGE = "vote_average";

    public List<MovieModel> getMovieList(String sortBy, String apiKey) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieDbAPI movieDbAPI = retrofit.create(MovieDbAPI.class);

        Call<MovieResponse> call;
        if (sortBy.contains(POPULAR)) {
            call = movieDbAPI.getMostPopularMovies(sortBy, apiKey);
        } else if (sortBy.contains(VOTE_AVERAGE)) {
            call = movieDbAPI.getTopRatedMovies(sortBy, apiKey);
        } else {
            call = movieDbAPI.getMovie(sortBy, apiKey);
        }

        try {
            Response<MovieResponse> response = call.execute();
            if (response.errorBody() == null) {
                mMovieList = response.body().getMovieList();
            } else {
                Log.v(LOG_TAG, response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mMovieList;
    }

    public List<TrailerModel> getTrailers(Long id, String apiKey) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieDbAPI movieDbAPI = retrofit.create(MovieDbAPI.class);

        Call<TrailerResponse> call;
        call = movieDbAPI.getRelatedVideos(id, apiKey);

        try {
            Response<TrailerResponse> response = call.execute();
            if (response.errorBody() == null) {
                mTrailerList = response.body().getTrailerList();
            } else {
                Log.v("Controller", response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mTrailerList;
    }

    public List<UserReviewModel> getUserReviews(Long id, String apiKey) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieDbAPI movieDbAPI = retrofit.create(MovieDbAPI.class);

        Call<UserReviewResponse> call;
        call = movieDbAPI.getUserReviews(id, apiKey);

        try {
            Response<UserReviewResponse> response = call.execute();
            if (response.errorBody() == null) {
                mReviewList = response.body().getReviewList();
            } else {
                Log.v(LOG_TAG, response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mReviewList;
    }
}

