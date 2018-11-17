package com.example.android.movieproject.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jcgray on 10/1/18.
 */

public class UserReviewResponse {
    @SerializedName("results")
    @Expose
    private List<UserReviewModel> reviewList;

    public List<UserReviewModel> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<UserReviewModel> reviewList) {
        this.reviewList = reviewList;
    }
}
