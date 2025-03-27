package com.example.certificateviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadTemplateActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
    private Button selectTemplateBtn;
    private ImageView templatePreview;
    private Uri selectedFileUri;
    private FirebaseFirestore db;
    private String savedTemplatePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_template);

        selectTemplateBtn = findViewById(R.id.selectTemplateBtn);
        templatePreview = findViewById(R.id.templatePreview);
        db = FirebaseFirestore.getInstance();

        selectTemplateBtn.setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/svg+xml"); // Only allow SVG files
        startActivityForResult(Intent.createChooser(intent, "Select SVG Template"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();

            if (selectedFileUri != null) {
                Log.d("UploadDebug", "✅ Selected SVG File: " + selectedFileUri.toString());
                saveTemplateToLocalStorage();
            } else {
                Log.e("UploadDebug", "❌ No file selected!");
                Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveTemplateToLocalStorage() {
        try {
            File directory = new File(getExternalFilesDir(null), "Templates");
            if (!directory.exists() && !directory.mkdirs()) {
                Log.e("StorageDebug", "❌ Failed to create directory for templates");
                Toast.makeText(this, "Failed to create storage directory!", Toast.LENGTH_SHORT).show();
                return;
            }

            File svgFile = new File(directory, "template.svg");
            savedTemplatePath = svgFile.getAbsolutePath();

            byte[] fileBytes = getContentResolver().openInputStream(selectedFileUri).readAllBytes();
            try (FileOutputStream fos = new FileOutputStream(svgFile)) {
                fos.write(fileBytes);
            }

            Log.d("StorageDebug", "✅ Template saved at: " + savedTemplatePath);
            saveTemplatePathToFirestore();

        } catch (IOException e) {
            Log.e("StorageDebug", "❌ Error saving template: " + e.getMessage());
            Toast.makeText(this, "Error saving template!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTemplatePathToFirestore() {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("adminId", FirebaseAuth.getInstance().getUid());
        templateData.put("templateFilePath", savedTemplatePath);

        db.collection("templates")
                .add(templateData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreDebug", "✅ Template path saved to Firestore: " + savedTemplatePath);
                    Toast.makeText(this, "Template Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreDebug", "❌ Error saving template path: " + e.getMessage());
                    Toast.makeText(this, "Error uploading template!", Toast.LENGTH_SHORT).show();
                });
    }
}
