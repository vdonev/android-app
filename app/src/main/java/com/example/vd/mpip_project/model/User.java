package com.example.vd.mpip_project.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by VD on 08.03.2018.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String profile_url;

    public User(){}

    public User(String username, String email){
        this.username = username;
        this.email = email;
        this.profile_url = "";
    }
}
