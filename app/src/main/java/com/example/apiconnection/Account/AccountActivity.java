package com.example.apiconnection.Account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apiconnection.Discover.DiscoverActivity;
import com.example.apiconnection.MainActivity;
import com.example.apiconnection.R;
import com.example.apiconnection.Search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {
    Button btnLogOut;
    Button btnOverlay;
    Button btnSave;
    private FirebaseAuth mAuth;
    private FrameLayout overlayLayout;

    TextInputEditText editTextName;
    TextInputEditText editTextSurname;
    TextInputEditText editTextEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Overlays
        overlayLayout = findViewById(R.id.overlayLayout);
        overlayLayout.setVisibility(View.GONE);

        //text inputs
        editTextName = findViewById(R.id.name);
        editTextSurname = findViewById(R.id.surname);
        editTextEmail = findViewById(R.id.email);

        //Buttons
        btnLogOut = findViewById(R.id.btnLogout);
        btnOverlay = findViewById(R.id.button);
        btnSave = findViewById(R.id.saveButton);

        mAuth = FirebaseAuth.getInstance();
        Map<String, Object> user = new HashMap<>();

        btnOverlay.setOnClickListener(view -> {
            overlayLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.button).setVisibility(View.GONE);
            findViewById(R.id.myMoviesButton).setVisibility(View.GONE);
            findViewById(R.id.mySeriesButton).setVisibility(View.GONE);
            findViewById(R.id.switchNotify).setVisibility(View.GONE);
            findViewById(R.id.recyclerViewMovies).setVisibility(View.GONE);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Object nameObject = user.get("name");
                Object surnameObject = user.get("surname");
                Object emailObject = user.get("email");

                if (nameObject != null) {
                    editTextName.setText(nameObject.toString());
                }

                if (surnameObject != null) {
                    editTextSurname.setText(surnameObject.toString());
                }

                if (emailObject != null) {
                    editTextEmail.setText(emailObject.toString());
                }
            }
        });



        btnSave.setOnClickListener(view -> {
            String name = Objects.requireNonNull(editTextName.getText()).toString();
            String surname = Objects.requireNonNull(editTextSurname.getText()).toString();
            String email = Objects.requireNonNull(editTextEmail.getText()).toString();


            overlayLayout.setVisibility(View.GONE);
            findViewById(R.id.button).setVisibility(View.VISIBLE);
            findViewById(R.id.myMoviesButton).setVisibility(View.VISIBLE);
            findViewById(R.id.mySeriesButton).setVisibility(View.VISIBLE);
            findViewById(R.id.switchNotify).setVisibility(View.VISIBLE);
            findViewById(R.id.recyclerViewMovies).setVisibility(View.VISIBLE);


            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name + " " + surname)
                        .build();
                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.put("name", name);
                                user.put("surname", surname);

                                currentUser.updateEmail(email)
                                        .addOnCompleteListener(emailUpdateTask -> {
                                            if (emailUpdateTask.isSuccessful())
                                                user.put("email", email);
                                            else
                                                user.put("email", "emailElseElse");
                                        });
                            }else{

                                user.put("name", "nameElse");
                                user.put("surname", "surnameElse");
                                user.put("email", "emailElse");
                            }
                        });
            }else{
                user.put("name", "name");
                user.put("surname", "surname");
                user.put("email", "email");
            }
        });

        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        });




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.action_home) {
                intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_search) {
                intent = new Intent(AccountActivity.this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_discover) {
                intent = new Intent(AccountActivity.this, DiscoverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Map<String, Object> user = new HashMap<>();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();

            if (displayName != null) {
                String[] nameParts = displayName.split(" ");

                if (nameParts.length >= 2) {
                    // Assuming the first part is the first name and the rest is the last name
                    String name = nameParts[0];
                    String surname = displayName.substring(name.length()).trim();

                    user.put("name", name);
                    user.put("surname", surname);
                } else {
                    // Handle cases where there's only one part in the display name
                    user.put("name", displayName);
                    user.put("surname", "");
                }
            } else {
                user.put("name", "No name ");
                user.put("surname", "or surname");
            }
            user.put("email", currentUser.getEmail());
            user.put("profilePic", "https://static.wikia.nocookie.net/supermarioglitchy4/images/f/f3/Big_chungus.png/revision/latest?cb=20200511041102");

            TextView tvUsername = findViewById(R.id.tvUsername);
            tvUsername.setText(user.get("name") + " " + user.get("surname"));
            TextView tvEmail = findViewById(R.id.tvEmail);
            tvEmail.setText(user.get("email").toString());
        }
    }
}
