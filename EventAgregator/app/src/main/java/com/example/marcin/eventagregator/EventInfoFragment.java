package com.example.marcin.eventagregator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventInfoFragment extends Fragment
{
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_event_info, container, false);
        Bundle bundle = getArguments();
        String eventJSON = bundle.getString("event");
        Event event = Event.createFromJSON(eventJSON);

        TextView name = view.findViewById(R.id.event_name);
        TextView date = view.findViewById(R.id.event_date);
        TextView address = view.findViewById(R.id.event_address);
        TextView description = view.findViewById(R.id.event_description);

        name.setText(event.getName());
        date.setText(event.getDate());
        address.setText(event.getAddress());
        description.setText(event.getDescription());
        return view;
    }
}
