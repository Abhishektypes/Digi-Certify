package com.example.certificateviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DragAndDropEditorActivity extends AppCompatActivity {
    private ImageView templateImageView;
    private TextView nameField, courseField, qrField, idField;
    private Button savePositionsButton;
    private String templateUri;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop_editor);

        templateImageView = findViewById(R.id.templateImageView);
        nameField = findViewById(R.id.nameField);
        courseField = findViewById(R.id.courseField);
        qrField = findViewById(R.id.qrField);
        idField = findViewById(R.id.idField);
        savePositionsButton = findViewById(R.id.savePositionsButton);

        // ✅ Get image URI from intent
        templateUri = getIntent().getStringExtra("selectedImageUri");
        if (templateUri != null) {
            Uri uri = Uri.parse(templateUri);
            Glide.with(this).load(uri).into(templateImageView);
        } else {
            Log.e("TemplateLoading", "❌ No template URI received!");
            Toast.makeText(this, "Error loading template!", Toast.LENGTH_SHORT).show();
        }

        // Enable drag & resize for each view
        setupTouchListener(nameField);
        setupTouchListener(courseField);
        setupTouchListener(qrField);
        setupTouchListener(idField);

        savePositionsButton.setOnClickListener(v -> saveTemplateWithPositions());
    }


    private void setupTouchListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            private boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        isDragging = true;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            v.setX(event.getRawX() + dX);
                            v.setY(event.getRawY() + dY);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        isDragging = false;
                        break;
                }
                return true; // Return true to consume the event
            }
        });
    }

    private void saveTemplateWithPositions() {
        if (templateUri == null) {
            Log.e("FirestoreUpload", "❌ Template image missing!");
            Toast.makeText(this, "Template image missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // ✅ Convert selected image to Base64 before saving
            InputStream inputStream = getContentResolver().openInputStream(Uri.parse(templateUri));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            // ✅ Prepare Firestore data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("adminId", FirebaseAuth.getInstance().getUid());
            templateData.put("templateBase64", base64Image);  // Store Base64 instead of URI
            templateData.put("namePosition", getPosition(nameField));
            templateData.put("coursePosition", getPosition(courseField));
            templateData.put("qrPosition", getPosition(qrField));
            templateData.put("idPosition", getPosition(idField));

            Log.d("FirestoreUpload", "🔥 Uploading template to Firestore...");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("templates").add(templateData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("FirestoreUpload", "✅ Template saved with ID: " + documentReference.getId());
                        Toast.makeText(this, "Template Saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreUpload", "❌ Error uploading template: " + e.getMessage());
                        Toast.makeText(this, "Upload failed! Please try again.", Toast.LENGTH_SHORT).show();
                    });

        } catch (IOException e) {
            Log.e("ImageProcessing", "❌ Error processing template image: " + e.getMessage());
            Toast.makeText(this, "Error processing image!", Toast.LENGTH_SHORT).show();
        }
    }

    private Map<String, Float> getPosition(View view) {
        Map<String, Float> position = new HashMap<>();
        position.put("x", view.getX());
        position.put("y", view.getY());
        position.put("width", (float) view.getWidth());
        position.put("height", (float) view.getHeight());
        return position;
    }
}
