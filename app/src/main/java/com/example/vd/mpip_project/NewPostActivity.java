package com.example.vd.mpip_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vd.mpip_project.model.Location;
import com.example.vd.mpip_project.model.Post;
import com.example.vd.mpip_project.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final int PICK_IMAGE_REQUEST = 2;

    private EditText mTitle;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private Button mButtonLocation;
    private Button mButtonAddImage;
    private Button mButtonUpload;
    private ListView dayList;
    private CustomAdapter adapter;
    private FloatingActionButton mSubmitButton;
    private List<Location> mLocations;
    private ImageButton mImageButton;
    private Uri profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        mLocations = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mButtonLocation = findViewById(R.id.addlocation);
        dayList = findViewById(R.id.listday);
        adapter = new CustomAdapter(getApplicationContext(),mLocations);
        dayList.setAdapter(adapter);
        mButtonAddImage = findViewById(R.id.addphoto);
        mButtonUpload = findViewById(R.id.addpost);
        mTitle = findViewById(R.id.field_title);
        mImageButton = findViewById(R.id.imageButton);

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost(mTitle.getText().toString(),mLocations);
            }
        });

        mButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        mButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < adapter.getCount(); i++){
                    View v = dayList.getChildAt(i);
                    mLocations.get(i).description = ((TextView) v.findViewById(R.id.description)).getText().toString();
                }
                Intent intent = new Intent(getApplicationContext(), GetLocation.class);
                startActivityForResult(intent,1);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 3);
            }
        });
        /*mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
                mLocations.add(new Location(data.getStringExtra("Location"),""));
                adapter.notifyDataSetChanged();
        }else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                mLocations.get(mLocations.size() - 1 ).image = bitmap;
                mLocations.get(mLocations.size() - 1 ).uri = uri;
                Log.v("asd","IMAGE SELECTED");
                adapter.notifyDataSetChanged();

            }catch (IOException e){
                e.printStackTrace();
            }
        }else if(requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null){
            //this should be changed
            Uri uri = data.getData();
            profile = uri;
        }
    }

    //this should not be here just testing

    class CustomAdapter extends BaseAdapter{
        private Context mContext;
        private List<Location> locations ;

        public CustomAdapter(Context mContext, List<Location> locations) {
            this.mContext = mContext;
            this.locations = locations;
        }

        @Override
        public int getCount() {
            return locations.size();
        }

        @Override
        public Object getItem(int i) {
            return locations.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(mContext , R.layout.list_item_location,null);
            TextView location = (TextView) v.findViewById(R.id.location);
            final TextView description = (TextView) v.findViewById(R.id.description);
            ImageView imageView = (ImageView) v.findViewById(R.id.locationimage);

            description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        locations.get(i).description = description.getText().toString();
                    }
                }
            });

            location.setText(locations.get(i).location);
            if(locations.get(i).description.equals("")){
                description.setHint("Write something about " + locations.get(i).location);
            }else {
                Log.v("loc", description.getText().toString());
                description.setText(locations.get(i).description);
            }

            if(locations.get(i).image != null){
                imageView.setImageBitmap(locations.get(i).image);
            }
            location.setTextColor(Color.BLACK);
            description.setTextColor(Color.BLACK);
            return v;
        }
    }

    //for my posts
    private void submitPost(final String title,final List<Location> list){

        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            //writeNewPost(userId, user.username, title, body);
                            writeNewPost(userId, user.username, title, list, user.profile_url);
                        }

                        // Finish this Activity, back to the stream
                        //setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        //setEditingEnabled(true);
                    }
                });
    }


    private void writeNewPost(final String userId, String username, String title, List<Location> list, String user_profile){
        final String key = mDatabase.child("posts").push().getKey();
        Uri uri;
        String url = "";

        Post post = new Post(userId, username, title, list, url,user_profile);

        Map<String, Object> postValues = post.toMap();

        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);

        for(int i=0; i < list.size(); i++){
            if(list.get(i).image != null){
                final int finalI = i;
                mStorageRef.child(userId).child(key).child(i+"").child("img.jpg").putFile(list.get(i).uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                mDatabase.child("posts").child(key).child("locations").child(finalI+"").child("image_url").setValue(downloadUri.toString());

                            }
                        });

            }
        }

        if(profile != null){
            mStorageRef.child(userId).child(key).child("profile").child("img.jpg").putFile(profile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            mDatabase.child("posts").child(key).child("profile_url").setValue(downloadUri.toString());
                            mDatabase.child("user-posts").child(userId).child(key+"/profile_url").setValue(downloadUri.toString());

                        }
                    });
        }


        /*
        for(int i = 0 ; i < list.size(); i++){
            if(list.get(i).image != null){

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                list.get(i).image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = mStorageRef.child(userId).child(key).child(i+"").child("img.jpg").putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.v("Uploading img","Failed");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Log.v("Uploading img","Good");
                    }
                });

            }
        }*/


    }
/*  this is working okey with submiting post to db
    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }


        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }*/
}
