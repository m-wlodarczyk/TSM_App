package com.example.marcin.eventagregator.database;

import android.provider.BaseColumns;

public final class NotificationsTimeBeforeEventDbContract
{
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private NotificationsTimeBeforeEventDbContract()
    { }

    /* Inner class that defines the table contents */
    public static class NotificationTimeBeforeEvent implements BaseColumns
    {
        public static final String TABLE_NAME = "NotificationsTimeBeforeEvent";
        public static final String COLUMN_NAME_DAYS = "days";
        public static final String COLUMN_NAME_HOURS = "hours";
    }
}
