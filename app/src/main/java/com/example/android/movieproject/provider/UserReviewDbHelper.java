package com.example.android.movieproject.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.movieproject.provider.UserReviewContract.UserReviewEntry;


public class UserReviewDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "userreviews.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public UserReviewDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the plants data
        final String SQL_CREATE_USER_REVIEW_TABLE = "CREATE TABLE " + UserReviewEntry.TABLE_NAME + " (" +
                UserReviewEntry._ID + " TEXT PRIMARY KEY," +
                UserReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                UserReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
