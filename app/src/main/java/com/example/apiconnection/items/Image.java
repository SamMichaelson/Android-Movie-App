package com.example.apiconnection.items;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("height")
    private int height;
    @SerializedName("id")
    private String id;
    @SerializedName("url")
    private String url;
    @SerializedName("width")
    private int width;

    public com.example.apiconnection.items.caption getCaption() {
        if(caption==null)
            return new caption();
        return caption;
    }

    @SerializedName("caption")
    private caption caption;

    public int getHeight() {
        if("null".equals( String.valueOf(height) ) )
            return 0;
        return height;
    }


    public String getId() {
        if (id==null)
            return "No id";
        return id;
    }

    public String getUrl() {
        if(url==null)
            return "No url";
        return url;
    }


    public int getWidth() {
        if ( "null".equals(String.valueOf(width) ) )
            return 0;
        return width;
    }

    public void setImage(String url,int height, int width, String id) {

        this.url = url;
        this.height = height;
        this.width = width;
        this.id = id;
    }
}
