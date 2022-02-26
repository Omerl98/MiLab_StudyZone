package com.example.studyzoneapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class markerInfoFetcher {
    // Intent intent = new Intent(MapsActivity.this, StudyZoneActivity.class);
    private RequestQueue _queue;
    private String REQUEST_URL = "http://10.0.2.2:8080/infostudyzone?";

    //intent.putExtra("NAME", markerName);
    public class markerInfoResponse {
        public boolean isError;
        public String price;;
        public String food;;
        public String crowded;;
        public String rating;;
        public int lat;
        public int lng;

        public markerInfoResponse(boolean isError, String price, String food, String crowded, String rating, int lat, int lng) {
            this.isError = isError;
            this.price = price;
            this.food = food;
            this.crowded = crowded;
            this.rating = rating;
            this.lat = lat;
            this.lng = lng;

        }

    }

    public interface markerInfoResponseListener {
        public void onResponse(markerInfoFetcher.markerInfoResponse response);
    }

    public markerInfoFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private markerInfoFetcher.markerInfoResponse createErrorResponse() {
        return new markerInfoFetcher.markerInfoResponse(true, null, null, null, null, 0, 0);
    }
    // Request a string response from the provided URL.
    public void dispatchRequest(final markerInfoFetcher.markerInfoResponseListener listener, String name) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, REQUEST_URL + "name=" + name, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject location = response.getJSONObject("location");
                            markerInfoFetcher.markerInfoResponse res = new markerInfoFetcher.markerInfoResponse(false, response.getString("price"), response.getString("food"), response.getString("crowded"), response.getString("totalRating"), location.getInt("latitude"), location.getInt("longitude"));
                            Log.i("MarkerInfo","Success");
                            listener.onResponse(res);
                        }
                        catch (JSONException e) {
                            Log.i("MarkerInfo","Failed");
                            System.out.println("ERROR: " + e);
                            listener.onResponse(createErrorResponse());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MarkerInfo","Failed");
                //System.out.println("ERROR: " + error);
                listener.onResponse(createErrorResponse());
            }
        });



        _queue.add(req);
    }

}

