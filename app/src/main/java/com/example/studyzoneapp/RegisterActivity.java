package com.example.studyzoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(view);
            }
        });
    }

    public void registerUser (final View view){
        final CredentialsFetcher fetcher = new CredentialsFetcher(view.getContext());
        EditText emailText = (EditText) findViewById(R.id.email);
        EditText passwordText = (EditText) findViewById(R.id.password);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        Intent intent = new Intent(view.getContext(), MapsActivity.class);

        fetcher.dispatchRegisterRequest(new CredentialsFetcher.CredentialsResponseListener() {
            @Override
            public void onResponse(CredentialsFetcher.CredentialsResponse response) {

                if (response.isError) {
                    Toast.makeText(view.getContext(), "Error while fetching credentials", Toast.LENGTH_LONG);
                    Log.i("CredentialsFetcher", "isError");
                    return;

                }
                else {
                    if (response.isUser) {
                        startActivity(intent);
                        Log.i("CredentialsFetcher", "Success");
                    }
                    else {
                        Log.i("CredentialsFetcher", "Wrong Credentials");
                        Toast toast = Toast.makeText(view.getContext(), "Wrong Email or Password", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

            }
        }, email, password);
    }
}
