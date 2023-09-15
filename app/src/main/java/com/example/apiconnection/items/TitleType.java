package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class TitleType {

    @SerializedName("text")
    private String text;

    @SerializedName("isSeries")
    private Boolean isSeries;

    @SerializedName("isEpisode")
    private Boolean isEpisode;


    public String getText() {
        if(text==null)
            return "No text";
        return text;
    }
    public Boolean getIsSeries() {
        return isSeries;
    }
    public Boolean getIsEpisode() {
        return isEpisode;
    }

}
