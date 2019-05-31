package com.example.marcin.eventagregator;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marcin.eventagregator.database.DbNotificationsTime;
import com.example.marcin.eventagregator.domain.Event;
import com.example.marcin.eventagregator.domain.NotificationTime;

import java.util.ArrayList;

public class NotificationTimeListAdapter extends ArrayAdapter<NotificationTime>
{

    private Context mContext;
    private View view;

    // View lookup cache
    private static class ViewHolder
    {
        TextView days;
        TextView hours;
        ImageButton deleteButton;

    }

    public NotificationTimeListAdapter(ArrayList<NotificationTime> data, Context context, View view)
    {
        super(context, R.layout.row_notification_time, data);
        this.mContext = context;
        this.view = view;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        // Get the data item for this position
        final NotificationTime notificationTime = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_notification_time, parent, false);

            viewHolder.days = convertView.findViewById(R.id.days_before_textview);
            viewHolder.hours = convertView.findViewById(R.id.hours_before_textview);
            viewHolder.deleteButton = convertView.findViewById(R.id.delete_notification_time_button);

            result = convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.days.setText(Integer.toString(notificationTime.getDaysBeforeEvent()));
        viewHolder.hours.setText(Integer.toString(notificationTime.getHoursBeforeEvent()));
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DbNotificationsTime.deleteById(notificationTime.getId(), getContext());
                notificationsTimeListInit();
                Snackbar.make(view, "Usunięto powiadomienie.", Snackbar.LENGTH_SHORT).show();

            }
        });


        // Return the completed view to render on screen
        return convertView;
    }

    public void notificationsTimeListInit()
    {
        ListView notificationsTimeListView = view.findViewById(R.id.list_notifications);
        ArrayList<NotificationTime> notificationsTime = DbNotificationsTime.getAll(getContext());
        NotificationTimeListAdapter notificationTimeListAdapter = new NotificationTimeListAdapter(notificationsTime, getContext(), view);
        notificationsTimeListView.setAdapter(notificationTimeListAdapter);
    }
}
