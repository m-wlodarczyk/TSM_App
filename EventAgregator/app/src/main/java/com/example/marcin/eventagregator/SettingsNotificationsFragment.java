package com.example.marcin.eventagregator;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.marcin.eventagregator.database.DbNotificationsTime;
import com.example.marcin.eventagregator.domain.NotificationTime;

public class SettingsNotificationsFragment extends Fragment
{
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_settings_notifications, container, false);

        spinnersInit();
        createNotificationButtonInit();



        return view;
    }

    private void spinnersInit()
    {
        Spinner daysSpinner = view.findViewById(R.id.days_spinner);
        String[] daysSpinnerItems = getResources().getStringArray(R.array.days_spinner);
        ArrayAdapter<String> daysSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, daysSpinnerItems);

        daysSpinner.setAdapter(daysSpinnerAdapter);

        Spinner hoursSpinner = view.findViewById(R.id.hours_spinner);
        String[] hoursSpinnerItems = getResources().getStringArray(R.array.hours_spinner);
        ArrayAdapter<String> hoursSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, hoursSpinnerItems);

        hoursSpinner.setAdapter(hoursSpinnerAdapter);
    }

    private void createNotificationButtonInit()
    {
        Button addNotificationButton = view.findViewById(R.id.create_notification_button);
        addNotificationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Spinner daysSpinner = view.findViewById(R.id.days_spinner);
                Spinner hoursSpinner = view.findViewById(R.id.hours_spinner);

                int days = Integer.parseInt(daysSpinner.getSelectedItem().toString());
                int hours = Integer.parseInt(hoursSpinner.getSelectedItem().toString());

                DbNotificationsTime.insert(new NotificationTime(days, hours), getContext());
                Snackbar.make(view, "Utworzono powiadomienie.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
