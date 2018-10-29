package com.example.android.movieproject.utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbAPI{
    @GET("/3/movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("sort_by") String sortBy, @Query("api_key") String key);

    @GET("/3/movie/most_popular")
    Call<MovieResponse> getMostPopularMovies(@Query("sort_by") String sortBy, @Query("api_key") String key);

    @GET("/3/movie/{id}/videos")
    Call<MovieResponse> getRelatedVideos(@Path("id") String id, @Query("api_key") String key);

    @GET("/3/movie/{id}/reviews")
    Call<MovieResponse> getUserReviews(@Path("id") String id, @Query("api_key") String key);

    @GET("/3/movie/{id}")
    Call<MovieResponse> getMovie(@Path("id") String id, @Query("api_key") String key);
}


