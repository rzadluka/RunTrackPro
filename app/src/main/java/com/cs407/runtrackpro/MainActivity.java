package com.cs407.runtrackpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    Button HomeFragmentButton;
    Button RunFragmentButton;
    Button StatsFragmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        HomeFragmentButton = findViewById(R.id.home_button);
        HomeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, HomeFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Home")
                        .commit();
            }
        });

        RunFragmentButton = findViewById(R.id.plan_button);
        RunFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, PlanRunFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Plan Run Fragment")
                        .commit();
            }
        });

        StatsFragmentButton = findViewById(R.id.stat_button);
        StatsFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainContainerView, StatsFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Stats Fragment")
                        .commit();
            }
        });

    }
}