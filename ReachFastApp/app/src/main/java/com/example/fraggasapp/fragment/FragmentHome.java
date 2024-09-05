package com.example.fraggasapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fraggasapp.DemoAdapter;
import com.example.fraggasapp.R;
import com.example.fraggasapp.areas;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import mumayank.com.airlocationlibrary.AirLocation;

public class FragmentHome extends Fragment implements AirLocation.Callback {
    private EditText nameText;
    private RecyclerView recyclerView;
    private TextView res;
    private Button addBut, calc;

    private List<areas> items;
    private AirLocation airLocation;
    private DemoAdapter adapter;

    private double latitudeStart = 0, longitudeStart = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new LinkedList<>();
        checkAndResetHistory();
    }

    private void checkAndResetHistory() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GasAppPrefs", getActivity().MODE_PRIVATE);
        long lastResetTime = sharedPreferences.getLong("lastResetTime", 0);
        long currentTime = System.currentTimeMillis();
        long fortyEightHoursInMillis = 48 * 60 * 60 * 1000;

        if (currentTime - lastResetTime > fortyEightHoursInMillis) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("historyList");
            editor.putLong("lastResetTime", currentTime);
            editor.apply();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        res = view.findViewById(R.id.ResText);
        nameText = view.findViewById(R.id.inputArea);
        recyclerView = view.findViewById(R.id.areasRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DemoAdapter(items, this);
        recyclerView.setAdapter(adapter);

        calc = view.findViewById(R.id.CalcBut);
        addBut = view.findViewById(R.id.addBut);

        addBut.setOnClickListener(v -> {
            if (!nameText.getText().toString().isEmpty()) {
                boolean any = items.stream().anyMatch(item -> item.getName().equals(nameText.getText().toString()));
                if (!any) {
                    Geocoder geocoder = new Geocoder(getContext());
                    String address = nameText.getText().toString();
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(address, 1);
                        if (addressList.isEmpty()) {
                            Toast.makeText(getContext(), "Place not found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        areas newArea = new areas();
                        newArea.setName(nameText.getText().toString().trim());
                        newArea.setLatitude(addressList.get(0).getLatitude());
                        newArea.setLongitude(addressList.get(0).getLongitude());
                        items.add(newArea);
                        adapter.notifyItemInserted(items.size() - 1);
                        nameText.setText("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "This area already exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter an area", Toast.LENGTH_SHORT).show();
            }
        });

        loadData();

        if(items.isEmpty()) {
            airLocation = new AirLocation(getActivity(), this, true, 0, "");
            airLocation.start();
        }

        calc.setOnClickListener(this::Calc);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if (items.isEmpty()) {
            airLocation = new AirLocation(getActivity(), this, true, 0, "");
            airLocation.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();  // Save data when the fragment pauses
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GasAppPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("areasList", json);
        if (res != null) {  // Check if res is not null
            editor.putString("resultText", res.getText().toString());
        }
        editor.apply();
    }

    public void updateResultText() {
        if (res != null) {
            res.setText("");
            res.setVisibility(View.INVISIBLE);
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GasAppPrefs", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("areasList", null);
        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<List<areas>>() {}.getType();
            items = gson.fromJson(json, type);
            if (items != null) {
                adapter = new DemoAdapter(items,this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
        String resultText = sharedPreferences.getString("resultText", "");
        if (res != null && items != null && items.size() >= 2) {
            res.setText(resultText);
            res.setVisibility(View.VISIBLE);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000;
    }

    private double calculateRouteDistance(List<areas> route) {
        double totalDistance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += calculateDistance(
                    route.get(i).getLatitude(), route.get(i).getLongitude(),
                    route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()
            );
        }
        return totalDistance;
    }

    private List<areas> findBestRoute(List<areas> areasList) {
        if (areasList.isEmpty()) {
            return new ArrayList<>();
        }
        areas startLocation = areasList.get(0);
        List<areas> locationsToPermute = new ArrayList<>(areasList.subList(1, areasList.size()));
        List<areas> bestRoute = new ArrayList<>();
        double bestDistance = Double.MAX_VALUE;

        List<List<areas>> permutations = generatePermutations(locationsToPermute);

        for (List<areas> perm : permutations) {
            List<areas> route = new ArrayList<>();
            route.add(startLocation);
            route.addAll(perm);
            double distance = calculateRouteDistance(route);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestRoute = new ArrayList<>(route);
            }
        }
        return bestRoute;
    }

    private List<List<areas>> generatePermutations(List<areas> list) {
        List<List<areas>> permutations = new ArrayList<>();
        if (list.size() == 0) {
            permutations.add(new ArrayList<>());
            return permutations;
        }
        areas firstElement = list.remove(0);
        List<List<areas>> remainingPermutations = generatePermutations(list);
        for (List<areas> perm : remainingPermutations) {
            for (int i = 0; i <= perm.size(); i++) {
                List<areas> temp = new ArrayList<>(perm);
                temp.add(i, firstElement);
                permutations.add(temp);
            }
        }
        return permutations;
    }

    public void Calc(View view) {
        if (items.size() < 2) {
            Toast.makeText(getContext(), "There must be at least 2 areas", Toast.LENGTH_SHORT).show();
            return;
        }
        List<areas> bestRoute = findBestRoute(items);
        double bestDistance = calculateRouteDistance(bestRoute);
        double gasCost = (bestDistance / 5)*13.75;

        StringBuilder routeDescription = new StringBuilder();
        routeDescription.append(items.get(0).getName());
        for (int i = 1; i < bestRoute.size(); i++) {
            routeDescription.append(" -> ").append(bestRoute.get(i).getName());
        }
        String resultText = String.format("Best Route: %s\nTotal Distance: %.2f km\nGas Cost: $%.2f",
                routeDescription.toString(), bestDistance, gasCost);

        if (res != null) {
            res.setText(resultText);
            res.setVisibility(View.VISIBLE);
        }

        // Save result to SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GasAppPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Load existing history
        Gson gson = new Gson();
        String json = sharedPreferences.getString("historyList", null);
        List<String> history = new ArrayList<>();
        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<List<String>>() {}.getType();
            history = gson.fromJson(json, type);
        }

        // Add the new result to history
        boolean b = history.stream().anyMatch(x -> Objects.equals(x, resultText));
        if(!b) history.add(resultText);

        // Save updated history
        String updatedJson = gson.toJson(history);
        editor.putString("historyList", updatedJson);
        editor.apply();
    }

    @Override
    public void onSuccess(@NonNull ArrayList<Location> arrayList) {
        latitudeStart = arrayList.get(0).getLatitude();
        longitudeStart = arrayList.get(0).getLongitude();
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitudeStart, longitudeStart, 1);
            areas area = new areas();
            area.setName("Your Location");
            area.setLatitude(latitudeStart);
            area.setLongitude(longitudeStart);
            items.add(area);
            adapter.notifyItemInserted(items.size() - 1);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
        // Handle location failure if needed
    }
}

