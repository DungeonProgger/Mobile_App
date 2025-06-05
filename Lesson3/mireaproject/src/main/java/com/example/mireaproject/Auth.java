package com.example.mireaproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class Auth extends Fragment implements View.OnClickListener {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_auth, container, false);

        // Views
        mStatusTextView = rootView.findViewById(R.id.statusTextView);
        mDetailTextView = rootView.findViewById(R.id.detailTextView);
        mEmailField = rootView.findViewById(R.id.fieldEmail);
        mPasswordField = rootView.findViewById(R.id.fieldPassword);

        // Buttons
        mEmailSignInButton = rootView.findViewById(R.id.emailSignInButton);
        mEmailCreateAccountButton = rootView.findViewById(R.id.emailCreateAccountButton);
        mSignOutButton = rootView.findViewById(R.id.signOutButton);
        mVerifyEmailButton = rootView.findViewById(R.id.verifyEmailButton);

        // Layouts
        mEmailPasswordFields = rootView.findViewById(R.id.emailPasswordFields);
        mEmailPasswordButtons = rootView.findViewById(R.id.emailPasswordButtons);
        mSignedInButtons = rootView.findViewById(R.id.signedInButtons);

        // Click listeners
        mEmailSignInButton.setOnClickListener(this);
        mEmailCreateAccountButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mVerifyEmailButton.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        return rootView;
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
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
                                Toast.makeText(getActivity(), getString(R.string.auth_failed) + ": " + e.getMessage(),
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(getActivity(), getString(R.string.error_sign_in_failed), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), getString(R.string.auth_failed) + ": " + e.getMessage(),
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
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        getString(R.string.verification_email_sent) + " " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(getActivity(),
                                        getString(R.string.error_sending_verification_email),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "User already verified or not signed in.", Toast.LENGTH_SHORT).show();
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