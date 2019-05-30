package com.example.marcin.eventagregator;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

        final ArrayList<Event> interestingEvents = Db.getAll(getContext());
//        String pattern = "yyyy-MM-dd HH:mm:ss";
        String withoutTimePattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(withoutTimePattern);
        Calendar calendar = Calendar.getInstance();
        String currentDateString = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        for (Event event : interestingEvents)
        {
            try
            {
                Date currentDate = sdf.parse(currentDateString);
                Date eventDate = sdf.parse(event.getDate());
                if (eventDate.before(currentDate))
                {
                    // event has been ended
                    interestingEvents.remove(event);
                    Db.deleteById(event.getId(), getContext());
                }
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }


        eventListView = view.findViewById(R.id.list);
        eventListAdapter = new EventListAdapter(interestingEvents, getContext());
        eventListView.setAdapter(eventListAdapter);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Event event = interestingEvents.get(position);

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
