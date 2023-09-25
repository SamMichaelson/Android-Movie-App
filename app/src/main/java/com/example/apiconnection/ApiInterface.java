package com.example.apiconnection;

import com.example.apiconnection.Movie.MovieResponse;
import com.example.apiconnection.Series.EpisodeResponse;
import com.example.apiconnection.Series.RatingResponse;
import com.example.apiconnection.Series.SeasonCountResponse;
import com.example.apiconnection.Series.SeriesResponse;
import com.example.apiconnection.items.CategoryResponse;
import com.example.apiconnection.items.FavoriteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiInterface {
    @GET("/titles/search/akas/{akaValue}")
    Call<MovieResponse> searchMoviesByAKAs(@Path("akaValue") String akaValue);

    @GET
    Call<MovieResponse> nextMoviePage(@Url String nextPageUrl);

    @GET("/titles/{id}/ratings")
    Call<RatingResponse> searchMoviesRating(@Path("id") String id);

    @GET("/titles/series/{id}/{season}")
    Call<SeriesResponse> episodesOfSeason(@Path("id") String id, @Path("season") String season);


    @GET("/titles/episode/{id}")
    Call<EpisodeResponse> episodeInfo(@Path("id") String id);

    @GET("/titles/seasons/{id}")
    Call<SeasonCountResponse> seasonCount(@Path("id") String id);

    @GET("/titles/x/upcoming")
    Call<MovieResponse> RandomMovies();


    @GET("/titles/search/akas/{akaValue}?sort=year.decr")
    Call<MovieResponse> searchMoviesByAKAsSortYear(@Path("akaValue") String akaValue);



    @GET("/titles/utils/genres")
    Call<CategoryResponse> discover();


    @GET("/titles/{id}")
    Call<FavoriteResponse> favorites(@Path("id") String id);


}
