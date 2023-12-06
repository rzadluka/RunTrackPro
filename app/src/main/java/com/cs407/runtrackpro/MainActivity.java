package com.cs407.runtrackpro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    LinearLayout HomeFragmentButton;
    LinearLayout RunFragmentButton;
    LinearLayout StatsFragmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeFragmentButton = findViewById(R.id.home_button);
        RunFragmentButton = findViewById(R.id.plan_button);
        StatsFragmentButton = findViewById(R.id.stat_button);

        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentButton.setBackgroundResource(R.drawable.rounded_background_dark);
                RunFragmentButton.setBackgroundResource(R.drawable.rounded_background);
                StatsFragmentButton.setBackgroundResource(R.drawable.rounded_background);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Home")
                        .commit();
            }
        });

        RunFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentButton.setBackgroundResource(R.drawable.rounded_background);
                RunFragmentButton.setBackgroundResource(R.drawable.rounded_background_dark);
                StatsFragmentButton.setBackgroundResource(R.drawable.rounded_background);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, PlanRunFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Plan a Run")
                        .commit();

            }
        });

        StatsFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentButton.setBackgroundResource(R.drawable.rounded_background);
                RunFragmentButton.setBackgroundResource(R.drawable.rounded_background);
                StatsFragmentButton.setBackgroundResource(R.drawable.rounded_background_dark);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, StatsFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Stats")
                        .commit();

            }
        });

    }
}