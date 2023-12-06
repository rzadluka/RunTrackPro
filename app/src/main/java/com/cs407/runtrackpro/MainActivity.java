package com.cs407.runtrackpro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        LinearLayout HomeFragmentButton = findViewById(R.id.home_button);
        HomeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Home")
                        .commit();
            }
        });

        LinearLayout RunFragmentButton = findViewById(R.id.plan_button);
        RunFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, PlanRunFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Plan a Run")
                        .commit();

            }
        });

        LinearLayout StatsFragmentButton = findViewById(R.id.stat_button);
        StatsFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, StatsFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Stats")
                        .commit();

            }
        });

    }
}