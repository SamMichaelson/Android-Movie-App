package com.example.apiconnection.Series;

import com.example.apiconnection.items.Result;
import com.google.gson.annotations.SerializedName;

public class EpisodeResponse {

        @SerializedName("results")
        private Result result;

        public Result getResult() {
            return result;
        }
}
