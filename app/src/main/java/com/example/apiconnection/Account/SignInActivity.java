package com.example.apiconnection.Account;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity {
    private FirebaseAuth mAuth;

    public SignInActivity() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void signOut() {
        // Sign out the current user
        mAuth.signOut();
    }

    private void updateUI(FirebaseUser user) {
        // Implement your UI update logic here based on the user's sign-in status.
        // For example, you can show/hide UI elements based on whether the user is signed in or not.
    }
}
