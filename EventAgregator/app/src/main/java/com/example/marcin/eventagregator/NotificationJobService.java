package com.example.marcin.eventagregator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationJobService extends JobService
{
    private static final String TAG = "NotificationJobService";
    private static final String CHANNEL_ID = "Interesting Events";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params)
    {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return false;
    }

    private void doBackgroundWork(final JobParameters params)
    {
//        Intent intent = new Intent(this, AlertDetails.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...asdfasfsadfasdfasdfasdfasdfsdfsdfasdasdfasdfsadf")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line...asdfasfsadfasdfasdfasdfasdfsdfsdfasdasdfasdfsadf"))
//                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {


                for (int i = 0; i < 10; i++)
                {
                    Log.d(TAG, "run: " + i);
                    if (jobCancelled)
                        return;
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                jobFinished(params, false);
            }
        }).start();
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
}
