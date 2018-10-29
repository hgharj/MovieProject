package com.example.android.movieproject.utils;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller implements Callback<List<MovieModel>> {
    static final String BASE_URL = "https://api.themoviedb.org/";
private List<MovieModel> mMovieList;
    public List<MovieModel> start(String sortBy,String apiKey) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieDbAPIList movieDbAPIList = retrofit.create(MovieDbAPIList.class);

        Call<List<MovieModel>> call;
        if (sortBy.contains("popular")) {
            call = movieDbAPIList.getMostPopularMovies(sortBy,apiKey);
        } else if (sortBy.contains("vote_average")) {
            call = movieDbAPIList.getTopRatedMovies(sortBy,apiKey);
        } else {
            call = movieDbAPIList.getMovie(sortBy,apiKey);
        }
        call.enqueue(this);
        return mMovieList;
    }

    @Override
    public void onResponse(Call<List<MovieModel>> call, Response<List<MovieModel>> response) {
        if(response.isSuccessful()) {
            List<MovieModel> changesList = response.body();
            mMovieList=changesList;
            for (MovieModel change : changesList) {
                System.out.println(change.getTitle());

            }
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<MovieModel>> call, Throwable t) {
        t.printStackTrace();
    }
}

