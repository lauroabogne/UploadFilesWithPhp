package com.simpleinfo.uploadfileswithphp;

import android.support.v7.widget.CardView;

public class Data {
    private String imageName;
    private String imagePath;
    private boolean isUploaded = false;


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
