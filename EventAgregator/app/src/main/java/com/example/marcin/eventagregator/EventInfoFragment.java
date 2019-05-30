package com.example.marcin.eventagregator;

import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        // ad banner
        mAdView = view.findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
//        builder.setLocation();
        mAdView.loadAd(builder.build());

        ImageButton sharebutton = view.findViewById(R.id.share_button);
        sharebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, event.getTitle()); // TODO set url
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


        // check if event is saved as interesting in DB
        existsInDB = Db.exists(event.getId(), getContext());
        final Button button = view.findViewById(R.id.add_to_interesting_button);
        if (existsInDB)
        {
            button.setText("Usuń z interesujących");
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_grade_gold_36dp, 0);
        } else
        {
            button.setText("Dodaj do interesujących");
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        String withoutTimePattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(withoutTimePattern);
        Calendar calendar = Calendar.getInstance();
        String currentDateString = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        try
        {
            Date currentDate = sdf.parse(currentDateString);
            Date eventDate = sdf.parse(event.getDate());

            if (eventDate.before(currentDate))
            {
                button.setText("Wydarzenie zakończone");
            }
            else
            {
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (existsInDB)
                        {
                            Db.deleteById(event.getId(), getContext());
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Usunięto z interesujących.", Snackbar.LENGTH_SHORT).show();
                            existsInDB = false;
                            button.setText("Dodaj do interesujących");
                            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        } else
                        {
                            Db.insert(event, getContext());
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Dodano do interesujących.", Snackbar.LENGTH_SHORT).show();
                            existsInDB = true;
                            button.setText("Usuń z interesujących");
                            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_grade_gold_36dp, 0);

                        }

                    }
                });
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }




        List<MarkerOptions> markers = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getContext());

        List<Address> addressList = null;
        try
        {
            addressList = geocoder.getFromLocationName(event.getAddress(), 1);
        } catch (IOException e)
        {
            Log.d("exception: ", e.getMessage());
        }

        if (!addressList.isEmpty())
        {
            Address address1 = addressList.get(0);
            double x = address1.getLatitude();
            double y = address1.getLongitude();
            LatLng latLng = new LatLng(x, y);
            markers.add(new MarkerOptions().position(latLng).title(event.getTitle()));

            Fragment mapFragment = new MapFragment();
            ((MapFragment) mapFragment).setMarkers(markers, getContext());
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.map, mapFragment);
            transaction.commit();
        } else
        {
            //TODO empty fragment
        }

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
