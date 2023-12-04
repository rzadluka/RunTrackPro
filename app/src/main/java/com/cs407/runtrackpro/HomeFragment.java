package com.cs407.runtrackpro;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static ArrayList<Stats> stats1 = new ArrayList<>();
    private String mParam1;
    private String mParam2;
    private final ArrayList<String> displayStats = new ArrayList<>();

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase("stats",
                Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        stats1 = dbHelper.readStats();
        if (stats1.isEmpty()) {
            displayStats.add("No data to display, Start your first run to see stats here!");
        }else {
            displayStats.clear();
            for (Stats stats : stats1) {
                displayStats.add(String.format(
                        "Date: %s%nTime: %s%nDistance: %s%n",
                        stats.getDate(),
                        stats.getTime(),
                        stats.getDistance()
                ));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, displayStats);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int statId = position;
                Intent intent = new Intent(getContext(), StatsDetailsActivity.class);
                intent.putExtra("statId", statId);
                startActivity(intent);
            }
        });

        return view;
    }

}