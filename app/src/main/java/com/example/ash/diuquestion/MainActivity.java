package com.example.ash.diuquestion;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private android.app.FragmentManager mFragmentManager;
    private MaterialSearchView searchView;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializing views
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("DIU Questions");
        setSupportActionBar(mToolbar);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();



        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
       // mRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mGridLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    protected void onStart() {
        super.onStart();

        recyclerAdapter();
    }



    public void recyclerAdapter() {

        final FirebaseRecyclerAdapter<Data, PostViewHolder> fra = new FirebaseRecyclerAdapter<Data, PostViewHolder>(
                Data.class,
                R.layout.cards,
                PostViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Data model, final int position) {
                if(viewHolder !=null)
                     progressBar.setVisibility(View.GONE);
                viewHolder.setPost_title(model.getPost_title());
                viewHolder.setUrl(model.getUrl());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, DetailsPage.class);
                        intent.putExtra("key", ""+ getRef(position).getKey());
                        startActivity(intent);
                    }
                });



            }
        };

        mRecyclerView.setAdapter(fra);


    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }


        public void setPost_title(String post_title){

            TextView post_title1 = (TextView) mView.findViewById(R.id.postTitle);
            post_title1.setText(post_title);

        }

        public void setUrl(String url){
            ImageView image= (ImageView) mView.findViewById(R.id.post_imageView);

            Glide.with(mView.getContext()).load(url).into(image);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.addQuestionId:
               // mFragmentManager.beginTransaction().replace(R.id.container, new AddQuestionFragment()).addToBackStack(null).commit();
                Intent intent = new Intent(MainActivity.this, AddQuestion.class);
                startActivity(intent);
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
       // int backCount = mFragmentManager.getBackStackEntryCount();


//        if(backCount > 1) {
//            super.onBackPressed();
//        }
         if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        else {
            finish();
        }

    }
}
