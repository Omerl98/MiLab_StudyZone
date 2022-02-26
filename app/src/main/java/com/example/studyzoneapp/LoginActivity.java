package com.example.studyzoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials(view);
            }
        });
    }

    public void checkCredentials (final View view){
        final CredentialsFetcher fetcher = new CredentialsFetcher(view.getContext());
        EditText emailText = (EditText) findViewById(R.id.email);
        EditText passwordText = (EditText) findViewById(R.id.password);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        Intent intent = new Intent(view.getContext(), MapsActivity.class);

        fetcher.dispatchRequest(new CredentialsFetcher.CredentialsResponseListener() {
            @Override
            public void onResponse(CredentialsFetcher.CredentialsResponse response) {

                if (response.isError) {
                    Toast.makeText(view.getContext(), "Error", Toast.LENGTH_LONG);
                    Log.i("CredentialsFetcher", "isError");
                    return;

                }
                else {
                    if (response.isUser) {
                        startActivity(intent);
                        Toast toast = Toast.makeText(view.getContext(), "Login Succeeded", Toast.LENGTH_LONG);
                        Log.i("CredentialsFetcher", "Success");
                    }
                    else {
                        Log.i("CredentialsFetcher", "Wrong Credentials");
                        Toast toast = Toast.makeText(view.getContext(), "There is a problem with your email or password. please try again", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

            }
        }, email, password);
    }
}