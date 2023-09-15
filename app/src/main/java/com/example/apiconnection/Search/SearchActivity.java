package com.example.apiconnection.Search;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.apiconnection.Account.AccountActivity;
import com.example.apiconnection.Discover.DiscoverActivity;
import com.example.apiconnection.MainActivity;
import com.example.apiconnection.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    private androidx.appcompat.widget.SearchView searchView; // Use the correct SearchView class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get the SearchView from the layout
        searchView = findViewById(R.id.searchView);

        // Set the OnQueryTextListener for the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Called when the user submits the search query
                performMovieSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Called as the user types each character in the search query
                return false;
            }
        });

        // Request focus on the SearchView and show the keyboard
        searchView.setIconified(false); // Expand the SearchView
        searchView.requestFocus();
        showKeyboard();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.action_home) {
                intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_profile) {
                intent = new Intent(SearchActivity.this, AccountActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_discover) {
                intent = new Intent(SearchActivity.this, DiscoverActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void performMovieSearch(String query) {
        // Create an intent to pass the search query to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("search_query", query);
        startActivity(intent);
        finish(); // Close the search activity
    }
}
