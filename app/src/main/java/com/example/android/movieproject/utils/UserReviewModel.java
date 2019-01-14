package com.example.android.movieproject.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserReviewModel implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("content")
    @Expose
    private String content;

    public UserReviewModel(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserReviewModel(Parcel parcel) {
        this.id = parcel.readString();
        this.author = parcel.readString();
        this.content = parcel.readString();
    }

    //creator - used when un-parceling our parcel (creating the object)
    public static final Creator CREATOR = new Creator() {
        @Override
        public UserReviewModel createFromParcel(Parcel parcel) {
            return new UserReviewModel(parcel);
        }

        @Override
        public UserReviewModel[] newArray(int i) {
            return new UserReviewModel[0];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
    }
}
