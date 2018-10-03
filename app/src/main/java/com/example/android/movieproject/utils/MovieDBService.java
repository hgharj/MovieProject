package com.example.android.movieproject.utils;

import com.example.android.movieproject.BuildConfig;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDBService {
    public List<MovieModel> getMovieResponse(String sortBy,String apiKey) {

        MovieModel movie = null;

        List<MovieModel> movies = null;
        MovieResponse response = null;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

//        MovieDbAPI MovieDbAPI = retrofit.create(MovieDbAPI.class);
//        Call<MovieModel> callSync = MovieDbAPI.getTopRatedMovies(sortBy,apiKey);

//        try {
//            Response<MovieModel> response = callSync.execute();
//            movie = response.body();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        MovieDbAPI movieDbAPI = retrofit.create(MovieDbAPI.class);
        Call<MovieResponse> callAsync;
        if (sortBy.contains("popular")) {
            callAsync = movieDbAPI.getMostPopularMovies(sortBy,apiKey);
        } else if (sortBy.contains("vote_average")) {
            callAsync = movieDbAPI.getTopRatedMovies(sortBy,apiKey);
        } else {
            callAsync = movieDbAPI.getMovie(sortBy,apiKey);
        }


        callAsync.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                MovieResponse movieResponse = response.body();
//                List<MovieModel> movies = movieResponse.getItems();

            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });

        return movies;
    }
}
