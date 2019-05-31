package com.example.marcin.eventagregator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.marcin.eventagregator.database.DbInterestingEvents;
import com.example.marcin.eventagregator.domain.Event;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class InterestingEventsListFragment extends Fragment
{

    private View view;
    private AdView mAdView;

    private static EventListAdapter eventListAdapter;
    private ListView eventListView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_interesting_events_list, container, false);

        mAdView = view.findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
//        builder.setLocation();
        mAdView.loadAd(builder.build());

        final ArrayList<Event> interestingEvents = DbInterestingEvents.getAll(getContext());

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
