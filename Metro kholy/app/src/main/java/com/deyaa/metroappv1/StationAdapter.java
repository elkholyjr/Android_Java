package com.deyaa.metroappv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StationAdapter extends ArrayAdapter<String> {

    public StationAdapter(Context context, List<String> stations) {
        super(context, 0, stations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_station, parent, false);
        }

        TextView stationNameTextView = convertView.findViewById(R.id.stationNameTextView);
        View circleView = convertView.findViewById(R.id.circleView);

        String stationName = getItem(position);
        stationNameTextView.setText(stationName);

        // Set the circle to be solid for the first and last items, hollow for others
        //if this station transfer station with the target line
        if (position == 0 || position == getCount() - 1) {
            circleView.setBackgroundResource(R.drawable.circle_solid);
        } else {
            circleView.setBackgroundResource(R.drawable.circle_hollow);
        }

        return convertView;
    }
}
