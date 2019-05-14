package com.example.marcin.eventagregator;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event> implements View.OnClickListener
{

    Context mContext;

    // View lookup cache
    private static class ViewHolder
    {
        TextView eventName;
//        TextView eventDate;
        TextView eventAddress;
    }

    public EventListAdapter(ArrayList<Event> data, Context context)
    {
        super(context, R.layout.row_item, data);
        this.mContext = context;
    }

    @Override
    public void onClick(View v)
    {
//        TODO
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Event event = (Event) object;
        Log.d("kot", "klik");
        Snackbar.make(v, "Release date ", Snackbar.LENGTH_LONG)
                .setAction("No action", null).show();
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.eventName = convertView.findViewById(R.id.event_name);
//            viewHolder.eventDate = convertView.findViewById(R.id.event_date);
            viewHolder.eventAddress = convertView.findViewById(R.id.event_address);
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

        viewHolder.eventName.setText(event.getName());
//        viewHolder.eventDate.setText(event.getDate());
        viewHolder.eventAddress.setText(event.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}
