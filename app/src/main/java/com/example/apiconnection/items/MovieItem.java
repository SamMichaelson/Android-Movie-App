package com.example.apiconnection.items;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieItem implements Parcelable {

    private String id;
    private String title;
    private int year;
    private Image image;
    private String type;
    private boolean isSeries;
    private boolean isEpisode;
    private Ratings rating;


    public MovieItem(String id,String title, int year, Image image, String type, boolean isSeries, boolean isEpisode) {
        this.id = id != null ? id : "Couldn't Find Title";
        this.title = title != null ? title : "Couldn't Find Title";
        this.year = year != 0 ? year : 404;

//        if (image == null || image.getUrl() == null || image.getUrl().isEmpty()) {
//            // Provide a default image URL and size
//            image = new Image();
//            image.setImage("https://images.pexels.com/photos/10224729/pexels-photo-10224729.jpeg", 1920, 1280, "");
//        }
        this.type = type != null ? type : "Couldn't Find Title";
        this.isSeries=isSeries;
        this.isEpisode=isEpisode;
        this.image = image;
    }

    public Ratings getRating() {
        return rating;
    }

    public void setRating(Ratings rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public int getYear() {
        return year;
    }
    public Image getImage() {
        return image;
    }
    public String getType() {
        return type;
    }
    public boolean getIsSeries() {
        return isSeries;
    }
    public boolean getIsEpisode() {
        return isEpisode;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeInt(year);
        dest.writeParcelable((Parcelable) image, flags);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {

        @Override
        public MovieItem createFromParcel(Parcel parcel) {
            String id = parcel.readString();
            String title = parcel.readString();
            int year = parcel.readInt();
            Image image = parcel.readParcelable(Image.class.getClassLoader());
            String type = parcel.readString();

            // Read the boolean values as bytes and convert to boolean
            byte isSeriesByte = parcel.readByte();
            boolean isSeries = isSeriesByte != 0;

            byte isEpisodeByte = parcel.readByte();
            boolean isEpisode = isEpisodeByte != 0;

            return new MovieItem(id, title, year, image, type, isSeries, isEpisode);
        }



        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}

