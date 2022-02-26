package com.example.studyzoneapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.studyzoneapp.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private static final int DEFAULT_ZOOM = 15;
    private Location lastKnownLocation;
    private PlacesClient placesClient;
    // A default location (Tel Aviv, Israel) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(32.109333, 34.855499);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermission();
        // Turn on the My Location layer
        updateLocationUI();
        // Get the current location of the device and set position on the map
        getDeviceLocation();
        // Add a marker in Sydney and move the camera
        addMarkers(mMap);
       // mMap.addMarker(new MarkerOptions().position(loc).title(title));
       // mMap.addMarker(new MarkerOptions().position(loc).title(title));
       // mMap.addMarker(new MarkerOptions().position(loc).title(title));
        mMap.addMarker(new MarkerOptions().position(new LatLng(32.0852999,34.78176759999999)).title("haroma_herzliya"));

        mMap.setOnMarkerClickListener(this);
    }

    private void addMarkers(GoogleMap mMap) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "http://10.0.2.2:8080/getStudyzones";

        // Request a string response from the provided URL.
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject zones = response.getJSONObject("studyZones");
                            for(int i = 0; i<zones.names().length(); i++){
                                String title = zones.names().getString(i);
                                JSONObject location = zones.getJSONObject(zones.names().getString(i)).getJSONObject("location");
                                LatLng loc = new LatLng(location.getDouble("latitude"),location.getDouble("longitude"));
                                mMap.addMarker(new MarkerOptions().position(loc).title(title));
                                Log.v("ZONES", "location:" + location + ", title: " + title);
                            }
                            Log.i("ZONES","Success");
                        }
                        catch (JSONException e) {
                            Log.i("ZONES","Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MarkerInfo","onErrorResponse");
                System.out.println(error);
            }
        });

        queue.add(req);
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = mfusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i(marker.getTitle(), "clicked");    //debug
        Intent intent = new Intent(MapsActivity.this, StudyZoneActivity.class);
        markerInfoFetcher fetcher = new markerInfoFetcher(this);
        String name = marker.getTitle();
        fetcher.dispatchRequest(new markerInfoFetcher.markerInfoResponseListener() {
            @Override
            public void onResponse(markerInfoFetcher.markerInfoResponse response) {

                if (response.isError) {
                    Log.i("MarkerInfo", "isError");
                    return;

                }
                else {
                    intent.putExtra("PRICE", response.price);
                    intent.putExtra("CROWDED", response.crowded);
                    intent.putExtra("FOOD", response.food);
                    intent.putExtra("NAME", name);
                    intent.putExtra("RATING", response.rating);
                    Log.i("MarkerInfo", "Success");
                    startActivity(intent);
                }

            }
        }, name);

        return false;
    }




};