package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class CategoryResponse {

    @SerializedName("results")
    private List<String> results;

    public List<String> getResults() {
        if(results==null)
            return Collections.emptyList();
        return results;
    }
}
