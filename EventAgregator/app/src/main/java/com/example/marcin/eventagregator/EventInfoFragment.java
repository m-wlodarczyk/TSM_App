package com.example.marcin.eventagregator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventInfoFragment extends Fragment
{
    private View view;
    private Event event;
    private AdView mAdView;
    private boolean existsInDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_event_info, container, false);

        final Bundle bundle = getArguments();
        String eventJSON = bundle.getString("event");
        event = Event.createFromJSON(eventJSON);

        // check if event is saved as interesting in DB
        InterestingEventsDbHelper dbHelper = new InterestingEventsDbHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InterestingEventsDbContract.InterestingEvent._ID
        };

        // Filter results WHERE "id" = event.id'
        String selection = InterestingEventsDbContract.InterestingEvent._ID + " = " + event.getId();

        Cursor cursor = db.query(
                InterestingEventsDbContract.InterestingEvent.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        ArrayList<Long> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(InterestingEventsDbContract.InterestingEvent._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        existsInDB = itemIds.size() > 0;
        final Button button = view.findViewById(R.id.add_to_interesting_button);
        if (existsInDB)
        {
            button.setText("Usuń z interesujących");
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_grade_gold_36dp, 0);

        }
        else
        {
            button.setText("Dodaj do interesujących");
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (existsInDB)
                {
                    String selection = InterestingEventsDbContract.InterestingEvent._ID + " = ?";
                    String[] selectionArgs = {Long.toString(event.getId())};
                    db.delete(InterestingEventsDbContract.InterestingEvent.TABLE_NAME, selection, selectionArgs);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Usunięto z interesujących.", Snackbar.LENGTH_SHORT).show();
                    existsInDB = false;
                    button.setText("Dodaj do interesujących");
                    button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                }
                else
                {
                    ContentValues values = new ContentValues();
                    values.put(InterestingEventsDbContract.InterestingEvent._ID, event.getId());
                    values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_TITLE, event.getTitle());
                    values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DESCRIPTION, event.getDescription());
                    values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_ADDRESS, event.getAddress());
                    values.put(InterestingEventsDbContract.InterestingEvent.COLUMN_NAME_DATE, event.getDate());
                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(InterestingEventsDbContract.InterestingEvent.TABLE_NAME, null, values);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Dodano do interesujących.", Snackbar.LENGTH_SHORT).show();
                    existsInDB = true;
                    button.setText("Usuń z interesujących");
                    button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_grade_gold_36dp, 0);

                }



            }
        });


        LatLngBounds.Builder bounds = LatLngBounds.builder();
        List<MarkerOptions> markers = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getContext());

        List<Address> addressList = null;
        try
        {
            addressList = geocoder.getFromLocationName(event.getAddress(), 1);
        }
        catch (IOException e)
        {
            Log.d("exception: ", e.getMessage());
        }

        Address address1 = addressList.get(0);
        double x = address1.getLatitude();
        double y = address1.getLongitude();
        LatLng latLng = new LatLng(x, y);
        markers.add(new MarkerOptions().position(latLng).title(event.getTitle()));
        bounds.include(new LatLng(x, y));
        // add additional points to set zoom
        bounds.include(new LatLng(x-0.01, y));
        bounds.include(new LatLng(x, y+0.01));

        Fragment mapFragment = new MapFragment();
        ((MapFragment) mapFragment).setMarkers(markers, getContext());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.map, mapFragment);
        transaction.commit();

        mAdView = view.findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
//        builder.setLocation();
        mAdView.loadAd(builder.build());

        TextView name = view.findViewById(R.id.event_title);
        TextView date = view.findViewById(R.id.event_date);
        TextView address = view.findViewById(R.id.event_address);
        TextView description = view.findViewById(R.id.event_description);

        name.setText(event.getTitle());
        date.setText(event.getDate());
        address.setText(event.getAddress());
        description.setText(event.getDescription());



        return view;
    }
}
