package com.example.vd.mpip_project.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by VD on 13.03.2018.
 */

@IgnoreExtraProperties
public class Comment {
    public String uid;
    public String author;
    public String text;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }

}
