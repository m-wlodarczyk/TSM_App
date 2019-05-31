package com.example.marcin.eventagregator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.marcin.eventagregator.domain.Event;
import com.example.marcin.eventagregator.domain.NotificationTime;

import java.util.ArrayList;

public class DbNotificationsTime
{
    public static ArrayList<NotificationTime> getAll(Context context)
    {
        NotificationsTimeBeforeEventDbHelper dbHelper = new NotificationsTimeBeforeEventDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(
                NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        final ArrayList<NotificationTime> notificationsTime = new ArrayList();
        while (cursor.moveToNext())
        {
            int itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent._ID));
            int days = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.COLUMN_NAME_DAYS));
            int hours = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.COLUMN_NAME_HOURS));

            NotificationTime notificationTime = new NotificationTime(itemId, days, hours);
            notificationsTime.add(notificationTime);
        }
        cursor.close();

        return notificationsTime;
    }

    public static boolean exists(int id, Context context)
    {
        NotificationsTimeBeforeEventDbHelper dbHelper = new NotificationsTimeBeforeEventDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = {NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent._ID};
        String selection = NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent._ID + " = " + id;
        Cursor cursor = db.query(
                NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.TABLE_NAME,
                projection,
                selection,
                null ,
                null,
                null,
                null
        );

        ArrayList<Integer> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            int itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        if (itemIds.size() > 0)
            return  true;
        else
            return  false;
    }

    public static void deleteById(int notificationId, Context context)
    {
        NotificationsTimeBeforeEventDbHelper dbHelper = new NotificationsTimeBeforeEventDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent._ID + " = ?";
        String[] selectionArgs = {Long.toString(notificationId)};
        db.delete(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.TABLE_NAME, selection, selectionArgs);

    }

    public static void insert(NotificationTime notificationTime, Context context)
    {
        NotificationsTimeBeforeEventDbHelper dbHelper = new NotificationsTimeBeforeEventDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.COLUMN_NAME_DAYS, notificationTime.getDaysBeforeEvent());
        values.put(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.COLUMN_NAME_HOURS, notificationTime.getHoursBeforeEvent());

        Log.d("wwww", values.toString());

        db.insert(NotificationsTimeBeforeEventDbContract.NotificationTimeBeforeEvent.TABLE_NAME, null, values);

    }

}
