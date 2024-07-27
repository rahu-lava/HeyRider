package com.example.heyrider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    // Declaring UI elements and variables
    EditText mobileNo;
    Button button;
    CountryCodePicker ccp;
    String CountryCode, number, phoneNumber;

    FirebaseAuth firebaseAuth;
    View FrameAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        // Set window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        mobileNo = findViewById(R.id.MobileNo);
        button = findViewById(R.id.sendOtp);
        ccp = findViewById(R.id.countryCodePicker);
        CountryCode = "+" + ccp.getSelectedCountryCode();
        FrameAnim = findViewById(R.id.include);

        // Set button click listener to send OTP
        button.setOnClickListener(view -> {
            number = mobileNo.getText().toString().trim();
            if (number.isEmpty() || number.length()<10 ) {
                Toast.makeText(AuthActivity.this, "Enter Valid Mobile Number!", Toast.LENGTH_LONG).show();
                return;
            }

            firebaseAuth = FirebaseAuth.getInstance();
            sendVerificationCode();
        });

        FrameAnim.setOnClickListener(view -> {

        });

    }

    // Method to send verification code via Firebase
    private void sendVerificationCode() {
        FrameAnim.setVisibility(View.VISIBLE);
        phoneNumber = CountryCode + number;

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setActivity(this)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setCallbacks(mCallBack)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Callbacks for phone number verification
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            FrameAnim.setVisibility(View.GONE);
            // Start OtpActivity with verification ID and phone number
            Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
            intent.putExtra("mobile", phoneNumber);
            intent.putExtra("verificationId", s);
            startActivity(intent);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // Verification completed successfully
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            FrameAnim.setVisibility(View.GONE);
            Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the user is already signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // If user is signed in, navigate to HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
