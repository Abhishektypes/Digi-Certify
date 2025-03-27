package com.example.certificateviewer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {
    private Button verifyCertBtn, logoutBtn, scanQRBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        verifyCertBtn = findViewById(R.id.verifyCertBtn);
        scanQRBtn = findViewById(R.id.scanQRBtn);  // New button
        logoutBtn = findViewById(R.id.logoutBtn);

        verifyCertBtn.setOnClickListener(v -> startActivity(new Intent(this, VerifyCertificateActivity.class)));
        scanQRBtn.setOnClickListener(v -> startActivity(new Intent(this, ScanQRActivity.class)));  // Redirects to QR Scanner
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        verifyCertBtn = findViewById(R.id.verifyCertBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        verifyCertBtn.setOnClickListener(v -> startActivity(new Intent(this, VerifyCertificateActivity.class)));
        logoutBtn.setOnClickListener(v -> {
            // Implement Logout Logic
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
