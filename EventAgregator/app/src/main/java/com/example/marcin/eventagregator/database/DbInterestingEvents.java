package com.example.marcin.eventagregator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.marcin.eventagregator.domain.Event;

import java.util.ArrayList;

public class DbInterestingEvents
{
    public static ArrayList<Event> getAll(Context context)
    {
        InterestingEventsDbHelper dbHelper = new InterestingEventsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(
                InterestingEventsDbContract.InterestingEvent.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        ArrayList<Long> itemIds = new ArrayList<>();
        final ArrayList<Event> eventsList = new ArrayList();
        while (cursor.moveToNext())
        {
            Long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(InterestingEventsDbContract.InterestingEvent._ID));
            int id = Integer.parseInt(cursor.getString(0));
            String nameS = cursor.getString(1);
            String descriptionS = cursor.getString(2);
            String addressS = cursor.getString(3);
            String urlS = cursor.getString(4);
            String dateS = cursor.getString(5);

            Event event = new Event(id, nameS, descriptionS, addressS, dateS, urlS);
            eventsList.add(event);
            itemIds.add(itemId);
        }
        cursor.close();

        return eventsList;
    }

    public static boolean exists(Integer id, Context context)
    {
        InterestingEventsDbHelper dbHelper = new InterestingEventsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = {InterestingEventsDbContract.InterestingEvent._ID};
        String selection = InterestingEventsDbContract.InterestingEvent._ID + " = " + id;
        Cursor cursor = db.query(
                InterestingEventsDbContract.InterestingEvent.TABLE_NAME,
                projection,
                selection,
                null ,
                null,
                null,
                null
        );

        ArrayList<Long> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(InterestingEventsDbContract.InterestingEvent._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        if (itemIds.size() > 0)
            return  true;
        else
            return  false;
    }

    public static void deleteById(Integer eventId, Context context)
    {
        InterestingEventsDbHelper dbHelper = new InterestingEventsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = InterestingEventsDbContract.InterestingEvent._ID + " = ?";
        String[] selectionArgs = {Long.toString(eventId)};
        db.delete(InterestingEventsDbContract.InterestingEvent.TABLE_NAME, selection, selectionArgs);

    }

    public static void insert(Event event, Context context)
    {
        InterestingEventsDbHelper dbHelper = new InterestingEventsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InterestingEventsDbContract.InterestingEvent._ID, event.getId());
        values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_TITLE, event.getTitle());
        values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DESCRIPTION, event.getDescription());
        values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ADDRESS, event.getAddress());
        values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DATE, event.getDate());
        values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_URL, event.getUrl());

        db.insert(InterestingEventsDbContract.InterestingEvent.TABLE_NAME, null, values);

    }


}
