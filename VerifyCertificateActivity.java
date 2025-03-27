package com.example.certificateviewer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class VerifyCertificateActivity extends AppCompatActivity {
    private EditText certIdInput;
    private Button verifyButton;
    private TextView certDetails;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_certificate);

        certIdInput = findViewById(R.id.certIdInput);
        verifyButton = findViewById(R.id.verifyButton);
        certDetails = findViewById(R.id.certDetails);
        db = FirebaseFirestore.getInstance();

        // If the activity is opened via QR scanning, fill the cert ID automatically
        String scannedId = getIntent().getStringExtra("certId");
        if (scannedId != null) {
            certIdInput.setText(scannedId);
            verifyCertificate(); // Auto-verify
        }

        verifyButton.setOnClickListener(v -> verifyCertificate());
    }

    private void verifyCertificate() {
        String certId = certIdInput.getText().toString().trim();
        if (certId.isEmpty()) {
            Toast.makeText(this, "Enter a certificate ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch certificate from Firestore
        db.collection("certificates").document(certId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract data from Firestore
                        String studentName = documentSnapshot.getString("studentName");
                        String course = documentSnapshot.getString("course");
                        String issueDate = documentSnapshot.getString("issueDate");

                        // Display the certificate details
                        String details = "✅ Certificate Verified!\n\n" +
                                "👤 Student: " + studentName + "\n" +
                                "📚 Course: " + course + "\n" +
                                "📅 Issued On: " + issueDate;
                        certDetails.setText(details);
                    } else {
                        certDetails.setText("❌ Certificate Not Found!");
                        Toast.makeText(VerifyCertificateActivity.this, "Certificate Not Found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    certDetails.setText("⚠️ Error Fetching Certificate!");
                    Toast.makeText(VerifyCertificateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
