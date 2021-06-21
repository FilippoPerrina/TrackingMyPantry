package com.example.trackingmypantry;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class LoginActivity extends AppCompatActivity {

    private final String loginUrl = "https://lam21.modron.network/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                loginRequest();
            } else {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
            }
        });

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            Intent intentToRegistrationActivity = new Intent(this,
                    RegistrationActivity.class);
            registrationActivityLauncher.launch(intentToRegistrationActivity);
        });
    }

    //when the registration succeed remove LoginActivity from the back stack
    ActivityResultLauncher<Intent> registrationActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    LoginActivity.this.finish();
                }
            });

    //get email and password from user and login in to the server,
    //start MainActivity passing accessToken to it
    private void loginRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);

        EditText editTextEmailLogin = findViewById(R.id.editTextEmailLogin);
        final String emailUser = editTextEmailLogin.getText().toString();
        EditText editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin);
        final String passwordUser = editTextPasswordLogin.getText().toString();

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", emailUser);
            jsonObject.put("password", passwordUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, loginUrl,jsonObject,
                        response -> {
                                Intent intentToMainActivity = new Intent(this,
                                    MainActivity.class);
                                try {
                                    intentToMainActivity.putExtra("accessToken",
                                            response.getString("accessToken"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intentToMainActivity);
                                LoginActivity.this.finish();
                        },
                        error -> Toast.makeText(this, "Email o password errate!",
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
                loginRequest();
            } else {
                Toast.makeText(this, "Negato accesso ad Internet",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //on back press show an alert dialog
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Vuoi uscire dall'applicazione?").setCancelable(true);
        alertDialogBuilder.setPositiveButton("Sì", (dialog, which) -> LoginActivity.this.finish());
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}