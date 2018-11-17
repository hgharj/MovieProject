package com.example.android.movieproject.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jcgray on 10/1/18.
 */

public class TrailerResponse {
    @SerializedName("results")
    @Expose
    private List<TrailerModel> trailerList;

    public List<TrailerModel> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<TrailerModel> trailerList) {
        this.trailerList = trailerList;
    }
}
