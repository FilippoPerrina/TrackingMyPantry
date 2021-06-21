package com.example.trackingmypantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectItemActivity extends AppCompatActivity implements SelectAdapter.OnItemClick {
    private final String postPreferencesUrl = "https://lam21.modron.network/votes";
    private ArrayList<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_item);

        productList = getIntent().getParcelableArrayListExtra("productList");

        ArrayList<String> productsName = new ArrayList<>();
        for (int i=0; i < productList.size(); i++) {
            productsName.add(i, productList.get(i).name);
        }

        String accessToken = getIntent().getStringExtra("accessToken");
        String sessionToken = getIntent().getStringExtra("sessionToken");

        ArrayList<String> values = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSelect);
        SelectAdapter selectAdapter = new SelectAdapter(values, this);
        recyclerView.setAdapter(selectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView textViewZeroProducts = findViewById(R.id.textNoProdFound);
        if (productList.size() == 0) {
            textViewZeroProducts.setVisibility(View.VISIBLE);
        }

        //show all products to user
        values.addAll(productsName);

        Button buttonSelectAdd = findViewById(R.id.buttonSelect);
        buttonSelectAdd.setOnClickListener(v -> {
            Intent intentToCompileItemActivity = new Intent(this, CompileItemActivity.class);
            intentToCompileItemActivity.putExtra("accessToken", accessToken);
            intentToCompileItemActivity.putExtra("sessionToken",sessionToken);
            registrationActivityLauncher.launch(intentToCompileItemActivity);
        });

    }

    ActivityResultLauncher<Intent> registrationActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Product productAdded = data.getParcelableExtra("productAdded");
                    Intent intent = getIntent();
                    intent.putExtra("productAdded",productAdded);
                    setResult(RESULT_OK, intent);
                    SelectItemActivity.this.finish();
                }
            });

    //show Details of the product clicked, pass the product selected to AddItemActivity
    @Override
    public void onClick(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("productSelected", productList.get(position));
        FragmentManager fm = getSupportFragmentManager();
        ShowDetailsFragmentSelect showDetailsFragment = new ShowDetailsFragmentSelect();
        showDetailsFragment.setArguments(bundle);
        showDetailsFragment.show(fm,"showDetailsSelect");
        fm.setFragmentResultListener("productSelected", this, (requestKey, result) ->{
            postProductsPreference(position);
            Intent intent = getIntent();
            intent.putExtra("productSelected", productList.get(position));
            setResult(RESULT_OK,intent);
            SelectItemActivity.this.finish();
        });

    }

    //notify the server of the product added giving a rating of +1
    public void postProductsPreference(int position) {
        String idSelected = productList.get(position).id;
        String sessionToken = getIntent().getStringExtra("sessionToken");
        String accessToken = getIntent().getStringExtra("accessToken");

        RequestQueue queue = Volley.newRequestQueue(this);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", sessionToken);
            jsonObject.put("rating", 1);
            jsonObject.put("productId", idSelected);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, postPreferencesUrl ,jsonObject,
                        response -> Log.d("addPreferencesSucceed", response.toString()),
                        error -> Log.d("addPreferencesFailed", error.toString())){
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

}
