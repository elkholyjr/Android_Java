package com.deyaa.metroappv1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.WaveDetector;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import mumayank.com.airlocationlibrary.AirLocation;


public class HomeFragment extends Fragment implements AirLocation.Callback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    AutoCompleteTextView startStationSpinner, endStationSpinner;
    EditText destinationAddress;
    Button startBtn, nearBut;
    MaterialButton buttonSwitch;
    ConnectivityManager connectivityManager;
    ImageView mapBut1, mapBut2, nearestLock;
    //    TextView outputTextView;
    MetroLine line1, line2, line3;
    Station startStation, endStation, nearest;
    AirLocation airLocation;
    List<Station> allStations;
    WaveDetector.WaveListener waveGestureListener;
    Vibrator vibrator;
    String bestRouteDirections;
    ArrayList<Route> flattenedAllRoutes;
    List<List<String>> allRoutes;
    ArrayList<String> bestRoute;
    ArrayAdapter<Station> startStationsAdapter;
    short travelTime, ticketPrice, count;
    long[] vibrationPattern = {0, 300, 200, 300};

    double longitudeStart = 0, latitudeStart = 0, latitudeOth, longitudeOth, KM;
    Location start, end;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onPause() {
        saveStationSelections();
        Sensey.getInstance().stopWaveDetection(waveGestureListener);
        Sensey.getInstance().stop();
        vibrator = null;
        airLocation = null;
        super.onPause();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapBut1 = view.findViewById(R.id.mapBut1);
        mapBut1.setOnClickListener((v) -> mapButton(v, startStation));
        mapBut2 = view.findViewById(R.id.mapBut2);
        mapBut2.setOnClickListener((v) -> mapButton(v, endStation));
        connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        startStationSpinner = view.findViewById(R.id.startStationSpinner);
        nearestLock = view.findViewById(R.id.nearest_lock);
        nearestLock.setOnClickListener((v) -> nearby(v));
        endStationSpinner = view.findViewById(R.id.endStationSpinner);
        buttonSwitch = view.findViewById(R.id.buttonSwitch);
        startBtn = view.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this::start);
        destinationAddress = view.findViewById(R.id.destinationAddress);
        nearBut = view.findViewById(R.id.nearBut);
        vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);

        List<Station> line1Stations = new ArrayList<>(Arrays.asList(
                new Station("Helwan", 1, 29.848971477600465, 31.3342277402767),
                new Station("Ain Helwan", 1, 29.862618672434877, 31.324868625198697),
                new Station("Helwan University", 1, 29.869449601926597, 31.320046759925),
                new Station("Wadi Hof", 1, 29.87907177726837, 31.31359877100767),
                new Station("Hadayek Helwan", 1, 29.8971343176686, 31.303949916769852),
                new Station("El-Maasara", 1, 29.90607180460953, 31.299546485341345),
                new Station("Tora El-Asmant", 1, 29.925952436139358, 31.28751737050467),
                new Station("Kozzika", 1, 29.936255483265562, 31.281746149662656),
                new Station("Tora El-Balad", 1, 329.946753482470786, 31.27297339529574),
                new Station("Sakanat El-Maadi", 1, 29.953295057267717, 31.262930306684932),
                new Station("Maadi", 1, 29.96030423051439, 31.25764948315825),
                new Station("Hadayek El-Maadi", 1, 29.970122212488985, 31.250598693114473),
                new Station("Dar El-Salam", 1, 29.982068330083234, 31.242137136641734),
                new Station("El-Zahraa'", 1, 29.995472526446658, 31.23118153562492),
                new Station("Mar Girgis", 1, 30.006101473601202, 31.229614844174662),
                new Station("El-Malek El-Saleh", 1, 30.017710810042463, 31.23120505463815),
                new Station("Al-Sayeda Zeinab", 1, 30.02927144981774, 31.235431896201714),
                new Station("Saad Zaghloul", 1, 30.037029775306358, 31.238362345996254),
                new Station("Sadat", 1, 2, 30.044131546661667, 31.23442101982754), // transition with line#2
                new Station("Orabi", 1, 30.05668848060458, 31.242045447595284),
                new Station("Al-Shohadaa", 1, 2, 30.061072829206587, 31.24605272665066), // transition with line#2
                new Station("Ghamra", 1, 30.069023346371683, 31.26461830891386),
                new Station("El-Demerdash", 1, 30.077330022991504, 31.27778779585812),
                new Station("Manshiet El-Sadr", 1, 30.081992026306036, 31.287537619402794),
                new Station("Kobri El-Qobba", 1, 30.08723160261677, 31.29411335237051),
                new Station("Hammamat El-Qobba", 1, 30.09124391016904, 31.298899586798832),
                new Station("Saray El-Qobba", 1, 30.097687390297263, 31.304588155210944),
                new Station("Hadayeq El-Zaitoun", 1, 30.10589892269087, 31.31047449059628),
                new Station("Helmeyet El-Zaitoun", 1, 30.11332907091743, 31.313954979683),
                new Station("El-Matareyya", 1, 30.12137639637615, 31.31372848534359),
                new Station("Ain Shams", 1, 30.131021186452816, 31.319080193578966),
                new Station("Ezbet El-Nakhl", 1, 30.13929018873895, 31.324357231785047),
                new Station("El-Marg", 1, 30.152100785277824, 31.335626552512593),
                new Station("New El-Marg", 1, 30.163631522845037, 31.338291653561676)
        ));

        List<Station> line2Stations = new ArrayList<>(Arrays.asList(
                new Station("El-Mounib", 2, 29.981651991446835, 31.212367615420153),
                new Station("Sakiat Mekky", 2, 29.995553588294797, 31.20854814991042),
                new Station("Omm El-Masryeen", 2, 30.006197101467905, 31.208295299388546),
                new Station("El Giza", 2, 30.01060334066647, 31.20681571771362),
                new Station("Faisal", 2, 30.017305743782682, 31.203790909502985),
                new Station("Cairo University", 2, 3, 30.02589832890943, 31.20108435766103), // transition with line#3
                new Station("El Bohoth", 2, 30.03578346864703, 31.20015967637783),
                new Station("Dokki", 2, 30.038435227514427, 31.2122256011576),
                new Station("Opera", 2, 30.041898137974737, 31.225082621984463),
                new Station("Mohamed Naguib", 2, 30.04535402428948, 31.244204262060276),
                new Station("Masarra", 2, 30.070876977882186, 31.245128050666978),
                new Station("Road El-Farag", 2, 30.080581951605392, 31.245372311976265),
                new Station("St. Teresa", 2, 30.087964202326077, 31.245465729806423),
                new Station("Khalafawy", 2, 30.097884814100773, 31.245399165907173),
                new Station("Mezallat", 2, 30.104189603256174, 31.245632631338168),
                new Station("Kolleyyet El-Zeraa", 2, 30.113690224264595, 31.248672945111473),
                new Station("Shubra El-Kheima", 2, 30.122483587915514, 31.244492820715912)
        ));

        List<Station> line3Stations = new ArrayList<>(Arrays.asList(
                new Station("Adly Mansour", 3, 30.146372169598383, 31.421179192021853),
                new Station("El Haykestep", 3, 30.14396823599606, 31.404575148268314),
                new Station("Omar Ibn El-Khattab", 3, 30.140368331060564, 31.39423255008058),
                new Station("Qobaa", 3, 30.13488508894196, 31.3837526712754),
                new Station("Hesham Barakat", 3, 30.130846414814734, 31.372945960707547),
                new Station("El-Nozha", 3, 30.127985836162438, 31.360164593827175),
                new Station("Nadi El-Shams", 3, 30.125465090698878, 31.348926192773863),
                new Station("Alf Maskan", 3, 30.11904607768164, 31.340186684856647),
                new Station("Heliopolis Square", 3, 30.108413730947746, 31.338283579136842),
                new Station("Haroun", 3, 30.10137789648754, 31.332955060261323),
                new Station("Al-Ahram", 3, 30.09172558994502, 31.326316943362475),
                new Station("Koleyet El-Banat", 3, 30.084042044569536, 31.32901707758358),
                new Station("Stadium", 3, 30.072910146730123, 31.31710969348421),
                new Station("Fair Zone", 3, 30.07325199788208, 31.300975368505853),
                new Station("Abbassia", 3, 30.071998861398757, 31.283380104107223),
                new Station("Abdou Pasha", 3, 30.064777973499975, 31.27474734146265),
                new Station("El Geish", 3, 30.061747856780492, 31.2668791125394),
                new Station("Bab El Shaaria", 3, 30.054137129565298, 31.255864511746665),
                new Station("Attaba", 3, 2, 30.052355576410562, 31.246806676910506),  // transition with line#2
                new Station("Nasser", 3, 1, 30.053504320891506, 31.238740868500635),  // transition with line#1
                new Station("Maspero", 3, 30.05572421422552, 31.23212075406194),
                new Station("Safaa Hegazy", 3, 30.062279450124358, 31.22327078532686),
                new Station("Kit Kat", 3, 30.066545761923596, 31.213009066175886),
                new Station("Sudan Street", 3, 30.070074056745625, 31.204732696169618),
                new Station("Imbaba", 3, 30.075831976206135, 31.207470979952763),
                new Station("El-Bohy", 3, 30.082143303494416, 31.210540528048867),
                new Station("El-Qawmia", 3, 30.093224176778676, 31.209006972251853),
                new Station("Ring Road", 3, 30.096414600498882, 31.199582274517578),
                new Station("Rod El-Farag Axis", 3, 30.101903588553455, 31.184419627490403),
                new Station("Tawfikeya", 3, 30.06701323291686, 31.203091023911238),
                new Station("Wadi El-Nil", 3, 30.059019659816197, 31.201299956190958),
                new Station("Gamaet El Dowal", 3, 30.050365021423257, 31.19900970184991),
                new Station("Bulaq El Dakrour", 3, 30.03772155052614, 31.195535497564585),
                new Station("Cairo University", 3, 2, 30.025989557786453, 31.201194920711227)
        ));

        line1 = new MetroLine(line1Stations, (byte) 1);
        line2 = new MetroLine(line2Stations, (byte) 2);
        line3 = new MetroLine(line3Stations, (byte) 3);

        allStations = new ArrayList<>();

        allStations.addAll(line1Stations);
        allStations.addAll(line2Stations);
        allStations.addAll(line3Stations);

        startStationsAdapter = new ArrayAdapter<>(requireActivity(), R.layout.dorpdown_list, allStations);
        ArrayAdapter<Station> endStationsAdapter = new ArrayAdapter<>(requireActivity(), R.layout.dorpdown_list, allStations);
        startStationSpinner.setAdapter(startStationsAdapter);
        endStationSpinner.setAdapter(endStationsAdapter);
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        startStationSpinner.setOnItemClickListener((parent, viewClicked, position, id) -> {
            startStation = (Station) parent.getItemAtPosition(position);
            imm.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
            saveStationSelections();
        });
        startStationSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleNewLineInput(s, startStationSpinner);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        startStationSpinner.setText(allStations.get(0).toString());
        startStation = allStations.get(0);
        endStationSpinner.setOnItemClickListener((parent, viewClicked, position, id) -> {
            endStation = (Station) parent.getItemAtPosition(position);
            imm.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
            saveStationSelections();
        });
        endStationSpinner.setText(allStations.get(0).toString());
        endStation = allStations.get(0);
        endStationSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleNewLineInput(s, endStationSpinner);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        destinationAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleNewLineInput(s, destinationAddress);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadStationSelections();
        loadResult();
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            airLocation = new AirLocation(getActivity(), this, true, 0, "");
            airLocation.start();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        Sensey.getInstance().init(requireActivity());
        waveGestureListener = () -> {
            startStationSpinner.setText("", true);
            endStationSpinner.setText("", true);
            startStation = null;
            endStation = null;
            saveStationSelections();
            saveResult("");
            destinationAddress.setText("");
            EditText[] texts = {startStationSpinner, endStationSpinner, destinationAddress};
            for (EditText text : texts) {
                YoYo.with(Techniques.Shake).duration(500).repeat(0).playOn(text);
            }
        };
        Sensey.getInstance().startWaveDetection(waveGestureListener);
        nearBut.setOnClickListener(this::nearBut);
        buttonSwitch.setOnClickListener(this::Switch);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (airLocation == null) {
                airLocation = new AirLocation(getActivity(), this, true, 0, "");
            }
            airLocation.start();
        }
    }

    public <T extends EditText> void handleNewLineInput(CharSequence s, T v) {
        String converted = s.toString();
        if (converted.contains("\n")) {
            converted = converted.replace("\n", "");
            v.setText(converted);
            v.setSelection(v.length());
        }
    }

    public void nearby(View v) {
        if (!isConnected()) {
            Toast.makeText(requireActivity(), "Internet connection error", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitudeStart == 0 || longitudeStart == 0) {
            Toast.makeText(requireActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
            return;
        }
        airLocation.start();
        Location start = new Location("");
        start.setLatitude(latitudeStart);
        start.setLongitude(longitudeStart);
        Location end = new Location("");
        Station nearest = null;
        float minDistance = Float.MAX_VALUE;
        for (Station station : allStations) {
            end.setLatitude(station.getLatitude());
            end.setLongitude(station.getLongitude());
            float distance = start.distanceTo(end);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = station;
            }
        }

        if (nearest != null) {
            startStationSpinner.setText(nearest.toString(), true);
            startStation = nearest;
            saveStationSelections();
            startStationsAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(requireActivity(), "No nearby stations found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveStationSelections() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MetroAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (startStation == null || endStation == null) {
            editor.putString("startStation", null);
            editor.putString("endStation", null);
        } else {
            editor.putString("startStation", startStation.getName());
            editor.putString("endStation", endStation.getName());
        }
        if (destinationAddress.getText() != null && !destinationAddress.getText().toString().isEmpty()) {
            editor.putString("nearStation", destinationAddress.getText().toString());
        } else {
            editor.putString("nearStation", null);
        }
        editor.apply();
    }

    private void loadStationSelections() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MetroAppPrefs", Context.MODE_PRIVATE);
        String savedStartStation = sharedPreferences.getString("startStation", null);
        String savedEndStation = sharedPreferences.getString("endStation", null);
        String near = sharedPreferences.getString("nearStation", null);

        if (savedStartStation != null && savedEndStation != null) {
            startStation = findStationByName(savedStartStation);
            endStation = findStationByName(savedEndStation);

            if (startStation != null) {
                startStationSpinner.setText(startStation.toString(), true);
            } else {
                startStationSpinner.setText("");
                startStation = null;
            }
            if (endStation != null) {
                endStationSpinner.setText(endStation.toString(), true);
            } else {
                endStationSpinner.setText("");
                endStation = null;
            }
        } else {
            startStation = null;
            endStation = null;
            startStationSpinner.setText(null, true);
            endStationSpinner.setText(null, true);
        }
        if (near != null) {
            destinationAddress.setText(near);
        } else {
            destinationAddress.setText("");
        }
    }

    private Station findStationByName(String stationName) {
        for (int i = 0; i < startStationSpinner.getAdapter().getCount(); i++) {
            Station station = (Station) startStationSpinner.getAdapter().getItem(i);
            if (station.getName().equals(stationName)) {
                return station;
            }
        }
        return null;
    }

    private void saveResult(String result) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MetroAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("routeResult", result);
        editor.apply();
    }

    private void loadResult() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MetroAppPrefs", Context.MODE_PRIVATE);
        String savedResult = sharedPreferences.getString("routeResult", null);

        if (savedResult != null) {
        }
    }

    public void mapButton(View v, Station s) {
        if (!isConnected()) {
            Toast.makeText(requireActivity(), "Internet connection error", Toast.LENGTH_SHORT).show();
            return;
        }
        Geocoder geocoder = new Geocoder(requireActivity());
        if (s == null) {
            Toast.makeText(requireActivity(), "Please select a station", Toast.LENGTH_SHORT).show();
            return;
        }
        String address = s.getName();
        try {
            List<Address> addressList = geocoder.getFromLocationName("Egypt Cairo Metro " + address, 1);
            if (addressList == null || addressList.isEmpty()) {
                Toast.makeText(getActivity(), "Place couldn't be found", Toast.LENGTH_SHORT).show();
                return;
            }
            latitudeStart = addressList.get(0).getLatitude();
            longitudeStart = addressList.get(0).getLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitudeStart + "," + longitudeStart));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void calc() {
        if (startStation == null || endStation == null) {
            if (startStation == null) {
                YoYo.with(Techniques.Shake).duration(500).repeat(1).playOn(startStationSpinner);
            }
            if (endStation == null) {
                YoYo.with(Techniques.Shake).duration(500).repeat(1).playOn(endStationSpinner);
            }
            return;
        }

        if (startStation.getName().equalsIgnoreCase(endStation.getName())) {
            Toast.makeText(getActivity(), "You are already at the destination station: " + startStation.getName(), Toast.LENGTH_SHORT).show();
            if (startStation.getLine() != endStation.getLine())
                Toast.makeText(getActivity(), "You can take the stairs", Toast.LENGTH_SHORT).show();
            return;
        }

        MetroGraph metroGraph = new MetroGraph();
        metroGraph.findAllRoutes(startStation.getName(), endStation.getName());

        allRoutes = metroGraph.allRoutes;
        if (allRoutes.isEmpty()) {
            Toast.makeText(getActivity(), "No valid route found.", Toast.LENGTH_SHORT).show();
            return;
        }
// route in graph sorted we don't need to used find best route take first route from graph
        // bestRoute = (ArrayList<String>) findBestRoute(allRoutes);
        bestRoute = (ArrayList<String>) allRoutes.get(0);
        count = (short) (bestRoute.size() - 1);

// Calculate travel time and ticket price
        travelTime = MetroLine.calculateTime(bestRoute);
        ticketPrice = MetroLine.calculateTicketPrice(bestRoute);
    }

    public void start(View view) {
        calc();
        if (allRoutes == null || allRoutes.isEmpty()) {
            return;
        }
        Intent intent = new Intent(requireActivity(), OutputActivity.class);
        ArrayList<ArrayList<String>> listOfLists = new ArrayList<>();
        for (List<String> subList : allRoutes) {
            listOfLists.add(new ArrayList<>(subList));
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("listOfLists", listOfLists);
        bundle.putSerializable("bestRoute", bestRoute);
        bundle.putShort("time", travelTime);
        bundle.putShort("price", ticketPrice);
        intent.putExtras(bundle);
        startActivity(intent);

    }


    public void Switch(View view) {
        if (!startStationSpinner.getText().toString().isEmpty() && !endStationSpinner.getText().toString().isEmpty()) {
            YoYo.with(Techniques.RotateInUpRight)
                    .duration(250)
                    .repeat(0)
                    .playOn(buttonSwitch);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
//            DEBUGGING
                Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show();
            }
            Station tempStation = startStation;
            startStation = endStation;
            endStation = tempStation;
            startStationSpinner.setText(startStation.toString(), false);
            endStationSpinner.setText(endStation.toString(), false);
            YoYo.with(Techniques.Shake).duration(50).repeat(0).playOn(startStationSpinner);
            YoYo.with(Techniques.Shake).duration(50).repeat(0).playOn(endStationSpinner);
        }
    }

    public boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return ((activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE));
    }

    public void nearBut(View view) {
        if (destinationAddress.getText() != null && !destinationAddress.getText().toString().isEmpty()) {
            Geocoder geocoder = new Geocoder(requireActivity());
            String address = destinationAddress.getText().toString();
            try {
                if (!isConnected()) {
                    Toast.makeText(requireActivity(), "Internet connection error", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                if (addressList == null || addressList.isEmpty()) {
                    Toast.makeText(requireActivity(), "Place couldn't be found", Toast.LENGTH_SHORT).show();
                    return;
                }
                latitudeOth = addressList.get(0).getLatitude();
                longitudeOth = addressList.get(0).getLongitude();
                start = new Location("");
                end = new Location("");
                start.setLatitude(latitudeOth);
                start.setLongitude(longitudeOth);
                KM = Integer.MAX_VALUE;
                nearest = null;
                for (Station x : allStations) {
                    end.setLatitude(x.getLatitude());
                    end.setLongitude(x.getLongitude());
                    if (KM > start.distanceTo(end)) {
                        KM = start.distanceTo(end);
                        nearest = x;
                    }
                }
                if (nearest != null) {
                    endStationSpinner.setText(nearest.toString(), true);
                    endStation = nearest;
                }

            } catch (IOException e) {
                Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSuccess(@NonNull ArrayList<Location> arrayList) {
        if (startStationSpinner.getText().toString().isEmpty()) {
            if (!isConnected()) {
                Toast.makeText(requireActivity(), "Internet connection error", Toast.LENGTH_SHORT).show();
                return;
            }
            latitudeStart = arrayList.get(0).getLatitude();
            longitudeStart = arrayList.get(0).getLongitude();
            updateLoc(latitudeStart, longitudeStart);
        }
    }

    @Override
    public void onFailure(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
        if (!isConnected()) {
            Toast.makeText(requireActivity(), "Internet connection error", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(requireActivity(), "Failed to get your current location", Toast.LENGTH_SHORT).show();
    }

    public void updateLoc(double lat, double lon) {
        start = new Location("");
        end = new Location("");
        start.setLatitude(latitudeStart);
        start.setLongitude(longitudeStart);
        KM = Integer.MAX_VALUE;
        nearest = null;
        for (Station x : allStations) {
            end.setLatitude(x.getLatitude());
            end.setLongitude(x.getLongitude());
            if (KM > start.distanceTo(end)) {
                KM = start.distanceTo(end);
                nearest = x;
            }
        }
        if (nearest != null) {
            startStationSpinner.setText(nearest.toString(), true);
            startStation = nearest;
            startStationsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start AirLocation
                airLocation = new AirLocation(getActivity(), this, true, 0, "");
                airLocation.start();
            } else {
                Toast.makeText(requireActivity(), "Permission denied, cannot get location", Toast.LENGTH_SHORT).show();
            }
        }
    }


}