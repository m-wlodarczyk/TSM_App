package com.example.marcin.eventagregator;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.stream.Collectors;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class TodayEventsListFragment extends Fragment
{
    private View view;
    ListView eventListView;
    private static EventListAdapter eventListAdapter;

    public static JSONObject convert(String xmlString)
    {
        XmlToJson xmlToJson = new XmlToJson.Builder(xmlString).build();
        JSONObject jsonObject = xmlToJson.toJson();
        return jsonObject;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_today_events_list, container, false);
        eventListView = view.findViewById(R.id.list);

        // read xml from file
//        final String fileName = "events.xml";
//        AssetManager assetManager = getContext().getAssets();
//        InputStream is = null;
//        try
//        {
//            is = assetManager.open(fileName);
//        } catch (IOException e)
//        {
//            Log.d("exception", e.getMessage());
//        }
//        JSONObject obj = null;
//        try
//        {
//            String s = null;
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset())))
//            {
//                s = br.lines().collect(Collectors.joining(System.lineSeparator()));
//            }
//
//            obj = convert(s);
//
//        } catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//        }
//        EventList eventList = null;
//        try
//        {
//            eventList = new EventList(obj.getJSONObject("root").getJSONArray("event"));
//        } catch (JSONException e)
//        {
//            Log.d("exception", e.getMessage());
//        }
//        for (int i = 0; i < eventList.length(); i++)
//        {
//            Log.d("koy", eventList.get(i).toString());
//        }


//        final ArrayList<Event> arrayListEvents = eventList.getEvents();
//        eventListAdapter = new EventListAdapter(arrayListEvents, getContext());
//        eventListView.setAdapter(eventListAdapter);
//        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//
//                Event event = arrayListEvents.get(position);
//
//                Snackbar.make(view, "hmmmm", Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//            }
//        });


        // read xml from website
        // Instantiate the RequestQueue.
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
                        eventListAdapter = new EventListAdapter(arrayListEvents, getContext());
                        eventListView.setAdapter(eventListAdapter);
                        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {

                                Event event = arrayListEvents.get(position);

                                Snackbar.make(view, "hmmmm", Snackbar.LENGTH_LONG)
                                        .setAction("No action", null).show();
                            }
                        });

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
}
