package com.example.certificateviewer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashLogo = findViewById(R.id.splashLogo);
        TextView loadingText = findViewById(R.id.loadingText);

        // Create Fade-In Animation
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1500);  // 1.5 seconds

        splashLogo.startAnimation(fadeIn);
        loadingText.startAnimation(fadeIn);

        // Delay and Start Main Activity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, SPLASH_DURATION);
    }
}
