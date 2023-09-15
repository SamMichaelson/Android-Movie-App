package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class ReleaseDate {
    @SerializedName("day")
    private int day;

    @SerializedName("month")
    private int month;

    @SerializedName("year")
    private int year;

    public int getDay() {
        if("null".equals(String.valueOf(day)))
            return 0;
        return day;
    }

    public int getMonth() {
        if("null".equals(String.valueOf(month)))
            return 0;
        return month;
    }

    public int getYear() {
        if("null".equals(String.valueOf(year)))
            return 0;
        return year;
    }

    // You can also define a constructor and other methods as needed
}
