package com.walmart.gshop.models;

import android.graphics.Bitmap;

/**
 * Created by GleasonK on 7/11/15.
 * <p>
 * ChatMessage is used to hold information that is transmitted using PubNub.
 * A message in this app has a username, message, and timestamp.
 */
public class ChatMessage {
    private String username;
    private String message;
    private long timeStamp;


    private Bitmap image;
    private String imageUri;

    public ChatMessage(String username, String message, long timeStamp, Bitmap image) {
        this.username = username;
        this.message = message;
        this.timeStamp = timeStamp;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isImage() {
        return (image == null) ? false : true;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
