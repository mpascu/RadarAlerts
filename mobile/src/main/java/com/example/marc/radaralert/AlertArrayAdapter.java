package com.example.marc.radaralert;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Marc on 19/03/2015.
 */
public class AlertArrayAdapter<T> extends ArrayAdapter{


    private final Context context;
    private final int layoutResourceId;
    private final String[] data;
    private final String[] descriptions;

    public AlertArrayAdapter(Context context, int resource, String[] values, String[] desciptions) {
        super(context, resource, values);
        this.context=context;
        this.layoutResourceId=resource;
        this.data=values;
        this.descriptions=desciptions;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AlertHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AlertHolder();
            holder.title = (TextView)row.findViewById(R.id.firstLine);
            holder.description = (TextView)row.findViewById(R.id.secondLine);

            row.setTag(holder);
        }
        else
        {
            holder = (AlertHolder)row.getTag();
        }

        String title = data[position];
        holder.title.setText(title);
        String secondLine = descriptions[position];
        holder.description.setText(secondLine);
        //holder.imgIcon.setImageResource(weather.icon);

        return row;
    }

    static class AlertHolder
    {
        TextView title;
        TextView description;
    }

}
