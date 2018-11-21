package com.example.android.movieproject.provider;

import android.net.Uri;
import android.provider.BaseColumns;


public class UserReviewContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.movieproject";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "userreviews" directory
    public static final String PATH_USER_REVIEWS = "userreviews";

    public static final long INVALID_USER_REVIEW_ID = -1;

    public static final class UserReviewEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_REVIEWS).build();

        public static final String TABLE_NAME = "userreviews";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
    }

}
