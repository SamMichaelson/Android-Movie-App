package com.example.apiconnection.Series;

import com.example.apiconnection.items.Ratings;
import com.google.gson.annotations.SerializedName;

public class RatingResponse {
    @SerializedName("results")
    private Ratings results;


    public Ratings getResults() {
        if (results != null)
            return results;
        return new Ratings();

    }

}
