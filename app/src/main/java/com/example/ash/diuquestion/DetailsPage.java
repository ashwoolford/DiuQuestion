package com.example.ash.diuquestion;

import android.content.Intent;
import android.graphics.Matrix;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//+import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailsPage extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView mImageView;
    private DatabaseReference mDataRef;
    private String key;
    private ProgressBar progressBar;
   // private PhotoViewAttacher mAttacher;
//    private Matrix matrix = new Matrix();
//    private ScaleGestureDetector SGD;
//    private float scale = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_page);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        Toast.makeText(getApplicationContext(), ""+key, Toast.LENGTH_SHORT).show();


        mToolbar = (Toolbar) findViewById(R.id.detailsToolbar);
        mImageView = (ImageView) findViewById(R.id.detailsImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
       // SGD = new ScaleGestureDetector(this, new ScaleListener());

        //initializing firebase instance variables
        mDataRef = FirebaseDatabase.getInstance().getReference().child("posts");
        //mToolbar.setTitle();
        //setting toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        title(mDataRef, key);

        mImageView.setOnTouchListener(new ImageMatrixTouchHandler(getApplicationContext()));


    }

    @Override
    protected void onStart() {
        super.onStart();
        getImageUrl(mDataRef, key);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getImageUrl(DatabaseReference mDataRef, String key){
        mDataRef.child(key).child("url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Glide.with(getApplicationContext()).load(dataSnapshot.getValue().toString())
                        .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(mImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void title(DatabaseReference mDataRef, String key){
      mDataRef.child(key).child("post_title").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              mToolbar.setTitle(dataSnapshot.getValue().toString());
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        SGD.onTouchEvent(event);
//        return true;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scale *= detector.getScaleFactor();
//            scale = Math.max(0.1f, Math.min(scale, 5.0f));
//            matrix.setScale(scale, scale);
//            mImageView.setImageMatrix(matrix);
//            return true;
//        }
//    }
}
