package com.stargazers.ncsvcemk200stargazers.models;

import java.sql.Timestamp;
import java.util.ArrayList;

public class StatusModel {

    private String stageName;
    private Timestamp timestamp;
    private ArrayList<String> images;

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

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
