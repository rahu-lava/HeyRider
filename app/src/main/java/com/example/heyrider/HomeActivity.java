package com.example.heyrider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    // Declaring UI elements
    Button LogOut;
    TextView myNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Set window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        myNumber = findViewById(R.id.MyNumber);
        LogOut = findViewById(R.id.LogOut);

        // Get the current authenticated user
        FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();

        // Ensure the user is not null and set the phone number
        assert myUser != null;
        myNumber.setText(myUser.getPhoneNumber());

        // Set click listener for the logout button
        LogOut.setOnClickListener(view -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Start AuthActivity and clear the back stack
            Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
