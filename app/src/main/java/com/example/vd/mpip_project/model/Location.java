package com.example.vd.mpip_project.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.ObjectsCompat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VD on 16.04.2018.
 */

@IgnoreExtraProperties
public class Location{
    public String location;
    public String description;
    public Bitmap image;
    public Uri uri;
    public String image_url;
    public Location() {
    }

    public Location(String location, String description) {
        this.location = location;
        this.description = description;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("location",location);
        map.put("description",description);
        map.put("image_url","");

        return map;
    }
}