package com.example.marcin.eventagregator;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private SwipeRefreshLayout pullToRefresh;

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

        setDateTextView();

        eventListView = view.findViewById(R.id.list);

        pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                TextView dateTextView = view.findViewById(R.id.date_textview);
                String year = dateTextView.getText().toString().substring(0, 2);
                String month = dateTextView.getText().toString().substring(3, 5);
                String dayOfMonth = dateTextView.getText().toString().substring(6, 10);
                updateEvents(URL + "?co=getDayEvent&date=" + dayOfMonth + "-" + month + "-" + year, true);

            }
        });

        updateEvents(URL + "?co=getCurrentDayEvents", false);
        calendarButtonInit();

        return view;
    }

    private void setDateTextView()
    {
        dateTextView = view.findViewById(R.id.date_textview);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = (calendar.get(Calendar.MONTH) + 1);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        String yearS = Integer.toString(year);
        String monthS = Integer.toString(monthOfYear + 1);
        String dayOfMonthS = Integer.toString(dayOfMonth);

        if (dayOfMonth < 10)
            dayOfMonthS = "0" + Integer.toString(dayOfMonth);
        if ((monthOfYear) < 10)
            monthS = "0" + Integer.toString(monthOfYear);

        dateTextView.setText(dayOfMonthS + "." + monthS + "." + yearS);
    }

    private void calendarButtonInit()
    {
        Button dateButton = view.findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                        new DatePickerDialog.OnDateSetListener()
                        {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {
                                pullToRefresh.setRefreshing(true);
                                String yearS = Integer.toString(year);
                                String monthS = Integer.toString(monthOfYear + 1);
                                String dayOfMonthS = Integer.toString(dayOfMonth);

                                if (dayOfMonth < 10)
                                    dayOfMonthS = "0" + Integer.toString(dayOfMonth);
                                if ((monthOfYear + 1) < 10)
                                    monthS = "0" + Integer.toString(monthOfYear + 1);

                                String pickedDate = dayOfMonthS + "." + monthS + "." + yearS;
                                dateTextView.setText(pickedDate);

                                updateEvents(URL + "?co=getDayEvent&date=" + yearS + "-" + monthS + "-" + dayOfMonthS, true);


                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    private void updateEvents(String url, final boolean isPullToRefresh)
    {
        ProgressBar progressBar = view.findViewById(R.id.empty_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        TextView emptyList = view.findViewById(R.id.empty_text_view);
        emptyList.setText("BRAK ELEMNTÓW DO WYŚWIETLENIA.");
        emptyList.setVisibility(View.INVISIBLE);

        // read xml from website
        // Instantiate the RequestQueue.
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

                        checkIfListHasEvents(arrayListEvents);


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

                        if (isPullToRefresh)
                            pullToRefresh.setRefreshing(false);


                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("exception", error.getMessage());
                Snackbar.make(view, "Nie można uzyskać połączenia.", Snackbar.LENGTH_LONG).show();

                ProgressBar progressBar = view.findViewById(R.id.empty_progress_bar);
                progressBar.setVisibility(View.INVISIBLE);

                TextView emptyList = view.findViewById(R.id.empty_text_view);
                emptyList.setText("NIE MOŻNA UZYSKAĆ POŁĄCZENIA.");
                emptyList.setVisibility(View.VISIBLE);

                if (isPullToRefresh)
                    pullToRefresh.setRefreshing(false);

            }
        });

        queue.add(stringRequest);
    }

    private void checkIfListHasEvents(ArrayList<Event> arrayListEvents)
    {
        // info about empty list
        if (arrayListEvents.size() > 0)
        {
            ProgressBar progressBar = view.findViewById(R.id.empty_progress_bar);
            progressBar.setVisibility(View.INVISIBLE);

            TextView emptyList = view.findViewById(R.id.empty_text_view);
            emptyList.setVisibility(View.INVISIBLE);
        } else
        {
            ProgressBar progressBar = view.findViewById(R.id.empty_progress_bar);
            progressBar.setVisibility(View.INVISIBLE);

            TextView emptyList = view.findViewById(R.id.empty_text_view);
            emptyList.setVisibility(View.VISIBLE);
        }
    }


}
