package com.example.marcin.eventagregator;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

public class Event
{
    private Integer id;
    private String title;
    private String description;
    private String address;
    private String date;

    public Event() {}
    public Event(Integer id, String title, String description, String address, String date)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.date = date;
    }

    public Event(JSONObject eventJSON)
    {
        try
        {
            this.id = Integer.parseInt(eventJSON.get("event_id").toString());
            this.title = eventJSON
                    .getJSONObject("event_version")
                    .getJSONObject("version")
                    .getString("evtml_name");
            this.description = eventJSON
                    .getJSONObject("event_version")
                    .getJSONObject("version")
                    .getString("evtml_desc");

            this.description = this.getDescription().replaceAll("<[^>]*>", " ");
            this.description = this.getDescription().replaceAll("&quot;", "\"");
            this.description = this.getDescription().replaceAll("#[0-9]+|opr.sw", "");


            this.address = eventJSON
                    .getJSONObject("event_address")
                    .getString("street");
            this.date = eventJSON.getString("event_start");
        } catch (JSONException e)
        {
            Log.d("exception", e.getMessage());
        }
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title; //.substring(21, name.length()-17);
    }

    public void setTitle(String name)
    {
        this.title = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        return "Event [ id: " + id + "; name: " + title + "; address: " + address + "; date: " + date + " ]";
    }


    public String toJSON()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String eventJSON = null;
        try
        {
            eventJSON = objectMapper.writeValueAsString(this);

        }
        catch (Exception e)
        {
            Log.d("exception", e.getMessage());
        }
        return eventJSON;
    }

    public static Event createFromJSON(String userJSON)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Event event = null;
        userJSON = userJSON.replaceAll(",\"date\":\\{[^}]*\\}", "");
        try
        {
            event = objectMapper.readValue(userJSON, Event.class);
        }
        catch (Exception e)
        {
            Log.d("exception", e.getMessage());
        }
        return event;
    }
}

