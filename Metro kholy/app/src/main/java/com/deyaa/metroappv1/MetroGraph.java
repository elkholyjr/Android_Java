package com.deyaa.metroappv1;

import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetroGraph {
    // Define your metro lines here as final lists
    public static final List<String> firstLine = Arrays.asList("Helwan",
            "Ain Helwan", "Helwan University", "Wadi Hof", "Hadayek Helwan", "El-Maasara", "Tora El-Asmant", "Kozzika", "Tora El-Balad",
            "Sakanat El-Maadi", "Maadi", "Hadayek El-Maadi", "Dar El-Salam", "El-Zahraa'", "Mar Girgis", "El-Malek El-Saleh", "Al-Sayeda Zeinab", "Saad Zaghloul", "Sadat", "Nasser",
            "Orabi", "Al-Shohadaa", "Ghamra", "El-Demerdash", "Manshiet El-Sadr", "Kobri El-Qobba", "Hammamat El-Qobba", "Saray El-Qobba", "Hadayeq El-Zaitoun",
            "Helmeyet El-Zaitoun", "El-Matareyya",
            "Ain Shams", "Ezbet El-Nakhl", "El-Marg", "New El-Marg");
    public static final List<String> secondLine = Arrays.asList("El-Mounib",
            "Sakiat Mekky", "Omm El-Masryeen", "El Giza", "Faisal", "Cairo University",
            "El Bohoth", "Dokki", "Opera", "Sadat", "Mohamed Naguib", "Attaba", "Al-Shohadaa", "Masarra", "Road El-Farag", "St. Teresa", "Khalafawy", "Mezallat",
            "Kolleyyet El-Zeraa", "Shubra El-Kheima");
    public static final List<String> thirdLine = Arrays.asList("Adly Mansour",
            "El Haykestep", "Omar Ibn El-Khattab", "Qobaa",
            "Hesham Barakat", "El-Nozha", "Nadi El-Shams", "Alf Maskan", "Heliopolis Square", "Haroun", "Al-Ahram",
            "Koleyet El-Banat", "Stadium", "Fair Zone", "Abbassia", "Abdou Pasha", "El Geish", "Bab El Shaaria", "Attaba", "Nasser", "Maspero", "Safaa Hegazy", "Kit Kat");
    public static final List<String> Eltafreaa1 = new ArrayList<>(Arrays.asList(
            "Kit Kat", "Sudan Street", "Imbaba", "El-Bohy", "El-Qawmia", "Ring Road", "Rod El-Farag Axis"
    ));
    public static final List<String> Eltafreaa2 = new ArrayList<>(Arrays.asList(
            "Kit Kat", "Tawfikeya", "Wadi El-Nil", "Gamaet El Dowal", "Bulaq El Dakrour", "Cairo University"
    ));
    private final Map<String, List<String>> metroMap;
    public List<List<String>> allRoutes;
    List<String> transationstation = new ArrayList<>(Arrays.asList("Elsadat", "Elshohada", "Jamal Abdulnasser", "El-Ataba", "Cairo university"));

    public MetroGraph() {
        metroMap = new HashMap<>();
        addLine(firstLine);
        addLine(secondLine);
        addLine(thirdLine);
        addLine(Eltafreaa1);
        addLine(Eltafreaa2);
        connectStations("Sadat", "Sadat");
        connectStations("Al-Shohadaa", "Al-Shohadaa");
        connectStations("Nasser", "Nasser");
        connectStations("Attaba", "Attaba");
        connectStations("Cairo university", "Cairo university");
        connectStations("Kit Kat", "Kit Kat");
        connectStations("Kit Kat", "Kit Kat");
    }

    public static List<String> getLineList(String line) {
        switch (line) {
            case "Line 1":
                return firstLine;
            case "Line 2":
                return secondLine;
            case "Line 3":
                return thirdLine;
            case "Eltafreaa 1":
                return Eltafreaa1;
            case "Eltafreaa 2":
                return Eltafreaa2;
            default:
                return new ArrayList<>();
        }
    }

    private void addLine(List<String> line) {
        for (int i = 0; i < line.size() - 1; i++) {
            String station1 = line.get(i);
            String station2 = line.get(i + 1);
            addEdge(station1, station2);
            addEdge(station2, station1);
        }
    }

    private void addEdge(String station1, String station2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            metroMap.putIfAbsent(station1, new ArrayList<>());
        }
        metroMap.get(station1).add(station2);
    }

    private void connectStations(String station1, String station2) {
        addEdge(station1, station2);
        addEdge(station2, station1);
    }

    public List<String> findAllRoutes(String start, String end) {
        List<List<String>> routes = new ArrayList<>();

        // Use DFS to find all possible routes
        findRoutesDFS(start, end, new HashSet<>(), new ArrayList<>(), routes);

        allRoutes = new ArrayList<>(routes);

        // Sort routes by number of stations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            allRoutes.sort(Comparator.comparingInt(List::size));
        }

        // Generate detailed direction messages
        List<String> directionMessages = new ArrayList<>();
        for (List<String> route : allRoutes) {
            directionMessages.add(getDirectionMessage(start, end, route));
        }

        return directionMessages;
    }

    private void findRoutesDFS(String current, String end, Set<String> visited, List<String> path, List<List<String>> routes) {
        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            routes.add(new ArrayList<>(path));
        } else {
            for (String neighbor : metroMap.getOrDefault(current, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    findRoutesDFS(neighbor, end, visited, path, routes);
                }
            }
        }
        path.remove(path.size() - 1);
        visited.remove(current);
    }

    private String getLine(String station, String previousLine, String nextStation) {
        if (station.equals("Cairo University") && Eltafreaa2.contains(nextStation)) {
            return "Eltafreaa 2";
        }
        if (firstLine.contains(station) && secondLine.contains(station)) {
            if (firstLine.contains(nextStation)) {
                return "Line 1";
            } else if (secondLine.contains(nextStation)) {
                return "Line 2";
            }
        }
        if (secondLine.contains(station) && thirdLine.contains(station)) {
            if (secondLine.contains(nextStation)) {
                return "Line 2";
            } else if (thirdLine.contains(nextStation)) {
                return "Line 3";
            }
        }
        if (firstLine.contains(station) && thirdLine.contains(station)) {
            if (firstLine.contains(nextStation)) {
                return "Line 1";
            } else if (thirdLine.contains(nextStation)) {
                return "Line 3";
            }
        }
        if (thirdLine.contains(station) && Eltafreaa1.contains(station)) {
            if (thirdLine.contains(nextStation)) {
                return "Line 3";
            } else if (Eltafreaa1.contains(nextStation)) {
                return "Eltafreaa 1";
            }
        }
        if (thirdLine.contains(station) && Eltafreaa2.contains(station)) {
            if (thirdLine.contains(nextStation)) {
                return "Line 3";
            } else if (Eltafreaa2.contains(nextStation)) {
                return "Eltafreaa 2";
            }
        }
        if (firstLine.contains(station)) return "Line 1";
        if (secondLine.contains(station)) return "Line 2";
        if (thirdLine.contains(station)) return "Line 3";
        if (Eltafreaa1.contains(station)) return "Eltafreaa 1";
        if (Eltafreaa2.contains(station)) return "Eltafreaa 2";
        return "Unknown Line";
    }


    public String getDirectionMessage(String start, String end, List<String> route) {
        StringBuilder directionMessage = new StringBuilder();
        directionMessage.append("Start at ").append(start).append(".\n");
        String previousLine = getLine(route.get(0), "", route.get(1));
        String previousDirection = getDirection(previousLine, route.get(0), route.get(1));
        directionMessage.append("Take ").append(previousLine).append(" ").append(previousDirection).append(".\n");
        for (int i = 1; i < route.size(); i++) {
            String currentStation = route.get(i);
            String nextStation = (i + 1 < route.size()) ? route.get(i + 1) : end;
            String currentLine = getLine(currentStation, previousLine, nextStation);
            if (!currentLine.equals(previousLine) && !currentStation.equals(end)) {
                directionMessage.append("Change to ").append(currentLine)
                        .append(" at ").append(route.get(i)).append(".\n");
                String currentDirection = getDirection(currentLine, currentStation, nextStation);
                directionMessage.append("Take ").append(currentLine).append(" ").append(currentDirection).append(".\n");
                previousLine = currentLine;
            }
        }
        directionMessage.append("Arrive at ").append(end).append(".");
        return directionMessage.toString();
    }


    private List<String> getAllLines(String station) {
        List<String> lines = new ArrayList<>();
        if (firstLine.contains(station)) lines.add("Line 1");
        if (secondLine.contains(station)) lines.add("Line 2");
        if (thirdLine.contains(station)) lines.add("Line 3");
        if (Eltafreaa1.contains(station)) lines.add("Eltafreaa 1");
        if (Eltafreaa2.contains(station)) lines.add("Eltafreaa 2");
        return lines;
    }

    private String getDirection(String line, String startStation, String endStation) {
        List<String> lineList = getLineList(line);
        if (lineList.isEmpty()) return "";
        int startIndex = lineList.indexOf(startStation);
        int endIndex = lineList.indexOf(endStation);
        if (startIndex == -1 || endIndex == -1) return "";
        if (line.equals("Line 1")) {
            if (endIndex > startIndex) {
                return "towards New El-Marg";
            } else if (endIndex < startIndex) {
                return "towards Helwan";
            }
        } else if (line.equals("Line 2")) {
            if (endIndex > startIndex) {
                return "towards Shubra El-Kheima";
            } else if (endIndex < startIndex) {
                return "towards Elmounib";
            }
        } else if (line.equals("Line 3")) {
            if (endIndex > startIndex) {
                return "towards Kit Kat";
            } else if (endIndex < startIndex) {
                return "towards Adly Mansour\"";
            }
        } else if (line.equals("Eltafreaa 1")) {
            if (endIndex > startIndex) {
                return "towards Kit Kat";
            } else if (endIndex < startIndex) {
                return "towards Rod El-Farag Axis";
            }
        } else if (line.equals("Eltafreaa 2")) {
            if (endIndex > startIndex) {
                return "towards Cairo University";
            } else if (endIndex < startIndex) {
                return "towards Kit Kat";
            }
        }
        return "";
    }
}