package com.example.apiconnection.Account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    private final int IMAGE_PICKER_REQUEST_CODE=123;

    public AccountActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(view -> openImagePickerDialog());
        
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

        if(user == null) {
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        }else {
            String userId = user.getUid();


            btnOverlay.setOnClickListener(view -> {
                overlayLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.button).setVisibility(View.GONE);
                findViewById(R.id.favoriteTv).setVisibility(View.GONE);
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
                findViewById(R.id.favoriteTv).setVisibility(View.VISIBLE);
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

                handler.removeCallbacks(refreshRunnable);
                handler.post(refreshRunnable);

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
            favoriteMovieAdapter.setOnItemClickListener(movie -> fetchRating(movie.getId(), movie, movie.getType(), movie.getIsSeries(), movie.getIsEpisode()));

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

            // Handle the result here
            // Now you can proceed with imageUri
            ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Handle the result here
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                Uri imageUri = data.getData();
                                // Now you can proceed with imageUri
                                uploadImageToFirebaseStorage(imageUri);
                            }
                        }
                    }
            );
        }
    }

    private final Handler handler = new Handler();
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, REFRESH_INTERVAL_MILLISECONDS);
        }
    };

    private static final long REFRESH_INTERVAL_MILLISECONDS = 60000;



    @Override
    protected void onStop() {
        super.onStop();

        // Stop the data refresh when thes activity is no longer visible
        handler.removeCallbacks(refreshRunnable);
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
        handler.post(refreshRunnable);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = rootDatabaseref.child("users").child(userId);

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

                        // Check if the profileImageUrl field exists in the database
                        if (dataSnapshot.hasChild("profileImageUrl")) {
                            String imageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                            // Load the profile image into an ImageView using Picasso
                            ImageView imageView = findViewById(R.id.imageView);
                            Picasso.get().load(imageUrl).into(imageView);
                        }
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

    private void openImagePickerDialog() {
        // Implement code to open an image picker dialog or library
        // After the user selects an image, you'll get the image URI

        // Example code for getting image URI from the image picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
    }

    // Handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();

                // Upload the selected image to Firebase Cloud Storage
                uploadImageToFirebaseStorage(imageUri);
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        // Get a reference to the Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a unique filename for the image (e.g., using user's ID)
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String imageName = "user_images/" + userId + "/" + System.currentTimeMillis() + ".jpg";

        // Create a reference to the image location in Firebase Storage
        StorageReference imageRef = storageRef.child(imageName);

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image upload successful, you can get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Save the image URL to Firebase Realtime Database
                        saveImageUrlToDatabase(imageUrl);

                        // Load the image into an ImageView
                        ImageView imageView = findViewById(R.id.imageView);
                        Picasso.get().load(imageUrl).into(imageView);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle image upload failure
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = rootDatabaseref.child("users").child(userId);

            // Save the image URL to the database
            userRef.child("profileImageUrl").setValue(imageUrl);
        }
    }


}
