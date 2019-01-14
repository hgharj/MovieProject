package com.example.android.movieproject.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailerModel implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("type")
    @Expose
    private String type;

    private String trailerPath;

    private static final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";

    public TrailerModel(String id, String name, String key, String type) {
        this.key = key;
        this.trailerPath = TRAILER_BASE_URL + key;
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTrailerUrl() {
        return TRAILER_BASE_URL + key;
    }

    public void setTrailerUrl(String key) {
        this.trailerPath = TRAILER_BASE_URL + key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TrailerModel(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.key = parcel.readString();
        this.type = parcel.readString();
    }

    //creator - used when un-parceling our parcel (creating the object)
    public static final Creator CREATOR = new Creator() {
        @Override
        public TrailerModel createFromParcel(Parcel parcel) {
            return new TrailerModel(parcel);
        }

        @Override
        public TrailerModel[] newArray(int i) {
            return new TrailerModel[0];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(TRAILER_BASE_URL + key);
        dest.writeString(type);
    }
}
