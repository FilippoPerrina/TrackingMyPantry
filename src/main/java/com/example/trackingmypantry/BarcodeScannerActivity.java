package com.example.trackingmypantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

public class BarcodeScannerActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    //get the barcode scanned and pass it to SelectItemActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            Toast.makeText(BarcodeScannerActivity.this, "Codice scannerizzato: "+
                            result.getText(), Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            intent.putExtra("barcodeScanned",result.getText());
            setResult(RESULT_OK, intent);
            BarcodeScannerActivity.this.finish();
        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
