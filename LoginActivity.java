package com.example.certificateviewer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Spinner roleSpinner;
    private Button loginButton, registerButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleSpinner = findViewById(R.id.roleSpinner);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String selectedRole = roleSpinner.getSelectedItem().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserRole(user.getUid(), selectedRole);
                        }
                    } else {
                        Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserRole(String userId, String role) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("role", role);

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(LoginActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("LoginActivity", "Error saving role", e));
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            fetchUserRole(user.getUid());
                        }
                    } else {
                        Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserRole(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Log.d("Firestore", "Fetched role: " + role);

                        if (role != null) {
                            if (role.equals("Admin (Teacher)")) {
                                Toast.makeText(LoginActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                            } else if (role.equals("Student")) {
                                Toast.makeText(LoginActivity.this, "Welcome Student!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Unknown role detected!", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {
                            Log.e("Firestore", "Role is null!");
                            Toast.makeText(LoginActivity.this, "Role not found in Firestore!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "User document does not exist!");
                        Toast.makeText(LoginActivity.this, "User data missing in Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching role: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Error retrieving role!", Toast.LENGTH_SHORT).show();
                });
    }

}
