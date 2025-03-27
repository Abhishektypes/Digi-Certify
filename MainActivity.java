package com.example.certificateviewer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore dbb = FirebaseFirestore.getInstance();
        dbb.collection("test").document("check").set(new HashMap<>())
                .addOnSuccessListener(aVoid -> Log.d("FirebaseTest", "Test write success!"))
                .addOnFailureListener(e -> Log.e("FirebaseTest", "Test write failed", e));


        // Check Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth != null) {
            Log.d("FirebaseTest", "Firebase Authentication is connected");
        } else {
            Log.e("FirebaseTest", "Firebase Authentication is NOT connected");
        }

        // Check Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (db != null) {
            Log.d("FirebaseTest", "Firestore is connected");
        } else {
            Log.e("FirebaseTest", "Firestore is NOT connected");
        }

        // Check Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (database != null) {
            Log.d("FirebaseTest", "Realtime Database is connected");
        } else {
            Log.e("FirebaseTest", "Realtime Database is NOT connected");
        }

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        signupButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignupActivity.class)));
    }
}
