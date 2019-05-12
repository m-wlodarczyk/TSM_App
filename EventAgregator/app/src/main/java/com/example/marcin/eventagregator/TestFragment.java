package com.example.marcin.eventagregator;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class TestFragment extends Fragment
{
    private View view;

    public static JSONObject convert(String xmlString)
    {
        XmlToJson xmlToJson = new XmlToJson.Builder(xmlString).build();
        JSONObject jsonObject = xmlToJson.toJson();
        return jsonObject;
    }
    TextView text;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.test_fragment, container, false);

        text = view.findViewById(R.id.text);
        Button testButton = view.findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                final String fileName = "events.xml";
//                AssetManager assetManager = getContext().getAssets();
//                InputStream is = null;
//                try
//                {
//                    is =  assetManager.open(fileName);
//                }
//                catch (IOException e)
//                {
//                    Log.d("exception", e.getMessage());
//                }
//                File xmlFile = new File(Environment.DIRECTORY_DOWNLOADS + "/" + fileName);
//                JSONObject obj = null;
//                try {
//                    String s = null;
//                    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()))) {
//                        s = br.lines().collect(Collectors.joining(System.lineSeparator()));
//                    }
//
//                    obj = convert(s);
//
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//                EventList events = null;
//                try
//                {
//                    events = new EventList(obj.getJSONObject("root").getJSONArray("event"));
//                }
//                catch (JSONException e)
//                {
//                    Log.d("exception", e.getMessage());
//                }
//                for (int i=0; i<5; i++){
//                    Log.d("koy", events.get(i).toString());
//                }
//                Log.d("koy", Integer.toString(events.length()));

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
                                text.setText(xmlString);

                                JSONObject obj = convert(xmlString);
                                EventList events = null;
                                try
                                {
                                    events = new EventList(obj.getJSONObject("root").getJSONArray("event"));

                                } catch (JSONException e)
                                {
                                    Log.d("exception", e.getMessage());
                                }
                                for (int i = 0; i < 5; i++)
                                {
                                    Log.d("koy", events.get(i).toString());
                                }
                                Log.d("koy", Integer.toString(events.length()));
                            }
                        }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("exception", error.getMessage());
                        Snackbar.make(view, error.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);

            }
        });

        return view;

    }
}
