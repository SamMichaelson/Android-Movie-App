package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class Episodes {

        @SerializedName("tconst")
        private String tconst;

        @SerializedName("seasonNumber")
        private int seasonNumber;

        @SerializedName("episodeNumber")
        private int episodeNumber;

        public String getTconst() {
            if("null".equals(tconst))
                return "No tconst";
            return tconst;
        }

        public int getSeasonNumber() {
            if("null".equals(String.valueOf(seasonNumber)))
                return 0;
            return seasonNumber;
        }

        public int getEpisodeNumber() {

            if("null".equals(String.valueOf(episodeNumber)))
                return 0;
            return episodeNumber;
        }



}
