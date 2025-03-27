package com.example.certificateviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IssueCertificateActivity extends AppCompatActivity {
    private EditText studentNameInput, courseInput;
    private Button generateButton, downloadCertBtn;
    private String certificatePath = "";
    private FirebaseFirestore db;
    private String templateFilePath;
    private boolean isTemplateLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_certificate);

        studentNameInput = findViewById(R.id.studentName);
        courseInput = findViewById(R.id.course);
        generateButton = findViewById(R.id.generateButton);
        downloadCertBtn = findViewById(R.id.downloadCertBtn);
        db = FirebaseFirestore.getInstance();

        fetchTemplate();

        generateButton.setOnClickListener(v -> issueCertificate());
        downloadCertBtn.setOnClickListener(v -> openCertificate());
    }

    /**
     * 🔥 Fetch the saved template SVG file path from Firestore

     */
    private void fetchTemplate() {
        db.collection("templates")
                .whereEqualTo("adminId", FirebaseAuth.getInstance().getUid())
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        templateFilePath = querySnapshot.getDocuments().get(0).getString("templatePath");

                        if (templateFilePath != null && !templateFilePath.isEmpty()) {
                            File file = new File(templateFilePath);
                            if (file.exists()) {
                                isTemplateLoaded = true;
                                Log.d("TemplateDebug", "✅ Fetched Template Path: " + templateFilePath);
                                Toast.makeText(this, "Template Loaded!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("TemplateDebug", "❌ Template file not found at: " + templateFilePath);
                                Toast.makeText(this, "Template file missing in local storage!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("TemplateDebug", "❌ Template path missing in Firestore!");
                            Toast.makeText(this, "Template path missing!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("TemplateDebug", "❌ No Template Found in Firestore");
                        Toast.makeText(this, "No template found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TemplateDebug", "❌ Error Fetching Template: " + e.getMessage());
                    Toast.makeText(this, "Error fetching template!", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * 🔥 Generate a certificate and store it in Firestore + local storage
     */
    private void issueCertificate() {
        String name = studentNameInput.getText().toString().trim();
        String course = courseInput.getText().toString().trim();

        if (name.isEmpty() || course.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isTemplateLoaded) {
            Toast.makeText(this, "Template not loaded properly!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Generate Unique Certificate ID
        String certId = "CERT-" + System.currentTimeMillis();
        Date issueDate = new Date();
        String issueDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(issueDate);

        // ✅ Store Certificate Data in Firestore
        Map<String, Object> certData = new HashMap<>();
        certData.put("id", certId);
        certData.put("studentName", name);
        certData.put("course", course);
        certData.put("issueDate", issueDateStr);
        certData.put("pdfPath", "/storage/emulated/0/Download/Certificates/" + certId + ".pdf"); // Store PDF path

        db.collection("certificates")
                .document(certId)
                .set(certData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreDebug", "✅ Certificate stored successfully in Firestore: " + certId);
                    Toast.makeText(this, "Certificate stored in Firestore!", Toast.LENGTH_SHORT).show();
                    generateCertificate(certId, name, course, issueDateStr);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreDebug", "❌ Error storing certificate: " + e.getMessage());
                    Toast.makeText(this, "Error storing certificate!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * 🔥 Generate the final PDF certificate
     */
    private void generateCertificate(String certId, String name, String course, String issueDate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PDFGenerator.generateCertificate(this, templateFilePath, certId, name, course, issueDate, new PDFGenerator.PDFGenerationCallback() {
                @Override
                public void onPDFGenerated(String filePath) {
                    runOnUiThread(() -> {
                        certificatePath = filePath;
                        downloadCertBtn.setVisibility(Button.VISIBLE);
                        Toast.makeText(IssueCertificateActivity.this, "Certificate Saved Locally!", Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onPDFGenerationFailed(Exception e) {
                    Log.e("PDFError", "❌ Failed to generate PDF: " + e.getMessage());
                    Toast.makeText(IssueCertificateActivity.this, "PDF generation failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 🔥 Open the generated PDF certificate
     */
    private void openCertificate() {
        if (!certificatePath.isEmpty()) {
            File file = new File(certificatePath);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Certificate not found in storage!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No certificate available!", Toast.LENGTH_SHORT).show();
        }
    }
}
