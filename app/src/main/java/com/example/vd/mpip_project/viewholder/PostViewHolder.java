package com.example.vd.mpip_project.viewholder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vd.mpip_project.MainActivity;
import com.example.vd.mpip_project.R;
import com.example.vd.mpip_project.model.Post;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by VD on 08.03.2018.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public ImageView bodyView; // should be changed
    private Context context;
    private CircleImageView authorPhoto;

    public PostViewHolder(View itemView) {
        super(itemView);
        this.context = itemView.getContext();
        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
        authorPhoto = itemView.findViewById(R.id.post_author_photo);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        //bodyView.setText(post.body);
        //if(! post.profile_url.equals("")){
        Glide.with(context)
                .load(post.profile_url)
                .into(bodyView);
       // }

        if(!post.user_profile.equals("")){
            Glide.with(context)
                    .load(post.user_profile)
                    .into(authorPhoto);
        }

        starView.setOnClickListener(starClickListener);
    }
}
