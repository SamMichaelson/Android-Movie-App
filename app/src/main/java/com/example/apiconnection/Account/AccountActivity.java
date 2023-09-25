package com.example.apiconnection.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiconnection.ApiInterface;
import com.example.apiconnection.Discover.DiscoverActivity;
import com.example.apiconnection.MainActivity;
import com.example.apiconnection.Movie.MovieAdapter;
import com.example.apiconnection.R;
import com.example.apiconnection.RetrofitClient;
import com.example.apiconnection.Search.SearchActivity;
import com.example.apiconnection.Series.MovieDetailActivity;
import com.example.apiconnection.Series.RatingResponse;
import com.example.apiconnection.items.FavoriteResponse;
import com.example.apiconnection.items.Image;
import com.example.apiconnection.items.MovieItem;
import com.example.apiconnection.items.Result;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountActivity extends AppCompatActivity {
    Button btnLogOut;
    Button btnOverlay;
    Button btnSave;
    private FirebaseAuth mAuth;
    private FrameLayout overlayLayout;

    List<MovieItem> favoriteMovies = new ArrayList<>();
    TextInputEditText editTextName;
    TextInputEditText editTextSurname;
    TextInputEditText editTextEmail;
    DatabaseReference rootDatabaseref;
    private MovieAdapter favoriteMovieAdapter;

    public AccountActivity() {
    }

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        rootDatabaseref = FirebaseDatabase.getInstance().getReference();

        assert user != null;
        String userId = user.getUid();


        btnOverlay.setOnClickListener(view -> {
            overlayLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.button).setVisibility(View.GONE);
            findViewById(R.id.myMoviesButton).setVisibility(View.GONE);
            findViewById(R.id.mySeriesButton).setVisibility(View.GONE);
            findViewById(R.id.switchNotify).setVisibility(View.GONE);
            findViewById(R.id.recyclerViewMovies2).setVisibility(View.GONE);

            rootDatabaseref.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String surname = dataSnapshot.child("surname").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);

                        editTextName.setText(name != null ? name : "");
                        editTextSurname.setText(surname != null ? surname : "");
                        editTextEmail.setText(email != null ? email : "");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        });


        btnSave.setOnClickListener(view -> {
            overlayLayout.setVisibility(View.GONE);
            findViewById(R.id.button).setVisibility(View.VISIBLE);
            findViewById(R.id.myMoviesButton).setVisibility(View.VISIBLE);
            findViewById(R.id.mySeriesButton).setVisibility(View.VISIBLE);
            findViewById(R.id.switchNotify).setVisibility(View.VISIBLE);
            findViewById(R.id.recyclerViewMovies2).setVisibility(View.VISIBLE);

            TextInputEditText editTextName = findViewById(R.id.name);
            TextInputEditText editTextSurname = findViewById(R.id.surname);
            TextInputEditText editTextEmail = findViewById(R.id.email);
            String name = Objects.requireNonNull(editTextName.getText()).toString();
            String surname = Objects.requireNonNull(editTextSurname.getText()).toString();
            String email = Objects.requireNonNull(editTextEmail.getText()).toString();
            rootDatabaseref.child("users").child(userId).child("name").setValue(name);
            rootDatabaseref.child("users").child(userId).child("surname").setValue(surname);
            rootDatabaseref.child("users").child(userId).child("email").setValue(email);


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



        RecyclerView recyclerViewFavoriteMovies = findViewById(R.id.recyclerViewMovies2);
        recyclerViewFavoriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        favoriteMovieAdapter = new MovieAdapter(favoriteMovies);
        favoriteMovieAdapter.setOnItemClickListener(movie -> fetchRating(movie.getId(), movie , movie.getType() , movie.getIsSeries() , movie.getIsEpisode()));

        recyclerViewFavoriteMovies.setAdapter(favoriteMovieAdapter);

        rootDatabaseref.child("users").child(userId).child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                        boolean isFavorite = Boolean.TRUE.equals(movieSnapshot.getValue(Boolean.class));
                        if (isFavorite) {
                            String movieId = movieSnapshot.getKey();
                            getFavoriteMovieInfo(movieId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });


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
                Toast.makeText(AccountActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void getFavoriteMovieInfo(String movieId) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();
        apiInterface.favorites(movieId).enqueue(new Callback<FavoriteResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<FavoriteResponse> call, @NonNull Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FavoriteResponse favoriteResponse = response.body();
                    Result result = favoriteResponse.getResults();

                    if (result != null) {
                        String title = result.getOriginalTitleText().getText(); // Use getTitleText() for the movie title.
                        int year = result.getReleaseYear().getYear();
                        Image image = result.getPrimaryImage();
                        String id = result.getId();
                        String type = result.getTitleType().getText();
                        boolean isSeries = result.getTitleType().getIsSeries();
                        boolean isEpisode = result.getTitleType().getIsEpisode();
                        MovieItem movieItem = new MovieItem(id, title, year, image, type, isSeries, isEpisode);
                        favoriteMovies.add(movieItem);
                        favoriteMovieAdapter.notifyDataSetChanged();
                    }

//                } else {
//                    Image image = new Image();
//                    image.setImage("https://m.media-amazon.com/images/M/MV5BNmMxMGE3MzUtNjI5Yy00MzI0LTkyMzktYzYyMjhiZjYyYTQ2XkEyXkFqcGdeQXVyMTAwOTI0MjU0._V1_.jpg", 2000, 2000, "your_image_id");
//
//                    favoriteMovies.add(new Response(
//                            "tt11916562",
//                            "The Dead Rose",
//                            2024,
//                            image,
//                            "TV Series",
//                            true,
//                            false
//                    ));
//
//                    favoriteMovieAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onFailure(@NonNull Call<FavoriteResponse> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }


    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    // Inside onStart method

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = rootDatabaseref.child("users").child(userId); // Updated this line

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String surname = dataSnapshot.child("surname").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);

                        TextView tvUsername = findViewById(R.id.tvUsername);
                        tvUsername.setText(name + " " + surname);
                        TextView tvEmail = findViewById(R.id.tvEmail);
                        tvEmail.setText(email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }

    private Intent createMovieDetailIntent(MovieItem movie, float averageRating, int numVotes, String type, boolean isSeries, boolean isEpisode) {
        Intent intent = new Intent(AccountActivity.this, MovieDetailActivity.class);

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
}
