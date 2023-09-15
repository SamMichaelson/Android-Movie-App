package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class TitleText {
    @SerializedName("text")
    private String text;

    public String getText() {
        if (text==null)
            return "No Text";
        return text;
    }

}
