package com.example.marc.radaralert;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marc.myapplication.backend.submitAlert.SubmitAlert;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by Marc on 19/03/2015.
 */
public class AlertArrayAdapter<T> extends ArrayAdapter{


    private final Context context;
    private final int layoutResourceId;
    private final String[] data;
    private final String[] descriptions;
    private final Integer[] tags;
    public AlertArrayAdapter(Context context, int resource, String[] values, String[] desciptions,Integer[] tags) {
        super(context, resource, values);
        this.context=context;
        this.layoutResourceId=resource;
        this.data=values;
        this.descriptions=desciptions;
        this.tags=tags;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
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
        final String secondLine = descriptions[position];
        holder.description.setText(secondLine);
        //holder.imgIcon.setImageResource(weather.icon);
        final ImageButton deleteButton = (ImageButton)row.findViewById(R.id.deleteImageButton);
        final View finalRow = row;
        //finalRow.setTag(0, tags[position]);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView tv = (TextView) convertView.findViewById(R.id.secondLine);

                deleteAlert(tags[position]);
                //System.out.println(tv.getText());
            }
        });
        return row;
    }

    static class AlertHolder
    {
        TextView title;
        TextView description;
    }

    private void deleteAlert(int tag){
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params){
                String msg = "";
                try{

                    SubmitAlert.Builder builder = new SubmitAlert.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                            .setRootUrl("https://crafty-shelter-88814.appspot.com/_ah/api/");
                    builder.setApplicationName(getContext().getPackageName());
                    SubmitAlert as = builder.build();
                    System.out.println(params[0]);
                    as.deleteAlert(params[0]).execute();
                    msg = "Alerta eliminada correctament";
                }
                catch (IOException ex){
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
///////////////////////////////////
            }
        }.execute(tag);
    }

}
