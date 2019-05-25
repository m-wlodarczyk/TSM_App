package com.example.marcin.eventagregator;

import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.marcin.eventagregator.TodayEventsListFragment.convert;

public class TodayEventsMapFragment extends Fragment
{

    private View view;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_today_events_map, container, false);


        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://www.poznan.pl/mim/public/ws-information/?co=getCurrentDayEvents";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        // Display the first 500 characters of the response string.
                        Log.d("123", "Response is: " + response.length());
                        String xmlString = response.substring(response.indexOf("<root"), response.indexOf("</root>") + 7);

                        JSONObject obj = convert(xmlString);
                        EventList eventList = null;
                        try
                        {
                            eventList = new EventList(obj.getJSONObject("root").getJSONArray("event"));

                        } catch (JSONException e)
                        {
                            Log.d("exception", e.getMessage());
                        }
                        for (int i = 0; i < 5; i++)
                        {
                            Log.d("koy", eventList.get(i).toString());
                        }
                        final ArrayList<Event> arrayListEvents = eventList.getEvents();

                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Ładowanie");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        new Thread(new Runnable()
                        {
                            public void run()
                            {

                                bounds = LatLngBounds.builder();
                                names = new ArrayList<>();

                                for (Event x : arrayListEvents)
                                {
                                    if (findOnMap(x.getAddress()))
                                    {
                                        names.add(x.getTitle());
                                    }
                                }
                                progressDialog.dismiss();

                                Fragment mapFragment = new MapFragment();
                                ((MapFragment) mapFragment).setMarkers(markers, bounds, names);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.map, mapFragment);
                                transaction.commit();
                            }
                        }).start();


                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("exception", error.getMessage());
                Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);


        return view;
    }

    private LatLngBounds.Builder bounds;
    private ArrayList<String> names;
    private List<LatLng> markers = new ArrayList<>();

    public boolean findOnMap(String locationName)
    {
        Log.d("kot", "findOnMap");

        Geocoder geocoder = new Geocoder(getContext());
        try
        {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            Address address = addressList.get(0);
            double x = address.getLatitude();
            double y = address.getLongitude();
            markers.add(new LatLng(x, y));
            bounds.include(new LatLng(x, y));


        } catch (IOException e)
        {
            Log.d("exception", e.getMessage());
            return false;
        } catch (IndexOutOfBoundsException e)
        {
            Log.d("exception", "wrong event address");
            return false;
        }

        return true;

    }
}
