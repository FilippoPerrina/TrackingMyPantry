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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity implements Serializable {
    private final String getProductsUrl = "https://lam21.modron.network/products?barcode=";
    private String sessionToken;
    private int numberOfProducts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Button getProductButton = findViewById(R.id.buttonGetProduct);
        getProductButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                getProductsRequest();
            } else {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
            }
        });

        FloatingActionButton scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        scanBarcodeButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intentToBarcodeScannerActivity = new Intent(this,
                        BarcodeScannerActivity.class);
                registrationActivityLauncher.launch(intentToBarcodeScannerActivity);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        });
        
    }

    //pass the Product added or selected to the MainActivity
    ActivityResultLauncher<Intent> registrationActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Intent intent = getIntent();
                    if (data.hasExtra("productSelected")) {
                        Product productSelected = data.getParcelableExtra("productSelected");
                        intent.putExtra("productSelected", productSelected);
                        setResult(RESULT_OK, intent);
                        AddItemActivity.this.finish();
                    } else if (data.hasExtra("productAdded")) {
                        Product productAdded = data.getParcelableExtra("productAdded");
                        intent.putExtra("productAdded", productAdded);
                        setResult(RESULT_OK, intent);
                        AddItemActivity.this.finish();
                    } else {
                    String productScanned = data.getStringExtra("barcodeScanned");
                    EditText editTextBarcode = findViewById(R.id.editTextAddBarcode);
                    editTextBarcode.setText(productScanned);
                    getProductsRequest();
                    }
                }
            });

    //get barcode and return lists from the WebServer, pass the name of the product to MainActivity,
    //show the result of the get in SelectItemActivity
    public void getProductsRequest() {

        ArrayList<Product> productList = new ArrayList<>();

        String accessToken = getIntent().getStringExtra("accessToken");
        EditText editTextBarcode = findViewById(R.id.editTextAddBarcode);
        String barcodeRequest = editTextBarcode.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, getProductsUrl + barcodeRequest,null,
                        response -> {
                            //populate the list to show the values returned by the server in SelectItemActivity
                            try {
                                sessionToken = response.getString("token");
                                numberOfProducts = response.getJSONArray("products").length();
                                if (response.getJSONArray("products").length() > 0 ) {
                                    for (int i=0; i < numberOfProducts ; i++) {
                                        String id = response.getJSONArray("products").
                                                getJSONObject(i).getString("id");
                                        String name = response.getJSONArray("products").
                                                getJSONObject(i).getString("name");
                                        String description = response.getJSONArray("products").
                                                getJSONObject(i).getString("description");
                                        String barcode = response.getJSONArray("products").
                                                getJSONObject(i).getString("barcode");
                                        Product product = new Product(id,name,description,barcode);
                                        productList.add(i, product);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent intentToSelectItemActivity = new Intent(this,
                                    SelectItemActivity.class);
                            intentToSelectItemActivity.putExtra("productList", productList);
                            intentToSelectItemActivity.putExtra("accessToken", accessToken);
                            intentToSelectItemActivity.putExtra("sessionToken", sessionToken);
                            registrationActivityLauncher.launch(intentToSelectItemActivity);
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

    //check internet and camera permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getProductsRequest();
                } else {
                    Toast.makeText(this, "Negato accesso ad Internet",
                            Toast.LENGTH_SHORT).show();
                }
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentToBarcodeScannerActivity = new Intent(this,
                            BarcodeScannerActivity.class);
                    registrationActivityLauncher.launch(intentToBarcodeScannerActivity);
                } else {
                    Toast.makeText(this, "Negato accesso alla fotocamera",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }
}
