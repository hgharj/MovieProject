package com.example.android.movieproject.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.movieproject.provider.MovieContract.MovieEntry;


public class UserReviewDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "movie.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public UserReviewDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the plants data
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_TITLE + " STRING NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TIMESTAMP NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " STRING NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
