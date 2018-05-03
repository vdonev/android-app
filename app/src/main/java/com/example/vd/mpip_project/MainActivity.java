package com.example.vd.mpip_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vd.mpip_project.fragment.MyPostFragment;
import com.example.vd.mpip_project.fragment.RecentPostFragment;

import com.example.vd.mpip_project.model.Post;
import com.example.vd.mpip_project.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST_PROFILE = 4;

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private StorageReference firebaseStorage;
    private DatabaseReference firebaseDatabase;
    private ImageView drawerImage;
    private String userKey;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_view);
        View mview = navigationView.getHeaderView(0);

        drawerImage = mview.findViewById(R.id.drawer_img);


        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase.child("users/"+userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(user != null && user.profile_url != null){
                    Glide.with(getApplicationContext())
                            .load(user.profile_url)
                            .into(drawerImage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new RecentPostFragment(),
                    new MyPostFragment()
            };
            private final String[] mFragmentNames = new String[] {
                    /*getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts)******/
                    "Recent","My posts"
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };


        // Set up the ViewPager with the sections adapter.

        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int i = item.getItemId();

                if (i == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();

                }else if(i == R.id.publish_trip){
                    startActivity(new Intent(getApplicationContext(), NewPostActivity.class));
                }
                else if(i == R.id.change_profile){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_PROFILE);
                }else if (i == R.id.my_trips){
                    mViewPager.setCurrentItem(2,true);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST_PROFILE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 75, 75, true);
                drawerImage.setImageBitmap(bitmap);
                firebaseStorage.child(userKey).child("profile_url").putFile(uri).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final String url = taskSnapshot.getDownloadUrl().toString();
                                //firebaseDatabase.child("users").child(userKey).child("profile_url").setValue(url);

                                firebaseDatabase.child("user-posts/"+userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Map<String,Object> map = new HashMap<>();
                                        map.put("users/"+userKey+"/profile_url",url);

                                        for(DataSnapshot d : dataSnapshot.getChildren()){
                                            map.put("user-posts/"+userKey+"/"+d.getKey()+"/user_profile",url);
                                            map.put("posts/"+d.getKey()+"/user_profile",url);
                                        }
                                        firebaseDatabase.updateChildren(map);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                );


            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflate = getMenuInflater();
        mMenuInflate.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        }else if(i == R.id.action_newpost){
            startActivity(new Intent(this, NewPostActivity.class));
            finish();
            return true;
        }else if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}
