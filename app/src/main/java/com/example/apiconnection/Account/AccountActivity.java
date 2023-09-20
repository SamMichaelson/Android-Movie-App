package com.example.apiconnection.Account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apiconnection.Discover.DiscoverActivity;
import com.example.apiconnection.MainActivity;
import com.example.apiconnection.R;
import com.example.apiconnection.Search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    Button btnLogOut;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

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

        btnLogOut=findViewById(R.id.btnLogout);
        mAuth=FirebaseAuth.getInstance();
        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Map<String, Object> user = new HashMap<>();
        if (currentUser != null) {
            user.put("name", "Mike");
            user.put("surname", "Sampson");
            user.put("email", currentUser.getEmail());
            user.put("profilePic", "https://static.wikia.nocookie.net/supermarioglitchy4/images/f/f3/Big_chungus.png/revision/latest?cb=20200511041102");

            TextView tvUsername = findViewById(R.id.tvUsername);
            tvUsername.setText(user.get("name") + " " + user.get("surname"));
        }
    }
}
