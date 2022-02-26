package com.example.studyzoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class StudyZoneActivity extends AppCompatActivity {

    private String markerName;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_zone);
        Intent intent = getIntent();
        Log.i("MarkerInfo","HERE1");
        Bundle bundle = intent.getExtras();

        TextView nameText = findViewById(R.id.name);
        nameText.setText("Name: " + bundle.getString("NAME"));

        TextView priceText = findViewById(R.id.price);
        priceText.setText("Price Rating: " + bundle.getString("PRICE"));

        TextView foodText = findViewById(R.id.food);
        foodText.setText("Food Rating: " + bundle.getString("FOOD"));

        TextView crowdedText = findViewById(R.id.crowded);
        crowdedText.setText("Crowdedness: " + bundle.get("CROWDED"));

        TextView ratingText = findViewById(R.id.total_rating);
        ratingText.setText("Total Rating: " + bundle.getString("RATING"));

    }

}