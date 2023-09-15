package com.example.apiconnection.Series;

import com.example.apiconnection.items.Episodes;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeriesResponse {
    @SerializedName("results")
    private List<Episodes> results;

    public List<Episodes> getEpisodes() {
        return results;
    }

}
