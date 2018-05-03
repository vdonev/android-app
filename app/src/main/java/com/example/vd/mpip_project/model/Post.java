package com.example.vd.mpip_project.model;


import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VD on 08.03.2018.
 */

@IgnoreExtraProperties
public class Post {
    public String uid;
    public String author;
    public String title;
    public List<Location> locations;
    public int starCount = 0;
    public Map<String,Boolean> stars = new HashMap<>();
    public Uri profile;
    public String profile_url;
    public String user_profile;

    public Post(){
        locations = new ArrayList<>();
    }

    public Post(String uid, String author, String title, List<Location> locations,String url,String user_profile) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.locations = locations;
        this.profile_url = url;
        this.user_profile = user_profile;
    }

    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("profile_url",profile_url);
        result.put("user_profile",user_profile);

        HashMap<String,Object> temp = new HashMap<>();

        for(int i = 0; i < locations.size(); i++){
            Log.v("for-"+i,locations.get(i).toMap().toString());
            temp.put(Integer.toString(i), locations.get(i).toMap());
        }

        result.put("locations", temp);
        return result;
    }
}
