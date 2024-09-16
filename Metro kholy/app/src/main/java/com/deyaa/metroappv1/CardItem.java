package com.deyaa.metroappv1;

import java.util.List;

public class CardItem {
    private final String routeTitle;
    private final List<String> stations; // List of station names

    public CardItem(String routeTitle, List<String> stations) {
        this.routeTitle = routeTitle;
        this.stations = stations;
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public List<String> getStations() {
        return stations;
    }
}
