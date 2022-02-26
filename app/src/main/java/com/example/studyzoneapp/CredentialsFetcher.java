package com.example.studyzoneapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class CredentialsFetcher {
    private RequestQueue _queue;
    private final static String LOGIN_URL = "http://10.0.2.2:8080/userCheck?";
    private final static String REGISTER_URL = "http://10.0.2.2:8080/userSignUp?";


    public class CredentialsResponse {
        public boolean isError;
        public boolean isUser;;

        public CredentialsResponse(boolean isError, Boolean isUser) {
            this.isError = isError;
            this.isUser = isUser;

        }

    }

    public interface CredentialsResponseListener {
        public void onResponse(CredentialsResponse response);
    }

    public CredentialsFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private CredentialsResponse createErrorResponse() {
        return new CredentialsResponse(true, false);
    }

    public void dispatchRequest(final CredentialsResponseListener listener, String email, String pass) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, LOGIN_URL + "email=" + email + "&password=" + pass, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            CredentialsResponse res = new CredentialsResponse(false,
                                    response.getBoolean("isUser"));
                            listener.onResponse(res);
                        }
                        catch (JSONException e) {
                            listener.onResponse(createErrorResponse());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                listener.onResponse(createErrorResponse());
            }
        });



        _queue.add(req);
    }

    public void dispatchRegisterRequest(final CredentialsResponseListener listener, String email, String pass) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, REGISTER_URL + "email=" + email + "&password=" + pass, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            CredentialsResponse res = new CredentialsResponse(false,
                                    response.getBoolean("isCreatedUser"));
                            listener.onResponse(res);
                        }
                        catch (JSONException e) {
                            listener.onResponse(createErrorResponse());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error: "+error);
                listener.onResponse(createErrorResponse());
            }
        });



        _queue.add(req);
    }
}