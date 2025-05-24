package com.datn.zimstay.model;

public class Image {
    private int id;
    private String imageUrl;
    private boolean isImage;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isImage() { return isImage; }
    public void setImage(boolean image) { isImage = image; }
}