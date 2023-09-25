package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class FavoriteResponse {

    @SerializedName("results")
    private Result results;

    public Result getResults() {
        return results;
    }
}
