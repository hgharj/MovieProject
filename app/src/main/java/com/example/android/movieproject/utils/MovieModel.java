package com.example.android.movieproject.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.movieproject.Movie;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieModel implements Parcelable{
    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("vote_average")
    @Expose
    private Float voteAverage;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("overview")
    @Expose
    private String overview;

    private static final String POSTER_SIZE = "w500";
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

//    public MovieModel(Long id, String title, String posterPath,  String releaseDate, Float voteAverage, String overview) {
//        this.posterPath = POSTER_BASE_URL + POSTER_SIZE + posterPath;
//        this.releaseDate = releaseDate;
//        this.id = id;
//        this.title = title;
//        this.voteAverage = voteAverage;
//        this.overview = overview;
//    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return POSTER_BASE_URL + POSTER_SIZE + posterPath;
    }

    public void setPosterUrl(String posterPath) {
        this.posterPath = POSTER_BASE_URL + POSTER_SIZE + posterPath;
    }

    public String getReleaseDate() {
        return MovieUtils.convertYYYY_MM_DD_MiddleEndian(releaseDate);
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public MovieModel(Parcel parcel){
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.posterPath = parcel.readString();
        this.releaseDate = parcel.readString();
        this.voteAverage = parcel.readFloat();
        this.overview = parcel.readString();
    }

    //creator - used when un-parceling our parcel (creating the object)
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public MovieModel createFromParcel(Parcel parcel) {
            return new MovieModel(parcel);
        }

        @Override
        public MovieModel[] newArray(int i) {
            return new MovieModel[0];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeFloat(voteAverage);
        dest.writeString(overview);
    }
}
