package com.neopick.soundboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<String> myDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_activity);

        recyclerView = findViewById(R.id.recycler_view);

        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        myDataSet = new ArrayList<String>();
        myDataSet.add("Coucou");
        myDataSet.add("comment");
        myDataSet.add("ça");
        myDataSet.add("va?");


        mAdapter = new RecyclerAdapter(myDataSet);
        recyclerView.setAdapter(mAdapter);
    }
}
