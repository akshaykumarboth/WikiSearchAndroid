package com.akshayboth.wikisearch.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.akshayboth.wikisearch.R;
import com.akshayboth.wikisearch.adapter.ResultsAdapter;
import com.akshayboth.wikisearch.pojo.Root;
import com.akshayboth.wikisearch.pojo.WikiPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private RelativeLayout no_data;
    List<WikiPage> wikiPageList;

    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        no_data = (RelativeLayout) findViewById(R.id.no_data_found_layout);

        try{
            sharedpreferences = getSharedPreferences(getResources().getString(R.string.shared_preference_key), Context.MODE_PRIVATE);
            //editor = sharedpreferences.edit();
            String jsonresponse = sharedpreferences.getString(getResources().getString(R.string.searchList), "");

            //String jsonresponse = getIntent().getExtras().getString("jsonresponse");
            System.out.println(jsonresponse);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

            Root root = gson.fromJson(jsonresponse, Root.class);
            wikiPageList = root.getQuery().getPages();
            for (WikiPage p : wikiPageList) {
                System.out.print(p.getTitle());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        setRecyclerView();
    }

    private void setRecyclerView(){

        if (wikiPageList != null && wikiPageList.size() != 0) {

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            ResultsAdapter adapter = new ResultsAdapter(wikiPageList,this);
            recyclerView.setAdapter(adapter);
        } else {
            showNoData();
        }



    }

    public void showNoData() {
        recyclerView.setVisibility(View.GONE);
        no_data.setVisibility(View.VISIBLE);
    }



    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(ResultsActivity.this, MainActivity.class));
        //overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finish();
        return true;
    }

}
