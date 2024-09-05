package com.example.fraggasapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fraggasapp.HistoryAdapter;
import com.example.fraggasapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class FragmentSettings extends Fragment {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private List<String> historyList;

    void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GasAppPrefs", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("historyList", null);
        historyList = new ArrayList<>();
        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<List<String>>() {}.getType();
            historyList = gson.fromJson(json, type);
        }
        historyAdapter = new HistoryAdapter(historyList);
        historyRecyclerView.setAdapter(historyAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        checkAndResetHistory();
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void checkAndResetHistory() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GasAppPrefs", getActivity().MODE_PRIVATE);
        long lastResetTime = sharedPreferences.getLong("lastResetTime", 0);
        long currentTime = System.currentTimeMillis();
        long fortyEightHoursInMillis = 48 * 60 * 60 * 1000; // 48 hours in milliseconds

        if (currentTime - lastResetTime > fortyEightHoursInMillis) {
            // Clear the history if 48 hours have passed
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("historyList");
            editor.putLong("lastResetTime", currentTime);
            editor.apply();
        }
    }

}
