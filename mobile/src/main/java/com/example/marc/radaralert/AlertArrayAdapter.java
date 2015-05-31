package com.example.marc.radaralert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.marc.myapplication.backend.submitAlert.SubmitAlert;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        final ImageButton deleteButton = (ImageButton)row.findViewById(R.id.deleteImageButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView tv = (TextView) convertView.findViewById(R.id.secondLine);

                deleteAlert(tags[position]);
                //System.out.println(tv.getText());
            }
        });
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //final CharSequence[] items = {"Foo", "Bar", "Baz"};
                final CharSequence[] items = getComments(16);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Comentaris");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                    }
                });
                builder.setNegativeButton("Enrere", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Afegir comentari", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return row;
    }
    static class commentsHolder{
        public static String[] comments= new String[100];

    }
    private CharSequence[] getComments(final int i) {

        //final commentsHolder com= new commentsHolder();

        APIRequestHandler.INSTANCE.makeGetRequest(Globals.URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int count = 0;

                        JSONParser jsonParser = new JSONParser();
                        try {
                            JSONArray messages = (JSONArray) jsonParser.parse(response);
                            for (int x = 0; x < messages.size(); x++) {
                                JSONObject message = (JSONObject) messages.get(x);
                                System.out.println("id drebut" + Integer.parseInt((String) message.get("alertId")) + "   Id" + i);
                                if (Integer.parseInt((String) message.get("alertId")) == i) {
                                    commentsHolder.comments[count] = (String) message.get("comment");
                                    System.out.print((String) message.get("comment"));
                                    count++;
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        //System.out.print(com.comments);
        return new CharSequence[]{"Foo", "Bar", "Baz"};

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
