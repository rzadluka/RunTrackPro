package com.cs407.runtrackpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

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
                        .replace(R.id.fragmentContainerView3, HomeFragment.class,null)
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
                        .replace(R.id.fragmentContainerView3,Fragment1.class,null)
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
                        .replace(R.id.fragmentContainerView3,StatsFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Fragment1")
                        .commit();

            }
        });
    }

    public void EnabledMilesButtonByDefault(){

    }
}