package com.example.android.movieproject.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.movieproject.provider.TrailerContract.TrailerEntry;


public class TrailerDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "trailer.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public TrailerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the plants data
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " STRING PRIMARY KEY," +
                TrailerEntry.COLUMN_TRAILER_NAME + " STRING NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_KEY + " STRING NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_TYPE + " STRING NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
