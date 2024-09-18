package com.metroappv1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetroLine {
    private static final Map<Integer, MetroLine> allLines = new HashMap<>();
    private static final int timePerStation = 2;  // in minutes
    static List<Station> stations;
    private static byte lines = 0;
    final byte lineNumber;

    public MetroLine(List<Station> stations, byte lineNum) {
        MetroLine.stations = stations;
        this.lineNumber = lineNum;
        ++lines;
        allLines.put((int) lineNumber, this);
    }

    public static MetroLine getLine(int lineNumber) {
        return allLines.get(lineNumber);
    }

    public static short calculateTime(List<String> route) {
        if (route == null) return -1;

        return (short) ((route.size() - 1) * timePerStation);
    }

    public static short calculateTicketPrice(List<String> route) {
        if (route == null) return -1;

        int numberOfStations = route.size() - 1;
        if (route.size() == 1) {
            return 0;
        } else if (numberOfStations <= 9) {
            return 8;
        } else if (numberOfStations <= 16) {
            return 10;
        } else if (numberOfStations <= 23) {
            return 15;
        } else if (numberOfStations <= 39) {
            return 20;
        } else {
            return 25;
        }
    }

    public static Station findStationByName(List<Station> stations, String stationName) {
        for (Station station : stations) {
            if (station.getName().equalsIgnoreCase(stationName))
                return station;
        }
        return null;
    }


    public byte getLineNumber() {
        return lineNumber;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getTimePerStation() {
        return timePerStation;
    }

    public ArrayList<Station> getRoute(Station start, Station destination) {
        short startIndex = (short) stations.indexOf(start);
        short destinationIndex = (short) stations.indexOf(destination);

        if (startIndex == -1 || destinationIndex == -1) {
            return null;
        }

        ArrayList<Station> route;
        if (startIndex <= destinationIndex) {
            route = new ArrayList<>(stations.subList(startIndex, destinationIndex + 1));

        } else {
            route = new ArrayList<>(stations.subList(destinationIndex, startIndex + 1));
            ArrayList<Station> reversedRoute = new ArrayList<>(route);
            Collections.reverse(reversedRoute);
            route = reversedRoute;
        }
        return route;
    }

    public List<Station> getTransitions() {
        ArrayList<Station> res = new ArrayList<>();
        for (Station station : stations) {
            if (station.isTransition())
                res.add(station);
        }
        return res;
    }

    public String toString() {
        return (lineNumber + "");
    }
}
