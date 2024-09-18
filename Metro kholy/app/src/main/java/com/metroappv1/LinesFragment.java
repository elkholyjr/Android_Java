package com.metroappv1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class LinesFragment extends Fragment {

    ImageView iconOverlay;
    Spinner linesSpinner;
    ArrayList<String> currStations;
    StationAdapter adapter;

    public LinesFragment() {
        super(R.layout.fragment_lines);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iconOverlay = view.findViewById(R.id.iconOverlay);
        iconOverlay.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
            intent.putExtra("imageResId", R.drawable.metro_lines3); // Pass the image resource ID
            startActivity(intent);
        });
        ListView stationListView = view.findViewById(R.id.stationListView);
        currStations = new ArrayList<>();
        linesSpinner = view.findViewById(R.id.linesSpinner);
        linesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currStations.clear();
                currStations.addAll(new ArrayList<>(MetroGraph.getLineList("Line " + (position + 1))));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        adapter = new StationAdapter(getActivity(), currStations);
        stationListView.setAdapter(adapter);
    }


}
