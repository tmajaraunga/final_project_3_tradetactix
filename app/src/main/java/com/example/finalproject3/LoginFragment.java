package com.example.finalproject3;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.editText_email);
        passwordEditText = view.findViewById(R.id.editText_password);
        loginButton = view.findViewById(R.id.button_login);
        registerButton = view.findViewById(R.id.button_register);
        progressBar = view.findViewById(R.id.progressBar_login);

        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> handleRegister());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in and navigate if they are.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Don't just navigate; ensure their data exists first.
            setLoading(true);
            checkAndCreateUserDocument(currentUser);
        }
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) return;
        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, check for user document before navigating
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkAndCreateUserDocument(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getContext(), "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        setLoading(false);
                    }
                });
    }

    private void handleRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) return;
        setLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Registration success, now create user data in Firestore
                        FirebaseUser newUser = mAuth.getCurrentUser();
                        if (newUser != null) {
                            createNewUserInFirestore(newUser);
                        }
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        setLoading(false);
                    }
                });
    }

    /**
     * Checks if a user document exists in Firestore. If not, it creates one.
     * This handles users who registered before the wallet feature was added.
     */
    private void checkAndCreateUserDocument(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(user.getUid());

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().exists()) {
                    // Document doesn't exist, so create it
                    createNewUserInFirestore(user);
                } else {
                    // Document already exists, proceed to the app
                    navigateToMatchList();
                }
            } else {
                setLoading(false);
                Toast.makeText(getContext(), "Failed to check user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewUserInFirestore(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("balance", 5000); // Starting balance of 5000 Tactix Points

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Welcome! Your wallet has been created.", Toast.LENGTH_SHORT).show();
                    navigateToMatchList();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(getContext(), "Error saving user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required.");
            emailEditText.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email.");
            emailEditText.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required.");
            passwordEditText.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters.");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void navigateToMatchList() {
        if (isAdded()) { // Ensure fragment is still attached to activity
            NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_matchListFragment);
        }
    }

    private void setLoading(boolean isLoading) {
        if(isAdded()){
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                registerButton.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
            }
        }
    }
}
