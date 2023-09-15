package com.example.apiconnection.Discover;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiconnection.Account.AccountActivity;
import com.example.apiconnection.ApiInterface;
import com.example.apiconnection.MainActivity;
import com.example.apiconnection.R;
import com.example.apiconnection.RetrofitClient;
import com.example.apiconnection.Search.SearchActivity;
import com.example.apiconnection.items.CategoryResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverActivity extends AppCompatActivity {
    private GenreAdapter genreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.action_search) {
                intent = new Intent(DiscoverActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_profile) {
                intent = new Intent(DiscoverActivity.this, AccountActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_home) {
                intent = new Intent(DiscoverActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
        RecyclerView recyclerViewGenres = findViewById(R.id.recyclerViewGenres);

        // Initialize the GenreAdapter with an empty list (you will update it later)
        genreAdapter = new GenreAdapter(new ArrayList<>());
        recyclerViewGenres.setAdapter(genreAdapter);

        // Set the layout manager (e.g., LinearLayoutManager)
        recyclerViewGenres.setLayoutManager(new LinearLayoutManager(this));

        // Rest of your code remains the same
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();
        apiInterface.discover().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryResponse categoryResponse = response.body();
                    List<String> results = categoryResponse.getResults();

                    // Update the data in the adapter
                    genreAdapter.setData(results);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                // Handle the failure here
            }
        });
    }

}
