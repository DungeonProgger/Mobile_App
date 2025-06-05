package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FirebaseAuthExample";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    private Button mEmailSignInButton;
    private Button mEmailCreateAccountButton;
    private Button mSignOutButton;
    private Button mVerifyEmailButton;

    private LinearLayout mEmailPasswordFields;
    private LinearLayout mEmailPasswordButtons;
    private LinearLayout mSignedInButtons;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mStatusTextView = findViewById(R.id.statusTextView);
        mDetailTextView = findViewById(R.id.detailTextView);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        mEmailSignInButton = findViewById(R.id.emailSignInButton);
        mEmailCreateAccountButton = findViewById(R.id.emailCreateAccountButton);
        mSignOutButton = findViewById(R.id.signOutButton);
        mVerifyEmailButton = findViewById(R.id.verifyEmailButton);

        // Layouts
        mEmailPasswordFields = findViewById(R.id.emailPasswordFields);
        mEmailPasswordButtons = findViewById(R.id.emailPasswordButtons);
        mSignedInButtons = findViewById(R.id.signedInButtons);

        // Click listeners
        mEmailSignInButton.setOnClickListener(this);
        mEmailCreateAccountButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mVerifyEmailButton.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                mPasswordField.setError(getString(R.string.error_weak_password));
                                mPasswordField.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mEmailField.setError(getString(R.string.error_invalid_email));
                                mEmailField.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                mEmailField.setError(getString(R.string.error_email_exists));
                                mEmailField.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, getString(R.string.auth_failed) + ": " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) { // Covers user not found & wrong password
                                Toast.makeText(MainActivity.this, getString(R.string.error_sign_in_failed), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, getString(R.string.auth_failed) + ": " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            updateUI(null);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this,
                                        getString(R.string.verification_email_sent) + " " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(MainActivity.this,
                                        getString(R.string.error_sending_verification_email),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "User already verified or not signed in.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            mEmailPasswordFields.setVisibility(View.GONE);
            mEmailPasswordButtons.setVisibility(View.GONE);
            mSignedInButtons.setVisibility(View.VISIBLE);

            mVerifyEmailButton.setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            mEmailPasswordFields.setVisibility(View.VISIBLE);
            mEmailPasswordButtons.setVisibility(View.VISIBLE);
            mSignedInButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (i == R.id.emailCreateAccountButton) {
            createAccount(email, password);
        } else if (i == R.id.emailSignInButton) {
            signIn(email, password);
        } else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.verifyEmailButton) {
            sendEmailVerification();
        }
    }
}