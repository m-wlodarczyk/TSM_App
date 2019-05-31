package com.example.marcin.eventagregator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InterestingEventsDbHelper extends SQLiteOpenHelper
{
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InterestingEventsDbContract.InterestingEvent.TABLE_NAME + " (" +
                    InterestingEventsDbContract.InterestingEvent._ID + " INTEGER PRIMARY KEY," +
//                    InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ID + " TEXT," +
                    InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_TITLE + " TEXT," +
                    InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ADDRESS + " TEXT," +
                    InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DATE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InterestingEventsDbContract.InterestingEvent.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "InterestingEvents.db";

    public InterestingEventsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
