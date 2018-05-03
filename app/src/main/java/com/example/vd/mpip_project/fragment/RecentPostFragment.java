package com.example.vd.mpip_project.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by VD on 08.03.2018.
 */

public class RecentPostFragment extends PostListFragment {

    public RecentPostFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("posts")
                .limitToFirst(20);
    }
}
