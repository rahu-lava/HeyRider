package com.example.heyrider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Set window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Assuming your LottieAnimationView is defined in your layout with the id animation_view
        LottieAnimationView animationView = findViewById(R.id.animationView);

// Apply a color filter to enforce colors
//        ColorFilter colorFilter = new LightingColorFilter(0xFFFFFFFF, 0x00000000);
//        animationView.addValueCallback(
//                new KeyPath("**"), // Replace "**" with the specific path to the color if needed
//                LottieProperty.COLOR_FILTER,
//                new LottieValueCallback<ColorFilter>(colorFilter));
//

        // Create a handler to delay the transition from the splash screen
        Handler handlers = new Handler();
        handlers.postDelayed(() -> {
            // Start AuthActivity after the delay
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            startActivity(intent);
            // Finish SplashActivity so the user cannot return to it
            finish();
        }, 4000); // Delay of 4 seconds
    }
}
