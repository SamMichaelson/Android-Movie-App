package com.example.apiconnection.items;

import android.os.Parcel;
import android.os.Parcelable;

public class SeriesItem implements Parcelable {

    private String id;
    private String title;
    private int year;
    private Image image;
    private String type;
    private String date;
    private boolean isSeries;
    private boolean isEpisode;
    private ReleaseDate releaseDate;

    private int seasonNo;
    private int episodeNo;

    public SeriesItem(String id, String title, int year, Image image, String type, boolean isSeries, boolean isEpisode,int seasonNo, int episodeNo,String date) {
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
        this.seasonNo = seasonNo ;
        this.episodeNo = episodeNo ;
        this.date = date ;
    }



    public String getId() {
        if ("null".equals(String.valueOf(id)))
            return "No id";
        return id;
    }
    public String getTitle() {

        if ("null".equals(String.valueOf(title)))
            return "No Title";
        return title;
    }
    public String getDate() {

        if ("null".equals(String.valueOf(date)))
            return "No date";
        return date;
    }
    public int getYear() {
        if ("null".equals(String.valueOf(year)))
            return 0;
        return year;
    }
    public Image getImage() {
        if ("null".equals(String.valueOf(image)))
            return null;
        return image;
    }
    public ReleaseDate getReleaseDate() {
        if ("null".equals(String.valueOf(releaseDate)))
            return null;
        return releaseDate;
    }
    public String getType() {
        if ("null".equals(String.valueOf(type)))
            return "No type";
        return type;
    }

    public int getSeasonNo() {
        if ("null".equals(String.valueOf(seasonNo)))
            return 0;
        return seasonNo;
    }
    public int getEpisodeNo() {
        if ("null".equals(String.valueOf(episodeNo)))
            return 0;
        return episodeNo;
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

    public static final Creator<SeriesItem> CREATOR = new Creator<SeriesItem>() {

        @Override
        public SeriesItem createFromParcel(Parcel parcel) {
            String id = parcel.readString();
            if (id == null) {
                id = "No id";
            }

            String title = parcel.readString();
            if (title == null) {
                title = "No title";
            }

            int year = parcel.readInt();
            if ("null".equals( String.valueOf(year) )) {
                year = 0;
            }

            Image image = parcel.readParcelable(Image.class.getClassLoader());
            if (image == null) {
                image = new Image();
            }

            String type = parcel.readString();
            if (type == null) {
                type = "No type";
            }
            String date = parcel.readString();
            if (date == null) {
                date = "No date";
            }
            int seasonNo = parcel.readInt();
            if ("null".equals( String.valueOf(seasonNo) )) {
                seasonNo = 0;
            }
            int episodeNo = parcel.readInt();
            if ("null".equals( String.valueOf(episodeNo) )) {
                episodeNo = 0;
            }
            // Read the boolean values as bytes and convert to boolean
            byte isSeriesByte = parcel.readByte();
            boolean isSeries = isSeriesByte != 0;

            byte isEpisodeByte = parcel.readByte();
            boolean isEpisode = isEpisodeByte != 0;

            return new SeriesItem(id, title, year, image, type, isSeries, isEpisode, seasonNo, episodeNo,date);
        }




        @Override
        public SeriesItem[] newArray(int size) {
            return new SeriesItem[size];
        }
    };
}

