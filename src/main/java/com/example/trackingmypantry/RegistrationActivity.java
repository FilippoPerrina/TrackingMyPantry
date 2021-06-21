package com.example.trackingmypantry;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private final String registrationUrl = "https://lam21.modron.network/users";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button buttonRegistration = findViewById(R.id.buttonRegistration);
        buttonRegistration.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                registrationRequest();
            } else {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
            }
        });
    }

    //get username, mail and password from the user and send it to the server,
    //start login activity in order to enter in mainActivity with the accessToken
    private void registrationRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);

        EditText editTextUsername = findViewById(R.id.editTextUsername);
        final String usernameUser = editTextUsername.getText().toString();
        EditText editTextEmailRegistration = findViewById(R.id.editTextEmailRegistration);
        final String emailUser = editTextEmailRegistration.getText().toString();
        EditText editTextPasswordRegistration = findViewById(R.id.editTextPasswordRegistration);
        final String passwordUser = editTextPasswordRegistration.getText().toString();

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", usernameUser);
            jsonObject.put("email", emailUser);
            jsonObject.put("password",passwordUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, registrationUrl,jsonObject,
                        response -> {
                                Intent intent = getIntent();
                                setResult(RESULT_OK, intent);
                                Intent intentToLoginActivity = new Intent(this, LoginActivity.class);
                                startActivity(intentToLoginActivity);
                                RegistrationActivity.this.finish();
                            Toast.makeText(this, "Registrazione avvenuta con successo!",
                                    Toast.LENGTH_SHORT).show();
                        },
                        error -> Toast.makeText(this, "Qualcosa è andato storto, riprova!",
                                Toast.LENGTH_SHORT).show()){
            @Override
            public Map<String,String> getHeaders() {
                Map<String,String> headers= new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registrationRequest();
            } else {
                Toast.makeText(this, "Negato accesso ad Internet",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
