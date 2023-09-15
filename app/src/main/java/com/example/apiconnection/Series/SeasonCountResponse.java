package com.example.apiconnection.Series;

import com.google.gson.annotations.SerializedName;

public class SeasonCountResponse {

        @SerializedName("results")
        private int result;

        public int getResult() {
            return result;
        }
}
