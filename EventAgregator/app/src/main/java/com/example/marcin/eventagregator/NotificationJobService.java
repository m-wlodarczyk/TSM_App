package com.example.marcin.eventagregator;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.marcin.eventagregator.database.DbInterestingEvents;
import com.example.marcin.eventagregator.database.DbNotificationsTime;
import com.example.marcin.eventagregator.domain.Event;
import com.example.marcin.eventagregator.domain.NotificationTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationJobService extends JobService
{
    private static final String TAG = "NotificationJobService";
    private static final String CHANNEL_ID = "Interesting Events";
    private static final String GROUP_KEY_WORK_EMAIL = "com.android.example.INTERESTING_EVENTS";
    private static final int SUMMARY_ID = 0;
    private boolean jobCancelled = false;
    private int notification_id;

    @Override
    public boolean onStartJob(JobParameters params)
    {
        notification_id = 1;
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return false;
    }

    private void doBackgroundWork(final JobParameters params)
    {
//        Intent intent = new Intent(this, AlertDetails.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Log.d(TAG, "doBackgroundWork");

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String currentDateWithHourString = getCurrentDateWithHour();

                ArrayList<Event> allInterestingEvents = DbInterestingEvents.getAll(getBaseContext());
                ArrayList<Event> showNotificationEvents = new ArrayList<>();
                ArrayList<NotificationTime> notificationsTime = DbNotificationsTime.getAll(getBaseContext());

                for (Event event : allInterestingEvents)
                {
                    try
                    {
                        long differenceInHours = differenceInHours(event.getDate(), currentDateWithHourString);

                        Log.d("difference0", event.getDate());
                        Log.d("difference00", currentDateWithHourString);

                        for (NotificationTime notificationTime : notificationsTime)
                        {
                            Log.d("difference1", Long.toString(differenceInHours));
                            Log.d("difference2", Long.toString(notificationTime.getWholeTimeInHours()));

                            if (differenceInHours == notificationTime.getWholeTimeInHours() )
                            {
                                if (!showNotificationEvents.contains(event))
                                {
                                    showNotificationEvents.add(event);
                                    Log.d(TAG, event.toString());
                                }
                            }
                        }

                    } catch (ParseException e)
                    {
                        Log.d("exception: ", e.getMessage());
                    }
                }
                showNotification(showNotificationEvents);
                jobFinished(params, false);
            }
        }).start();
    }

    private static long differenceInHours(String eventDateString, String currentDateWithHourString) throws ParseException
    {
        String withHourPattern = "yyyy-MM-dd HH";
        long differenceInHours;

        if (Build.VERSION.SDK_INT >= 26)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(withHourPattern);
            LocalDateTime currentLocalDate = LocalDateTime.parse(currentDateWithHourString, formatter);
            LocalDateTime eventLocalDate = LocalDateTime.parse(eventDateString.substring(0, 13), formatter);
            Duration duration = Duration.between(currentLocalDate, eventLocalDate);
            differenceInHours = duration.toHours();
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat(withHourPattern);
            Date currentDate = sdf.parse(currentDateWithHourString);
            Date eventDate = sdf.parse(eventDateString.substring(0, 13));
            long diff = eventDate.getTime() - currentDate.getTime();
            differenceInHours = (diff / (1000*60*60));
        }

        return differenceInHours;
    }

    private static String getCurrentDateWithHour()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);


        String monthString = String.format("%02d", month);
        String dayOfMonthString = String.format("%02d", dayOfMonth);
        String hourString = String.format("%02d", hour);

//                String currentDateWithoutTimeString = year + "-" + monthString + "-" + dayOfMonthString;
        String currentDateWithHourString = +year + "-" + monthString + "-" + dayOfMonthString +
                " " + hourString;

        return currentDateWithHourString;
    }

    @Override
    public boolean onStopJob(JobParameters params)
    {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Interesujące wydarzenie";
            String description = "Powiadomienie nadchodzącym wydarzeniu, którym się zainteresowałeś.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(ArrayList<Event> events)
    {
        createNotificationChannel();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        for (Event event : events)
        {
            Log.d(TAG, event.getTitle());

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("event", event.toJSON());
            intent.putExtra("notificationPressed", "true");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setContentText(event.getTitle())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(event.getTitle()))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notification_id++, notification);
        }

        if (events.size() > 0)
        {
            Notification summaryNotification =
                    new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                            .setContentText("Nadchodzące wydarzenia: " + (notification_id - 1))
                            //set content text to support devices running API level < 24
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .setSummaryText("Masz nadchodzące wydarzenia"))
                            //specify which group this notification belongs to
                            .setGroup(GROUP_KEY_WORK_EMAIL)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build();

            notificationManager.notify(SUMMARY_ID, summaryNotification);
        }


    }

}
