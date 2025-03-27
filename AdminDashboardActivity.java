package com.example.certificateviewer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button issueCertBtn, verifyCertBtn, bulkUploadBtn, logoutBtn, uploadTemplateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard); // Ensure this matches the actual XML file name

        issueCertBtn = findViewById(R.id.issueCertBtn);
        verifyCertBtn = findViewById(R.id.verifyCertBtn);
        bulkUploadBtn = findViewById(R.id.bulkUploadBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        uploadTemplateBtn = findViewById(R.id.uploadTemplateBtn);

        issueCertBtn.setOnClickListener(v -> startActivity(new Intent(this, IssueCertificateActivity.class)));
        verifyCertBtn.setOnClickListener(v -> startActivity(new Intent(this, VerifyCertificateActivity.class)));
        bulkUploadBtn.setOnClickListener(v -> startActivity(new Intent(this, BulkUploadActivity.class))); // To be implemented next
        uploadTemplateBtn.setOnClickListener(v -> startActivity(new Intent(this, UploadTemplateActivity.class)));
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
