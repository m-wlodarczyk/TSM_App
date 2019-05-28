package com.example.marcin.eventagregator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class InterestingEventsListFragment extends Fragment
{

    View view;
    private static EventListAdapter eventListAdapter;
    private ListView eventListView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_interesting_events_list, container, false);

        InterestingEventsDbHelper dbHelper = new InterestingEventsDbHelper(getContext());
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
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(InterestingEventsDbContract.InterestingEvent._ID));
            Integer id = Integer.parseInt(cursor.getString(0));
            String nameS = cursor.getString(1);
            String descriptionS = cursor.getString(2);
            String addressS = cursor.getString(3);
            String dateS = cursor.getString(4);
            Event event = new Event(id, nameS, descriptionS, addressS, dateS);
            eventsList.add(event);
            itemIds.add(itemId);
        }
        cursor.close();
        for (Event event : eventsList)
        {
            Log.d("koy", event.toString());
        }

        eventListView = view.findViewById(R.id.list);
        eventListAdapter = new EventListAdapter(eventsList, getContext());
        eventListView.setAdapter(eventListAdapter);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // TODO
                Event event = eventsList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("event", event.toJSON());

                Fragment newFragment = new EventInfoFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();


            }
        });

        return  view;
    }
}
