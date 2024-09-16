package com.deyaa.metroappv1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class OutputActivity extends AppCompatActivity {

    public TextView timeBox, stationsCntBox, priceBox, headerDirection, direction;
    public MetroGraph metroGraph = new MetroGraph();
    short travelTime, ticketPrice;
    ArrayList<ArrayList<String>> Routtes;
    private ViewPager2 viewPager;

    private CardPagerAdapter cardPagerAdapter;
    private ArrayList<String> bestRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_output);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backArrow = findViewById(R.id.iv_back_arrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        timeBox = findViewById(R.id.timeBox);
        direction = findViewById(R.id.whichDirection);
        stationsCntBox = findViewById(R.id.stationsBox);
        priceBox = findViewById(R.id.priceBox);
        headerDirection = findViewById(R.id.headerDirection);
        viewPager = findViewById(R.id.viewPager);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {

            Routtes = (ArrayList<ArrayList<String>>) extras.getSerializable("listOfLists");
            bestRoute = (ArrayList<String>) extras.getSerializable("bestRoute");

            travelTime = extras.getShort("time");

            ticketPrice = extras.getShort("price");

            // dir = extras.getString("direction");
            // Now you can work with listOfLists in your activity
        }


        postOutput();
    }//debug stop her

    @Override
    protected void onResume() {
        super.onResume();
        // Reset ViewPager2 to default state
        resetViewPager();
    }

    private void resetViewPager() {
        if (cardPagerAdapter != null) {
            // Optionally reset the adapter or its data
            cardPagerAdapter.notifyDataSetChanged(); // Notify adapter of data changes

            // Optionally set the default card
            viewPager.setCurrentItem(0, true); // Go to first card without animation
        }
    }

    public void postOutput() {
        if (Routtes == null || Routtes.isEmpty()) {
            Toast.makeText(this, "Couldn't load the data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bestRoute == null || bestRoute.isEmpty()) {
            Toast.makeText(this, "No valid route found.", Toast.LENGTH_SHORT).show();
            return;
        }
        headerDirection.setText(bestRoute.get(0) + " to " + bestRoute.get(bestRoute.size() - 1));
        // Add cards dynamically
        List<CardItem> cardItems = new ArrayList<>();
        String routeTitle = "Metro Route";

        cardItems.add(new CardItem(routeTitle + ' ' + 1, bestRoute));

        for (int i = 1; i < Routtes.size(); i++) {
            cardItems.add(new CardItem(routeTitle + ' ' + (i + 1), Routtes.get(i)));
        }

        // Set up the ViewPager2 with the adapter
        cardPagerAdapter = new CardPagerAdapter(cardItems);
        viewPager.setAdapter(cardPagerAdapter);

        // Set up the initial values for the first route (index 0)
        updateOutputForRoute(0);

        // Add a page change listener to update boxes when swiping between cards
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateOutputForRoute(position);
            }
        });
    }

    private void updateOutputForRoute(int position) {
        // Update time, stations, and price based on the selected route
        ArrayList<String> selectedRoute = Routtes.get(position);

        // Update the TextViews
        timeBox.setText("Time\n" + MetroLine.calculateTime(selectedRoute) + " Mins");
        stationsCntBox.setText("Stations\n" + (selectedRoute.size() - 1));
        priceBox.setText("Price\n" + MetroLine.calculateTicketPrice(selectedRoute) + " LE");
        direction.setText(metroGraph.getDirectionMessage(
                selectedRoute.get(0),                                   // Start station
                selectedRoute.get(selectedRoute.size() - 1),             // End station
                selectedRoute                                            // Full route for detecting transitions
        ));
        if (Routtes.size() > 1) {
            direction.append("\n\nSwipe Left for more routes");
        }
    }


}
