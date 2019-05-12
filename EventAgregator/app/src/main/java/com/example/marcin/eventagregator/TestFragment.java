package com.example.marcin.eventagregator;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Collectors;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.test_fragment, container, false);

        Button testButton = view.findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String fileName = "events.xml";
                AssetManager assetManager = getContext().getAssets();
                InputStream is = null;
                try
                {
                    is =  assetManager.open(fileName);
                }
                catch (IOException e)
                {
                    Log.d("exception", e.getMessage());
                }
                File xmlFile = new File(Environment.DIRECTORY_DOWNLOADS + "/" + fileName);
                JSONObject obj = null;
                try {
                    String s = null;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()))) {
                        s = br.lines().collect(Collectors.joining(System.lineSeparator()));
                    }

                    obj = convert(s);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                EventList events = null;
                try
                {
                    events = new EventList(obj.getJSONObject("root").getJSONArray("event"));
                }
                catch (JSONException e)
                {
                    Log.d("exception", e.getMessage());
                }
                for (int i=0; i<5; i++){
                    Log.d("events123", events.get(i).toString());
                }
                Log.d("events123", Integer.toString(events.length()));
            }
        });

        return view;

    }
}
