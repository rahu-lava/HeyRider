package com.example.heyrider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    // Declaring UI elements
    EditText Otp1, Otp2, Otp3, Otp4, Otp5, Otp6;
    TextView resendOtp, editNumber, timeCount;
    Button verify;
    View FrameAnim;
    Boolean Flag;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);

        // Set window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        Otp1 = findViewById(R.id.Otp1);
        Otp2 = findViewById(R.id.Otp2);
        Otp3 = findViewById(R.id.Otp3);
        Otp4 = findViewById(R.id.Otp4);
        Otp5 = findViewById(R.id.Otp5);
        Otp6 = findViewById(R.id.Otp6);

        verify = findViewById(R.id.verify);
        resendOtp = findViewById(R.id.resendOtp);
        editNumber = findViewById(R.id.EditNumber);
        timeCount = findViewById(R.id.TimeCount);
        FrameAnim = findViewById(R.id.include);

        // Set up OTP input fields and listeners
        setOtp();
        setListeners();
        startCountdownTimer(63);
        setVerificationId();

        setBackspaceListener(Otp2, Otp1);
        setBackspaceListener(Otp3, Otp2);
        setBackspaceListener(Otp4, Otp3);
        setBackspaceListener(Otp5, Otp4);
        setBackspaceListener(Otp6, Otp5);

        // Resend OTP click listener
        resendOtp.setOnClickListener(view -> {
            if (Flag) {
                FrameAnim.setVisibility(View.VISIBLE);
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                .setActivity(this)  // Pass the activity context
                                .setPhoneNumber(Objects.requireNonNull(getIntent().getStringExtra("mobile")))
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setCallbacks(mCallBack)
                                .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        // Edit number click listener
        editNumber.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        FrameAnim.setOnClickListener(view -> {
            // Intentionally Empty
        });

    }

    // Start countdown timer for OTP resend
    private void startCountdownTimer(int seconds) {
        Flag = false;
        resendOtp.setTextColor(getResources().getColor(R.color.grey, getTheme()));
        new CountDownTimer(seconds * 1000L, 1000) {

            public void onTick(long millisUntilFinished) {
                // Calculate minutes and seconds
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                // Format the time as 00:00
                String timeFormatted = String.format("%02d:%02d", minutes, seconds);

                // Set the text to the TextView
                timeCount.setText(timeFormatted);
            }

            public void onFinish() {
                // Set the text to "00:00" when the countdown finishes
                timeCount.setText("00:00");
                Flag = true;
                resendOtp.setTextColor(getResources().getColor(R.color.primary, getTheme()));
            }
        }.start();
    }

    // Callbacks for phone number verification
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Toast.makeText(OtpActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
            FrameAnim.setVisibility(View.GONE);
            startCountdownTimer(63);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // Verification completed successfully
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            FrameAnim.setVisibility(View.GONE);
        }
    };

    // Set verification ID from intent
    private void setVerificationId() {
        verificationId = getIntent().getStringExtra("verificationId");
    }

    // Set listeners for verify button
    private void setListeners() {
        verify.setOnClickListener(view -> {
            if (Otp1.getText().toString().trim().isEmpty()
                    || Otp2.getText().toString().trim().isEmpty()
                    || Otp3.getText().toString().trim().isEmpty()
                    || Otp4.getText().toString().trim().isEmpty()
                    || Otp5.getText().toString().trim().isEmpty()
                    || Otp6.getText().toString().trim().isEmpty()) {
                Toast.makeText(OtpActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                return;
            }
            String code =
                    Otp1.getText().toString() +
                            Otp2.getText().toString() +
                            Otp3.getText().toString() +
                            Otp4.getText().toString() +
                            Otp5.getText().toString() +
                            Otp6.getText().toString();

            if (verificationId != null) {
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );

                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(OtpActivity.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                                clearOtpFields();
                            }
                        });
            }

        });

    }

    public void setBackspaceListener(EditText currentEditText, EditText previousEditText) {
        currentEditText.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (currentEditText.getText().toString().isEmpty() && previousEditText != null) {
                    previousEditText.requestFocus();
                    previousEditText.setText(""); // Clear the previous EditText
                } else {
                    currentEditText.setText(""); // Clear the current EditText
                }
                return true; // Indicate that the event has been handled
            }
            return false;
        });
    }

    private void clearOtpFields() {
        // Clear all OTP EditText fields
        Otp1.setText("");
        Otp2.setText("");
        Otp3.setText("");
        Otp4.setText("");
        Otp5.setText("");
        Otp6.setText("");
        Otp1.requestFocus(); // Set focus back to the first OTP field
    }
    // Set text watchers for OTP input fields
    void setOtp() {
        Otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Intentionally Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    Otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Intentionally Empty
            }
        });
        Otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Intentionally Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    Otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Intentionally Empty
            }
        });
        Otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Intentionally Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    Otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Intentionally Empty
            }
        });
        Otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Intentionally Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    Otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Intentionally Empty
            }
        });
        Otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Intentionally Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    Otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Intentionally Empty
            }
        });
        Otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Intentionally Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    verify.performClick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Intentionally Empty
            }
        });

    }

}
