package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class YearRange {

    @SerializedName("year")
    private int year;

    public int getYear() {
        if ("null".equals(String.valueOf(year))) {
            return 0;
        }
        return year;
    }



}
