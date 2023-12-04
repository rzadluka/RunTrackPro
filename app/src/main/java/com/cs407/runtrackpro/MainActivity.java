package com.cs407.runtrackpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager =getSupportFragmentManager();
        Button HomeFragmentButton =findViewById(R.id.home_button);
        HomeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, HomeFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Home")
                        .commit();
            }
        });

        Button RunFragmentButton =findViewById(R.id.plan_button);
        RunFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, PlanRunFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Fragment1")
                        .commit();

            }
        });

        Button StatsFragmentButton =findViewById(R.id.stat_button);
        StatsFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView,StatsFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Fragment1")
                        .commit();

            }
        });

    }

    public void EnabledMilesButtonByDefault(){

    }
}