package com.example.marcin.eventagregator;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.marcin.eventagregator.database.DbInterestingEvents;
import com.example.marcin.eventagregator.database.DbNotificationsTime;
import com.example.marcin.eventagregator.domain.Event;
import com.example.marcin.eventagregator.domain.NotificationTime;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.MapsInitializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// git test
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener
{
    private static final String TAG = "NotificationJobService";
    public static Location location;
    private static final int JOB_SERVICE_ID = 0;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapsInitializer.initialize(getApplicationContext());
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.ad_app_id));

        configureDrawerAndToolbar();
        enablePushNotificationService();
        deleteOldInterestingEvents();
        startTodayEventsListFragment();
        checkIfNotificationPressed();

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);

    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;

        // Handle navigation view item clicks here.
        switch (menuItem.getItemId())
        {
            case R.id.nav_events_list:
            {
                fragmentClass = TodayEventsListFragment.class;
                break;
            }
            case R.id.nav_events_map:
            {
                fragmentClass = TodayEventsMapFragment.class;
                break;
            }
            case R.id.nav_interesting_events:
            {
                fragmentClass = InterestingEventsListFragment.class;
                break;
            }
            case R.id.nav_settings:
            {
                fragmentClass = SettingsFragment.class;

                break;
            }
            default:
                fragmentClass = TodayEventsListFragment.class;

        }
        try
        {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar name
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void enableAd(View view)
    {
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        if (location != null)
        {
            builder.setLocation(location);
        }
        mAdView.loadAd(builder.build());
    }

    private void configureDrawerAndToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void enablePushNotificationService()
    {
        if (!isJobServiceOn(this))
        {
            ComponentName componentName = new ComponentName(this, NotificationJobService.class);
            JobInfo info = new JobInfo.Builder(JOB_SERVICE_ID, componentName)
                    .setPeriodic(15 * 60 * 1000)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .build();

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(info);

            if (resultCode == JobScheduler.RESULT_SUCCESS)
                Log.d(TAG, "Job scheduled");
            else
                Log.d(TAG, "Job failed");
        }

    }

    private void deleteOldInterestingEvents()
    {
        final ArrayList<Event> interestingEvents = DbInterestingEvents.getAll(this);
//        String pattern = "yyyy-MM-dd HH:mm:ss";
        String withoutTimePattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(withoutTimePattern);
        Calendar calendar = Calendar.getInstance();
        String currentDateString = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        for (Event event : interestingEvents)
        {
            try
            {
                Date currentDate = sdf.parse(currentDateString);
                Date eventDate = sdf.parse(event.getDate());
                if (eventDate.before(currentDate))
                {
                    // event has been ended
                    interestingEvents.remove(event);
                    DbInterestingEvents.deleteById(event.getId(), this);
                }
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void checkIfNotificationPressed()
    {
        Intent intent = getIntent();
        boolean isNotificationPressed = Boolean.parseBoolean(intent.getStringExtra("notificationPressed"));

        if (isNotificationPressed)
        {
            String eventJSON = intent.getStringExtra("event");
            Bundle bundle = new Bundle();
            bundle.putString("event", eventJSON);

            Fragment newFragment = new EventInfoFragment();
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public static boolean isJobServiceOn(Context context)
    {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;

        for (JobInfo jobInfo : scheduler.getAllPendingJobs())
        {
            if (jobInfo.getId() == JOB_SERVICE_ID)
            {
                hasBeenScheduled = true;
                break;
            }
        }

        return hasBeenScheduled;
    }

    private void startTodayEventsListFragment()
    {
        Fragment newFragment = new TodayEventsListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        int action = MotionEventCompat.getActionMasked(event);
        String DEBUG_TAG = "koy";
        switch (action)
        {
            case (MotionEvent.ACTION_DOWN):
                Log.d(DEBUG_TAG, "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(DEBUG_TAG, "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(DEBUG_TAG, "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }


    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }
}
