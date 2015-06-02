package com.example.marc.radaralert;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 20/04/2015.
 */
public class APIRequestHandler {
    public final static APIRequestHandler INSTANCE = new APIRequestHandler();
    private RequestQueue queue;

    private APIRequestHandler() {
    }

    public void setQueue(RequestQueue requestQueue) {
        this.queue = requestQueue;
    }

    public void makeGetRequest(String serverURL, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, serverURL,responseListener, errorListener);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makePostRequest(String serverURL, final Integer alertId, final String comment) {
        Response.Listener responseListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        StringRequest postRequest = new StringRequest(Request.Method.POST, serverURL, responseListener, errorListener ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("alertId", alertId.toString());
                params.put("comment", comment);
                return params;
            }
        };
        queue.add(postRequest);
    }
    public void makeLoginRequest(String serverURL, final String username, final String password, Response.Listener responseListener) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        StringRequest postRequest = new StringRequest(Request.Method.POST, serverURL, responseListener, errorListener ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        queue.add(postRequest);
    }
    public void makeDeleteRequest(String serverURL, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, serverURL, responseListener, errorListener);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
