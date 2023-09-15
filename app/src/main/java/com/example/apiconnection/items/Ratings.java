package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class Ratings {
    @SerializedName("averageRating")
    private float averageRating;
    @SerializedName("numVotes")
    private int numVotes;

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    public float getAverageRating() {
        if (Float.isNaN(averageRating)) {
            numVotes=0;
            return 0.0F; // or any other default value
        }
        return averageRating;
    }


    public int getNumVotes() {
        if ( "null".equals(String.valueOf(numVotes)) )        {
            averageRating=0.0F;
            return 0; // or any other default value
        }
        return numVotes;
    }


}
