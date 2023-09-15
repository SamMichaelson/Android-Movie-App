package com.example.apiconnection.items;

import com.example.apiconnection.items.MovieItem;

public class MovieWithRating {
    private MovieItem movie;
    private float rating;

    public MovieWithRating(MovieItem movie, float rating) {
        this.movie = movie;
        this.rating = rating;
    }

    public MovieItem getMovie() {
        return movie;
    }

    public float getRating() {
        return rating;
    }
}
