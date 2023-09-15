package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("id")
    private String id;

    @SerializedName("primaryImage")
    private Image primaryImage;

    @SerializedName("titleType")
    private TitleType titleType;

//    @SerializedName("titleText")
//    private TitleText titleText;

    @SerializedName("originalTitleText")
    private TitleText originalTitleText;

    @SerializedName("releaseYear")
    private YearRange releaseYear;

    @SerializedName("releaseDate")
    private ReleaseDate releaseDate;

    public String getId() {
        if(id==null)
            return "No id";
        return id;
    }

    public Image getPrimaryImage() {
        if (primaryImage == null || primaryImage.getUrl().equals("null")) {
            return null;
        }
        return primaryImage;
    }


    public TitleType getTitleType() {
        if(titleType!=null)
            return titleType;
        return new TitleType();
    }

//    public TitleText getTitleText() {
//        return titleText;
//    }

    public TitleText getOriginalTitleText() {
        if (originalTitleText==null)
            return new TitleText();
        return originalTitleText;
    }

    public YearRange getReleaseYear() {
        if (releaseYear == null || releaseYear.equals("null"))
            return new YearRange();
        return releaseYear;
    }


    public ReleaseDate getReleaseDate() {
        if (releaseDate == null || releaseDate.equals("null"))
            return new ReleaseDate();
        return releaseDate;
    }
}
