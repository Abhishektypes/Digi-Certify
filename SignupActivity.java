package com.example.certificateviewer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Spinner roleSpinner;
    private Button signupButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleSpinner = findViewById(R.id.roleSpinner);
        signupButton = findViewById(R.id.signupButton);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(v -> registerUser());
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("role", role);
        userData.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail()); // Store email for reference

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User role saved successfully: " + role);
                    Toast.makeText(SignupActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard(role);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving role: " + e.getMessage());
                    Toast.makeText(SignupActivity.this, "Error saving role", Toast.LENGTH_SHORT).show();
                });
    }



    private void navigateToDashboard(String role) {
        Intent intent;
        if (role.equals("Admin (Teacher)")) {
            intent = new Intent(SignupActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(SignupActivity.this, StudentDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
