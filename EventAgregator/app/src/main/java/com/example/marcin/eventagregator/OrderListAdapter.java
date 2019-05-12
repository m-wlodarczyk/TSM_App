
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.marcin.eventagregator.R;

import java.util.ArrayList;
import java.util.Map;

public class OrderListAdapter  { //extends ArrayAdapter<Order>

    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView name;
    }
//
//    public OrderListAdapter(Context context, int resource, ArrayList<Order> objects) {
//        super(context, resource, objects);
//        mContext = context;
//        mResource = resource;
//    }

    @NonNull
//    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        ViewHolder viewHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);

            viewHolder.name.setText("nazwa");

            view = convertView;
        }
        else {
            view = convertView;
        }

        return view;
    }

}
