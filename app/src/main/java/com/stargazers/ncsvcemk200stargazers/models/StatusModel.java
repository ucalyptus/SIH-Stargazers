package com.stargazers.ncsvcemk200stargazers.models;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class StatusModel {

    private String stageName;
    private Timestamp timestamp;
    private String images;
    private GeoPoint geotag;

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public GeoPoint getGeotag() {
        return geotag;
    }

    public void setGeotag(GeoPoint geotag) {
        this.geotag = geotag;
    }
}
