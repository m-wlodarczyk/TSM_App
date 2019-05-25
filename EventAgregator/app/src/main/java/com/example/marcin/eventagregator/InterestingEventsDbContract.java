package com.example.marcin.eventagregator;

import android.provider.BaseColumns;

public final class InterestingEventsDbContract
{
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private InterestingEventsDbContract()
    { }

    /* Inner class that defines the table contents */
    public static class InterestingEvent implements BaseColumns
    {
        public static final String TABLE_NAME = "InterestingEvent";
        public static final String COLUMN_NAME_ID= "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_DATE= "date";

    }
}
