package com.example.marcin.eventagregator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventInfoFragment extends Fragment
{
    private View view;
    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_event_info, container, false);
        Bundle bundle = getArguments();
        String eventJSON = bundle.getString("event");
        event = Event.createFromJSON(eventJSON);

        TextView name = view.findViewById(R.id.event_title);
        TextView date = view.findViewById(R.id.event_date);
        TextView address = view.findViewById(R.id.event_address);
        TextView description = view.findViewById(R.id.event_description);

        Log.d("koy", event.toString());
        Log.d("koy", event.getDescription());

        name.setText(event.getTitle());
        date.setText(event.getDate());
        address.setText(event.getAddress());
        description.setText(event.getDescription());

        Button addToInterestingButton = view.findViewById(R.id.add_to_interesting_button);
        addToInterestingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InterestingEventsDbHelper dbHelper = new InterestingEventsDbHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ID, event.getId());
                values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_TITLE, event.getTitle());
                values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DESCRIPTION, event.getDescription());
                values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ADDRESS, event.getAddress());
                values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DATE, event.getDate());
                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(InterestingEventsDbContract.InterestingEvent.TABLE_NAME, null, values);


                SQLiteDatabase db2 = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
                String[] projection = {
                        BaseColumns._ID,
                        InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ID,
                        InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_TITLE,
                        InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DESCRIPTION,
                        InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ADDRESS,
                        InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DATE
                };

// Filter results WHERE "name" = 'My Title'
                String selection = InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_TITLE + " = ?";
                String[] selectionArgs = {"My Title"};

// How you want the results sorted in the resulting Cursor
//                String sortOrder =
//                        InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_SUBTITLE + " DESC";

                Cursor cursor = db2.query(
                        InterestingEventsDbContract.InterestingEvent.TABLE_NAME,   // The table to query
                        null,             // The array of columns to return (pass null to get all)
                        null,              // The columns for the WHERE clause
                        null,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        null               // The sort order
                );

                List itemIds = new ArrayList<>();
                ArrayList<Event> eventsList = new ArrayList();
                while (cursor.moveToNext())
                {
                    long itemId = cursor.getLong(
                            cursor.getColumnIndexOrThrow(InterestingEventsDbContract.InterestingEvent._ID));
                    Integer id = Integer.parseInt(cursor.getString(1));
                    String name = cursor.getString(2);
                    String description = cursor.getString(3);
                    String address = cursor.getString(4);
                    String date = cursor.getString(5);
                    Event event = new Event(id, name, description, address, date);
                    eventsList.add(event);
                }
                cursor.close();
                for (Event event : eventsList)
                {
                    Log.d("koy", event.toString());

                }

            }
        });
        return view;
    }
}
