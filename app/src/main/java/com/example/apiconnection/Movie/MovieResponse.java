package com.example.apiconnection.Movie;

import com.example.apiconnection.items.Result;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<Result> results;
    @SerializedName("entries")
    private int entries;
    @SerializedName("next")
    private String next;

    public List<Result> getResults() {
        if(results==null)
            return Collections.emptyList();
        return results;
    }
    public int getEntries() {
        if ("null".equals(String.valueOf(entries))) {
            return 0;
        }
        return entries;
    }
    //we need next to sty null because we check if its null in mainActivity so that it wont get to the next one
    public String getNext() {
        return next;
    }
}
