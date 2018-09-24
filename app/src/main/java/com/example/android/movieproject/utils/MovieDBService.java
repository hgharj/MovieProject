package com.example.android.movieproject.utils;

import com.example.android.movieproject.BuildConfig;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDBService {
    public MovieResponse getMovieResponse(String sortBy) {

        String apiKey = BuildConfig.ApiKey;
        MovieResponse movie = null;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

//        MovieDbAPI MovieDbAPI = retrofit.create(MovieDbAPI.class);
//        Call<MovieResponse> callSync = MovieDbAPI.getTopRatedMovies(sortBy,apiKey);

//        try {
//            Response<MovieResponse> response = callSync.execute();
//            movie = response.body();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        MovieDbAPI movieDbAPI = retrofit.create(MovieDbAPI.class);
        Call<MovieResponse> callAsync;
        if (sortBy.contains("popular")) {
            callAsync = movieDbAPI.getMostPopularMovies(sortBy,apiKey);
        } else if (sortBy.contains("top_rated")) {
            callAsync = movieDbAPI.getTopRatedMovies(sortBy,apiKey);
        } else {
            callAsync = movieDbAPI.getMovie(sortBy,apiKey);
        }


        callAsync.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                MovieResponse movie = response.body();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });

        return movie;
    }
}
