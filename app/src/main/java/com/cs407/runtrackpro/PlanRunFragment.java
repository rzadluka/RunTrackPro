package com.cs407.runtrackpro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class PlanRunFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentManager fragmentManager;
    Button milesButton;
    Button DestinationButton;
    Button StartRunButton;
    private String mParam1;
    private String mParam2;

    public PlanRunFragment() {
    }

    public static PlanRunFragment newInstance(String param1, String param2) {
        PlanRunFragment fragment = new PlanRunFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan_run, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        fragmentManager = getParentFragmentManager();

        // start by showing the "Plan By Miles" container as default
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentRunContainerView, PlanByMilesFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("Showing Plan by Miles")
                .commit();

        milesButton = view.findViewById(R.id.Miles);
        milesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show map API and input miles.
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentRunContainerView, PlanByMilesFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Plan by Miles")
                        .commit();
            }
        });

        DestinationButton = view.findViewById(R.id.Destination);
        DestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show map API and start location and end location.
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentRunContainerView, PlanByDestinationFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Plan by Destination")
                        .commit();
            }
        });
    }


}