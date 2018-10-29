package com.example.android.movieproject.utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbAPIList {
    @GET("3/movie/top_rated")
    Call<List<MovieModel>> getTopRatedMovies(@Query("sort_by") String sortBy, @Query("api_key") String key);

    @GET("3/movie/most_popular")
    Call<List<MovieModel>> getMostPopularMovies(@Query("sort_by") String sortBy, @Query("api_key") String key);

    @GET("3/movie/{id}/videos")
    Call<List<MovieModel>> getRelatedVideos(@Path("id") String id, @Query("api_key") String key);

    @GET("3/movie/{id}/reviews")
    Call<List<MovieModel>> getUserReviews(@Path("id") String id, @Query("api_key") String key);

    @GET("3/movie/{id}")
    Call<List<MovieModel>> getMovie(@Path("id") String id, @Query("api_key") String key);
}


