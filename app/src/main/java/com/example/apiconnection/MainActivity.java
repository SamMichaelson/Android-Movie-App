package com.example.apiconnection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiconnection.Account.AccountActivity;
import com.example.apiconnection.Discover.DiscoverActivity;
import com.example.apiconnection.Movie.MovieAdapter;
import com.example.apiconnection.Movie.MovieResponse;
import com.example.apiconnection.Search.SearchActivity;
import com.example.apiconnection.Series.MovieDetailActivity;
import com.example.apiconnection.Series.RatingResponse;
import com.example.apiconnection.items.Image;
import com.example.apiconnection.items.MovieItem;
import com.example.apiconnection.items.Ratings;
import com.example.apiconnection.items.Result;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView recyclerViewMovies;
    GridLayoutManager gridLayoutManager;
    MovieAdapter adapter;
    List<MovieItem> movieItems = new ArrayList<>();

    Spinner sortSpinner;
    String sort = "Sort By: Default"; // Default sorting option

    int currentIndex = 0; // Current index of the movie being fetched
    private String querySearch;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progressBar);
        recyclerViewMovies = findViewById(R.id.recyclerViewMovies);
        sortSpinner = findViewById(R.id.sortSpinner);

        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerViewMovies.setLayoutManager(gridLayoutManager);


        adapter = new MovieAdapter(movieItems);

        recyclerViewMovies.setAdapter(adapter);
        adapter.setOnItemClickListener(movie -> fetchRating(movie.getId(), movie , movie.getType() , movie.getIsSeries() , movie.getIsEpisode()));

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(sort==null)
                    sortSpinner.setVisibility(View.GONE);
                else
                    sortSpinner.setVisibility(View.VISIBLE);

                sort = (String) parentView.getItemAtPosition(position);
                if(querySearch!=null)
                    performMovieSearch(querySearch);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
