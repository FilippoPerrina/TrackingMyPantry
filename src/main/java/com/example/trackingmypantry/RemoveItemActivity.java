package com.example.trackingmypantry;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class RemoveItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_item);

        Button removeLocalProductButton = findViewById(R.id.buttonRemoveLocalProduct);
        removeLocalProductButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                removeLocalProductsRequest();
            } else {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
            }
        });

    }

    //get the name of the product to be removed and pass it to MainActivity
    public void removeLocalProductsRequest() {
        EditText editTextRemoveName = findViewById(R.id.editTextRemoveName);
        String removeName = editTextRemoveName.getText().toString();
        Intent intent = getIntent();
        intent.putExtra("removeName", removeName);
        setResult(RESULT_OK,intent);
        RemoveItemActivity.this.finish();
    }

}
