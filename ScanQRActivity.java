package com.example.certificateviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class ScanQRActivity extends AppCompatActivity {
    private Button scanQRButton;
    private TextView qrResult;

    private final ActivityResultLauncher<Intent> qrScanLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    if (bitmap != null) {
                        scanQRCode(bitmap);
                    } else {
                        Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        scanQRButton = findViewById(R.id.scanQRButton);
        qrResult = findViewById(R.id.qrResult);

        scanQRButton.setOnClickListener(v -> openCamera());
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            qrScanLauncher.launch(intent);
        } else {
            Toast.makeText(this, "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void scanQRCode(Bitmap bitmap) {
        try {
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            BarcodeScanner scanner = BarcodeScanning.getClient();

            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (barcodes.isEmpty()) {
                            Toast.makeText(this, "No QR Code Detected", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String scannedText = barcodes.get(0).getRawValue();
                        if (scannedText != null && !scannedText.isEmpty()) {
                            navigateToVerifyCertificate(scannedText);
                        } else {
                            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("QRScanner", "QR Scan Failed: " + e.getMessage());
                        Toast.makeText(this, "QR Scan Failed", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("QRScanner", "Error processing image: " + e.getMessage());
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToVerifyCertificate(String certId) {
        Log.d("QRScanner", "Scanned Certificate ID: " + certId);
        Intent intent = new Intent(ScanQRActivity.this, VerifyCertificateActivity.class);
        intent.putExtra("certId", certId);
        startActivity(intent);
        finish(); // Prevents going back to scanner after verification
    }
}
