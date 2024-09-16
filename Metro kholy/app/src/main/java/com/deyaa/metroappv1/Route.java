package com.deyaa.metroappv1;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Route implements Parcelable {
    // Parcelable Creator
    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
    public final ArrayList<Station> stationsList;
    public final ArrayList<String> directionsList;

    // Constructor
    public Route(ArrayList<Station> stationsList, ArrayList<String> directionsList) {
        this.stationsList = stationsList;
        this.directionsList = directionsList;
    }

    // Parcelable Constructor
    protected Route(Parcel in) {
        // Read the Station objects from the Parcel
        stationsList = in.createTypedArrayList(Station.CREATOR);

        // Read the directionsList from the Parcel
        directionsList = in.createStringArrayList();
    }

    // Getters
    public ArrayList<Station> getStationsList() {
        return stationsList;
    }

    public ArrayList<String> getDirectionsList() {
        return directionsList;
    }

    @NonNull
    @Override
    public String toString() {
        return "RouteData{" +
                "stationsList=" + stationsList +
                ", directionsList=" + directionsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // Write the Station objects to the Parcel
        dest.writeTypedList(stationsList);

        // Write the directionsList to the Parcel
        dest.writeStringList(directionsList);
    }
}