//        performMovieSearch("The Boys");


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.action_search) {
                intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_profile) {
                intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_discover) {
                intent = new Intent(MainActivity.this, DiscoverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            }

            return false;
        });

        String searchQuery = getIntent().getStringExtra("search_query");
        if (searchQuery != null) {
            querySearch=searchQuery;
            // Call a method to fetch movies based on the search query
            fetchMovies(searchQuery);
        }else {
            upcomingMovies();
        }
    }

    private void performMovieSearch(String akaValue) {
        movieItems.clear();
        currentIndex = 0;

        fetchMovies(akaValue);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void fetchMovies(String akaValue) {
        TextView headerTextView = findViewById(R.id.headerTextView);
        headerTextView.setText("Search Results for:"+akaValue);

        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();

        switch (sort) {
            case "Sort By: Date":
                apiInterface.searchMoviesByAKAsSortYear(akaValue).enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MovieResponse movieResponse = response.body();
                            List<Result> results = movieResponse.getResults();
                            int limit = movieResponse.getEntries();
                            String next = movieResponse.getNext();
                            int count = 0;
                            for (Result responseItem : results) {
                                if (isValid(responseItem)) {
                                    String title = responseItem.getOriginalTitleText().getText();
                                    int year = responseItem.getReleaseYear().getYear();
                                    Image image = responseItem.getPrimaryImage();
                                    if (image == null) {
                                        continue;
                                    }
                                    String id = responseItem.getId();
                                    String type = responseItem.getTitleType().getText();
                                    boolean isSeries = responseItem.getTitleType().getIsSeries();
                                    boolean isEpisode = responseItem.getTitleType().getIsEpisode();
                                    MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                                    movieItems.add(movieItem);
                                    count++;
                                    if (count >= limit) {
                                        break;
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            if (next != null && !next.equals("null")) {
                                fetchNextMoviePage(next);
                            }
                        }

                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        // Handle failure
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "Sort By: Rate":
                apiInterface.searchMoviesByAKAs(akaValue).enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MovieResponse movieResponse = response.body();
                            List<Result> results = movieResponse.getResults();
                            int limit = movieResponse.getEntries();
                            String next = movieResponse.getNext();
                            int count = 0;
                            for (Result responseItem : results) {
                                if (isValid(responseItem)) {
                                    String title = responseItem.getOriginalTitleText().getText();
                                    int year = responseItem.getReleaseYear().getYear();
                                    Image image = responseItem.getPrimaryImage();
                                    if (image == null) {
                                        continue;
                                    }
                                    String id = responseItem.getId();
                                    String type = responseItem.getTitleType().getText();
                                    boolean isSeries = responseItem.getTitleType().getIsSeries();
                                    boolean isEpisode = responseItem.getTitleType().getIsEpisode();
                                    MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                                    fetchRatingSort(id, movieItem,"Rate");
                                    movieItems.add(movieItem);
                                    count++;
                                    if (count >= limit) {
                                        break;
                                    }
                                }
                            }
                            if (movieItems != null)
                                sortMovieItemsByAverageRating(movieItems);
                            if (next != null && !next.equals("null")) {
                                fetchNextMoviePageSort(next,"Rate");
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        // Handle failure
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "Sort By: Popularity":
                apiInterface.searchMoviesByAKAs(akaValue).enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MovieResponse movieResponse = response.body();
                            List<Result> results = movieResponse.getResults();
                            int limit = movieResponse.getEntries();
                            String next = movieResponse.getNext();
                            int count = 0;
                            for (Result responseItem : results) {
                                if (isValid(responseItem)) {
                                    String title = responseItem.getOriginalTitleText().getText();
                                    int year = responseItem.getReleaseYear().getYear();
                                    Image image = responseItem.getPrimaryImage();
                                    if (image == null) {
                                        continue;
                                    }
                                    String id = responseItem.getId();
                                    String type = responseItem.getTitleType().getText();
                                    boolean isSeries = responseItem.getTitleType().getIsSeries();
                                    boolean isEpisode = responseItem.getTitleType().getIsEpisode();
                                    MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                                    fetchRatingSort(id, movieItem,"Popularity");
                                    movieItems.add(movieItem);
                                    count++;
                                    if (count >= limit) {
                                        break;
                                    }
                                }
                            }
                            if (movieItems != null)
                                sortMovieItemsByNumVotes(movieItems);

                            if (next != null && !next.equals("null")) {
                                fetchNextMoviePageSort(next,"Popularity");
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        // Handle failure
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "Sort By: Default":
                apiInterface.searchMoviesByAKAs(akaValue).enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MovieResponse movieResponse = response.body();
                            List<Result> results = movieResponse.getResults();
                            int limit = movieResponse.getEntries();
                            String next = movieResponse.getNext();
                            int count = 0;
                            for (Result responseItem : results) {
                                if (isValid(responseItem)) {
                                    String title = responseItem.getOriginalTitleText().getText();
                                    int year = responseItem.getReleaseYear().getYear();
                                    Image image = responseItem.getPrimaryImage();
                                    if (image == null) {
                                        continue;
                                    }
                                    String id = responseItem.getId();
                                    String type = responseItem.getTitleType().getText();
                                    boolean isSeries = responseItem.getTitleType().getIsSeries();
                                    boolean isEpisode = responseItem.getTitleType().getIsEpisode();
                                    MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                                    movieItems.add(movieItem);
                                    count++;
                                    if (count >= limit) {
                                        break;
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            if (next != null && !next.equals("null")) {
                                fetchNextMoviePage(next);
                            }
                        }

                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        // Handle failure
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
    private void fetchNextMoviePage(String nextPageUrl) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();

        apiInterface.nextMoviePage(nextPageUrl).enqueue(new Callback<MovieResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> results = response.body().getResults();
                    int limit = response.body().getEntries();
                    String next = response.body().getNext();

                    int count = 0;
                    for (Result responseItem : results) {
                        if (isValid(responseItem)) {
                            String title = responseItem.getOriginalTitleText().getText();
                            int year = responseItem.getReleaseYear().getYear();
                            Image image = responseItem.getPrimaryImage();
                            if (image==null) {continue;}
                            String id = responseItem.getId();
                            String type=responseItem.getTitleType().getText();
                            boolean isSeries=responseItem.getTitleType().getIsSeries();
                            boolean isEpisode=responseItem.getTitleType().getIsEpisode();
                            // Create a new MovieItem instance for the fetched movie
                            MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                            movieItems.add(movieItem);
                            count++; // Increment the counter

                            if (count >= limit) {
                                if (next != null ) {
                                    fetchNextMoviePage(next);
                                }
                                break; // Break the loop if the limit is reached
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Handle failure
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNextMoviePageSort(String nextPageUrl,String sort) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();

        apiInterface.nextMoviePage(nextPageUrl).enqueue(new Callback<MovieResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> results = response.body().getResults();
                    int limit = response.body().getEntries();
                    String next = response.body().getNext();

                    int count = 0;
                    for (Result responseItem : results) {
                        if (isValid(responseItem)) {
                            String title = responseItem.getOriginalTitleText().getText();
                            int year = responseItem.getReleaseYear().getYear();
                            Image image = responseItem.getPrimaryImage();
                            if (image==null) {continue;}
                            String id = responseItem.getId();
                            String type=responseItem.getTitleType().getText();
                            boolean isSeries=responseItem.getTitleType().getIsSeries();
                            boolean isEpisode=responseItem.getTitleType().getIsEpisode();
                            MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                            fetchRatingSort(id,movieItem,sort);
                            movieItems.add(movieItem);
                            count++; // Increment the counter

                            if (count >= limit) {
                                if (next != null ) {
                                    fetchNextMoviePageSort(next,sort);
                                }else{
                                    adapter.notifyDataSetChanged();
                                }
                                break; // Break the loop if the limit is reached
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Handle failure
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Create Intent items for movie details
    private Intent createMovieDetailIntent(MovieItem movie, float averageRating, int numVotes, String type, boolean isSeries, boolean isEpisode) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_YEAR, movie.getYear());
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_IMAGE_URL, movie.getImage().getUrl());

        String originalString = movie.getImage().getCaption().getPlainText();
        String modifiedString = "Cast: " + originalString;
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_PLAINTEXT, modifiedString);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_AVERAGE_RATING, averageRating);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_NUM_VOTES, numVotes);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_TYPE, type);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_IS_SERIES, String.valueOf(isSeries) );
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_IS_EPISODE, String.valueOf(isEpisode) );
//        if(isSeries){
//            fetchEpisodes(movie.getId());
//        }
        return intent;
    }




    private void fetchRating(String id, MovieItem movie,String type,boolean isSeries, boolean isEpisode) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();

        Call<RatingResponse> call = apiInterface.searchMoviesRating(id);
        call.enqueue(new Callback<RatingResponse>() {
            @Override
            public void onResponse(@NonNull Call<RatingResponse> call, @NonNull Response<RatingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    float averageRating = response.body().getResults().getAverageRating();
                    int numVotes = response.body().getResults().getNumVotes();
                    Intent intent = createMovieDetailIntent(movie, averageRating, numVotes, type ,isSeries, isEpisode ) ;


                    // Start the MovieDetailActivity with the created Intent
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RatingResponse> call, @NonNull Throwable t) {
                // Handle failure
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRatingSort(String id, MovieItem movieItem,String sort) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();

        Call<RatingResponse> call = apiInterface.searchMoviesRating(id);
        call.enqueue(new Callback<RatingResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<RatingResponse> call, @NonNull Response<RatingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    float averageRating = response.body().getResults().getAverageRating();
                    int numVotes = response.body().getResults().getNumVotes();
                    if (movieItem.getRating() == null) {
                        movieItem.setRating(new Ratings());
                    }
                    if (Float.isNaN(averageRating)) {
                        movieItem.getRating().setAverageRating(0.0F);
                    } else {
                        movieItem.getRating().setAverageRating(averageRating);
                    }
                    if ("null".equals(String.valueOf(averageRating))) {
                        movieItem.getRating().setNumVotes(0);
                    } else {
                        movieItem.getRating().setNumVotes(numVotes);
                    }
                    if (Objects.equals(sort, "Rate")) {
                        sortMovieItemsByAverageRating(movieItems);
                    } else if (Objects.equals(sort, "Popularity")) {
                        sortMovieItemsByNumVotes(movieItems);
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RatingResponse> call, @NonNull Throwable t) {
                // Handle failure
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @SuppressLint("NotifyDataSetChanged")
    private void upcomingMovies() {
        sort=null;
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();

        apiInterface.RandomMovies().enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieResponse movieResponse = response.body();
                    List<Result> results = movieResponse.getResults();
                    int limit = movieResponse.getEntries();
                    String next = movieResponse.getNext();

                    int count = 0;
                    for (Result responseItem : results) {
                        if (isValid(responseItem)) {
                            String title = responseItem.getOriginalTitleText().getText();
                            int year = responseItem.getReleaseYear().getYear();
                            Image image = responseItem.getPrimaryImage();
                            if (image==null) {continue;}
                            String id = responseItem.getId();
                            String type=responseItem.getTitleType().getText();
                            boolean isSeries=responseItem.getTitleType().getIsSeries();
                            boolean isEpisode=responseItem.getTitleType().getIsEpisode();
                            MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                            movieItems.add(movieItem);
                            count++; // Increment the counter

                            if (count >= limit) {
                                break; // Break the loop if the limit is reached
                            }
                        }
                    }

                    // Notify adapter that data has changed
                    adapter.notifyDataSetChanged();

                    // Fetch the next page of movies if available
                    if (next != null && !next.equals("null")) {
                        fetchNextMoviePage(next);
                    }
                }

                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Handle failure
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isValid(Result responseItem) {
        return responseItem != null;
    }

    private void sortMovieItemsByAverageRating(List<MovieItem> movieItems) {
        Collections.sort(movieItems, (item1, item2) -> {
            // Check for null ratings
            if (item1.getRating() != null && item2.getRating() != null) {
                float rating1 = item1.getRating().getAverageRating();
                float rating2 = item2.getRating().getAverageRating();
                // Compare the ratings in descending order
                return Float.compare(rating2, rating1);
            } else if (item1.getRating() != null) {
                return -1; // item2 has null ratings, so item1 comes first
            } else if (item2.getRating() != null) {
                return 1; // item1 has null ratings, so item2 comes first
            } else {
                return 0; // Both have null ratings, consider them equal
            }
        });
    }

    private void sortMovieItemsByNumVotes(List<MovieItem> movieItems) {
        Collections.sort(movieItems, (item1, item2) -> {
            if (item1.getRating() != null && item2.getRating() != null) {
                int rating1 = item1.getRating().getNumVotes();
                int rating2 = item2.getRating().getNumVotes();
                return Integer.compare(rating2, rating1);
            } else if (item1.getRating() != null) {
                return -1;
            } else if (item2.getRating() != null) {
                return 1;
            } else {
                return 0;
            }
        });
    }





}