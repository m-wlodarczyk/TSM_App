package com.example.marcin.eventagregator.domain;

public class NotificationTime
{
    private int id;
    private int daysBeforeEvent;
    private int hoursBeforeEvent;


    public NotificationTime(int id, int daysBeforeEvent, int hoursBeforeEvent)
    {
        this.id = id;
        this.daysBeforeEvent = daysBeforeEvent;
        this.hoursBeforeEvent = hoursBeforeEvent;
    }

    public NotificationTime(int daysBeforeEvent, int hoursBeforeEvent)
    {
        this.daysBeforeEvent = daysBeforeEvent;
        this.hoursBeforeEvent = hoursBeforeEvent;
    }

    public int getWholeTimeInHours()
    {
        int wholeTimeInHours = getHoursBeforeEvent() + getDaysBeforeEvent() * 24;
        return wholeTimeInHours;
    }

    public int getDaysBeforeEvent()
    {
        return daysBeforeEvent;
    }

    public void setDaysBeforeEvent(int daysBeforeEvent)
    {
        this.daysBeforeEvent = daysBeforeEvent;
    }

    public int getHoursBeforeEvent()
    {
        return hoursBeforeEvent;
    }

    public void setHoursBeforeEvent(int hoursBeforeEvent)
    {
        this.hoursBeforeEvent = hoursBeforeEvent;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
