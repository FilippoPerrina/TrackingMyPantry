package com.example.trackingmypantry;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class CompileItemActivity extends AppCompatActivity {
    private final String addProductsUrl = "https://lam21.modron.network/products";
    private Product productAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile_item);

        Button addProductsButton = findViewById(R.id.addCompileButton);
        addProductsButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                addProductsRequest();
            } else {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
            }
        });

    }

    //get name, barcode and description of the product and post it to the server.
    public void addProductsRequest() {

        EditText editTextNameProduct = findViewById(R.id.editTextCompileName);
        String nameProduct = editTextNameProduct.getText().toString();

        EditText editTextBarcodeProduct = findViewById(R.id.editTextCompileBarcode);
        String barcode = editTextBarcodeProduct.getText().toString();

        EditText editTextDescProduct = findViewById(R.id.editTextCompileDescription);
        String descriptionProduct = editTextDescProduct.getText().toString();

        String sessionToken = getIntent().getStringExtra("sessionToken");
        String accessToken = getIntent().getStringExtra("accessToken");

        RequestQueue queue = Volley.newRequestQueue(this);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", sessionToken);
            jsonObject.put("name", nameProduct);
            jsonObject.put("description", descriptionProduct);
            jsonObject.put("barcode", barcode);
            jsonObject.put("test", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, addProductsUrl,jsonObject,
                        response -> {
                    //get properties of the added product and pass to the listener (Main/Select)
                            try {
                                productAdded = new Product(
                                        response.getString("id"),
                                        response.getString("name"),
                                        response.getString("description"),
                                        response.getString("barcode")
                                );
                                Toast.makeText(this, response.getString("name") +
                                                " aggiunto alla tua dispensa", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = getIntent();
                            intent.putExtra("productAdded", productAdded);
                            setResult(RESULT_OK, intent);
                            CompileItemActivity.this.finish();
                        },
                        error -> Toast.makeText(this, "Qualcosa è andato storto, riprova",
                                Toast.LENGTH_SHORT).show()){
            @Override
            public Map<String,String> getHeaders() {
                Map<String,String> headers= new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization", "Bearer " + accessToken);
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
                addProductsRequest();
            } else {
                Toast.makeText(this, "Negato accesso ad Internet",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
