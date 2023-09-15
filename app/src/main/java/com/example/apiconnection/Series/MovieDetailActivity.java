package com.example.apiconnection.Series;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apiconnection.ApiInterface;
import com.example.apiconnection.R;
import com.example.apiconnection.RetrofitClient;
import com.example.apiconnection.items.Episodes;
import com.example.apiconnection.items.Image;
import com.example.apiconnection.items.Result;
import com.example.apiconnection.items.SeriesItem;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    // Constants for intent extras
    public static final String EXTRA_MOVIE_TITLE = "extra_movie_title";
    public static final String EXTRA_MOVIE_YEAR = "extra_movie_year";
    public static final String EXTRA_MOVIE_IMAGE_URL = "extra_movie_image_url";
    public static final String EXTRA_MOVIE_PLAINTEXT = "extra_movie_plain_text";
    public static final String EXTRA_MOVIE_AVERAGE_RATING = "extra_movie_avg_rating";
    public static final String EXTRA_MOVIE_NUM_VOTES = "extra_movie_num_votes";
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    public static final String EXTRA_MOVIE_TYPE = "extra_movie_type";
    public static final String EXTRA_MOVIE_IS_SERIES = "extra_movie_is_series";
    public static final String EXTRA_MOVIE_IS_EPISODE = "extra_movie_is_episode";
    public static final String EXTRA_SERIES_ITEMS = "extra_series_item";

    private SeriesAdapter seriesAdapter;
    private SeasonAdapter seasonAdapter;
    private List<SeriesItem> seriesItems = new ArrayList<>();
    private final List<String> seasonItems = new ArrayList<>();

    GridLayoutManager gridLayoutManager;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        seriesItems = getIntent().getParcelableArrayListExtra(EXTRA_SERIES_ITEMS);
        if (seriesItems == null) {
            seriesItems = new ArrayList<>();
        }

        RecyclerView recyclerViewSeries = findViewById(R.id.recyclerViewSeries);
        gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerViewSeries.setLayoutManager(gridLayoutManager);
        seriesAdapter = new SeriesAdapter(seriesItems);
        recyclerViewSeries.setAdapter(seriesAdapter);
        seriesAdapter.notifyDataSetChanged();

        RecyclerView recyclerViewSeasons = findViewById(R.id.recyclerViewSeasons);
        gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        recyclerViewSeasons.setLayoutManager(gridLayoutManager);
        seasonAdapter = new SeasonAdapter(seasonItems);
        recyclerViewSeasons.setAdapter(seasonAdapter);
        seasonAdapter.notifyDataSetChanged();
        seasonAdapter.setOnItemClickListener(seasonNumber -> {
            // Handle season item click here
            seriesItems.clear();
            seasonItems.clear();
            fetchEpisodes(getIntent().getStringExtra(EXTRA_MOVIE_ID), seasonNumber);
        });

        int movieYear = getIntent().getIntExtra(EXTRA_MOVIE_YEAR, 0);
        String movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        String movieImageUrl = getIntent().getStringExtra(EXTRA_MOVIE_IMAGE_URL);
        String moviePlainText = getIntent().getStringExtra(EXTRA_MOVIE_PLAINTEXT);
        float movieAverageRating = getIntent().getFloatExtra(EXTRA_MOVIE_AVERAGE_RATING, 0);
        int movieNumVotes = getIntent().getIntExtra(EXTRA_MOVIE_NUM_VOTES, 0);
        String movieId = "IMDB id: " + getIntent().getStringExtra(EXTRA_MOVIE_ID);
        String movieType = "Type: " + getIntent().getStringExtra(EXTRA_MOVIE_TYPE);

        ImageView imageView3 = findViewById(R.id.imageView3);
        TextView yearTextView = findViewById(R.id.yearTextView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        ImageView imageView = findViewById(R.id.imageView);
        ImageView imageView2 = findViewById(R.id.imageView2);

        if (getIntent().hasExtra(EXTRA_MOVIE_YEAR)) {
            movieYear = getIntent().getIntExtra(EXTRA_MOVIE_YEAR, 0);
        }

        // Load movie images using Glide
        Glide.with(this).load(movieImageUrl).into(imageView);
        Glide.with(this).load(movieImageUrl).into(imageView2);
        Glide.with(this)
                .load("https://play-lh.googleusercontent.com/8Wo6Eg3iUaLAz_tFaxGxW9QP3crthfIxXMILX84FMbQHgXHY2ewxf_lzYhpveG0iJQ=w240-h480-rw")
                .into(imageView3);

        // Set TextView values
        yearTextView.setText(String.valueOf(movieYear));
        titleTextView.setText(movieTitle);

        TextView plainTextTextView = findViewById(R.id.plainTextTextView);
        TextView averageRatingTextView = findViewById(R.id.averageRatingTextView);
        TextView numVotesTextView = findViewById(R.id.numVotesTextView);
        TextView idTextView = findViewById(R.id.idTextView);
        TextView typeTextView = findViewById(R.id.typeTextView);

        idTextView.setText(movieId);
        typeTextView.setText(movieType);

        plainTextTextView.setText(moviePlainText);
        averageRatingTextView.setText(String.valueOf(movieAverageRating));
        numVotesTextView.setText(String.valueOf(movieNumVotes));

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imdbUrl = "https://www.imdb.com/title/" + movieId + "/";
                goToUrl(imdbUrl);
            }

            private void goToUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        if ("true".equals(getIntent().getStringExtra(EXTRA_MOVIE_IS_SERIES))) {
            fetchEpisodes(getIntent().getStringExtra(EXTRA_MOVIE_ID), 1);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchEpisodes(String id, int season) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();
        fetchSeasons(id);

        // Create a queue to manage episode requests
        Queue<Episodes> episodeQueue = new LinkedList<>();

        apiInterface.episodesOfSeason(id, String.valueOf(season)).enqueue(new Callback<SeriesResponse>() {
            @Override
            public void onResponse(@NonNull Call<SeriesResponse> call, @NonNull Response<SeriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Episodes> episodes = response.body().getEpisodes();

                    // Add episodes to the queue
                    episodeQueue.addAll(episodes);

                    // Start processing episodes
                    processNextEpisode(episodeQueue);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SeriesResponse> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void processNextEpisode(Queue<Episodes> episodeQueue) {
        if (!episodeQueue.isEmpty()) {
            Episodes episode = episodeQueue.poll();
            assert episode != null;
            String tconst = episode.getTconst();
            int seasonNumber = episode.getSeasonNumber();
            int episodeNumber = episode.getEpisodeNumber();

            // Fetch episode information
            fetchInfo(tconst, seasonNumber, episodeNumber, new EpisodeFetchCallback() {
                @Override
                public void onEpisodeFetched(SeriesItem episodeItem) {
                    // Add the episode item to the list and notify the adapter
                    seriesItems.add(episodeItem);
                    seriesAdapter.notifyDataSetChanged();

                    // Process the next episode
                    processNextEpisode(episodeQueue);
                }
            });
        }
    }

    interface EpisodeFetchCallback {
        void onEpisodeFetched(SeriesItem episodeItem);
    }

    private void fetchSeasons(String id) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();
        apiInterface.seasonCount(id).enqueue(new Callback<SeasonCountResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<SeasonCountResponse> call, @NonNull Response<SeasonCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SeasonCountResponse seasonCountResponseResponse = response.body();
                    int result = seasonCountResponseResponse.getResult();
                    for (int i = 0; i < result; i++) {
                        seasonItems.add(String.valueOf(i + 1));
                    }

                }
                seasonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<SeasonCountResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void fetchInfo(String id, int seasonNo, int episodeNo, EpisodeFetchCallback callback) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient();

        apiInterface.episodeInfo(id).enqueue(new Callback<EpisodeResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<EpisodeResponse> call, @NonNull Response<EpisodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EpisodeResponse episodeResponse = response.body();
                    Result result = episodeResponse.getResult();

                    if (result != null) {
                        boolean isSeries = result.getTitleType().getIsSeries();
                        boolean isEpisode = result.getTitleType().getIsEpisode();
                        if (isEpisode) {
                            // Access properties of the result object
                            String title = result.getOriginalTitleText().getText();
                            int year = result.getReleaseYear().getYear();
                            Image image = result.getPrimaryImage();
                            String id = result.getId();
                            String type = result.getTitleType().getText();
                            String date = "";
                            if (result.getReleaseDate() != null) {
                                int day = result.getReleaseDate().getDay();
                                int month = result.getReleaseDate().getMonth();
                                int yearr = result.getReleaseDate().getYear();
                                date = formatDate(day, month, yearr);
                            }
                            SeriesItem item = new SeriesItem(id, title, year, image, type, isSeries, true, seasonNo, episodeNo, date);
                            callback.onEpisodeFetched(item);
                        }
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<EpisodeResponse> call, @NonNull Throwable t) {

            }
        });
    }

    public String formatDate(int day, int month, int year) {
        // Check if the provided day and month are within valid ranges
        if (day < 1 || day > 31 || month < 1 || month > 12) {
            return "Invalid date";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1); // Month is 0-based
        calendar.set(Calendar.YEAR, year);

        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        String[] months = dfs.getMonths();

        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
        String monthStr = months[month - 1];

        return String.format(Locale.getDefault(), "%s %02d %s %04d", dayOfWeek, day, monthStr, year);
    }
}
