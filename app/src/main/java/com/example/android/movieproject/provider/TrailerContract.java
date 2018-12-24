package com.example.android.movieproject.provider;

import android.net.Uri;
import android.provider.BaseColumns;


public class TrailerContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.movieproject";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "trailers" directory
    public static final String PATH_TRAILERS = "trailers";

    public static final String INVALID_TRAILER_ID = "";

    public static final class TrailerEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_TRAILER_NAME = "name";
        public static final String COLUMN_TRAILER_KEY = "key";
        public static final String COLUMN_TRAILER_TYPE = "type";
    }

}
