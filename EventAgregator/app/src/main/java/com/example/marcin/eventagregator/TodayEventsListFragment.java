package com.example.marcin.eventagregator;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marcin.eventagregator.domain.Event;
import com.example.marcin.eventagregator.domain.EventList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class TodayEventsListFragment extends Fragment
{
    private static final String URL = "http://www.poznan.pl/mim/public/ws-information/";
    private View view;
    ListView eventListView;
    private TextView dateTextView;
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

        MainActivity.enableAd(view);

        eventListView = view.findViewById(R.id.list);
        dateTextView = view.findViewById(R.id.date_textview);
        Calendar calendar = Calendar.getInstance();
        dateTextView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH)+1) + "." + calendar.get(Calendar.YEAR));


        // read xml from website
        // Instantiate the RequestQueue.
        updateEvents(URL + "?co=getCurrentDayEvents");

        Button dateButton = view.findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String pickedDate = dayOfMonth + "." + (monthOfYear+1) + "." + year;
                                dateTextView.setText(pickedDate);
                                updateEvents(URL + "?co=getDayEvent&date=" + year + "-" + (monthOfYear+1) + "-" + dayOfMonth );


                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        return view;
    }

    private void updateEvents(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(getContext());
//        String url = "http://www.poznan.pl/mim/public/ws-information/?co=getCurrentDayEvents";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
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

                        final ArrayList<Event> arrayListEvents = eventList.getEvents();
                        eventListAdapter = new EventListAdapter(arrayListEvents, getContext());
                        eventListView.setAdapter(eventListAdapter);
                        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                Event event = arrayListEvents.get(position);

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
    }


}
