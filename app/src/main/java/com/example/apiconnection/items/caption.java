package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class caption {

    @SerializedName("plainText")
    private String plainText;

    public String getPlainText() {
        if(plainText==null)
            return "No Plain Text";
        return plainText;
    }
}
