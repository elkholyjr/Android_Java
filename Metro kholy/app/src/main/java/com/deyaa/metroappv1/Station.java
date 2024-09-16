package com.deyaa.metroappv1;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Station implements Parcelable {
    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
    private final String name;
    private final int line, sharedWithLineNum;
    private final double latitude, longitude;

    public Station(String name, int line) {
        this(name, line, 0, 0.0, 0.0);
    }

    public Station(String name, int line, int sharedWith) {
        this(name, line, sharedWith, 0.0, 0.0);
    }

    public Station(String name, int line, double latitude, double longitude) {
        this(name, line, 0, latitude, longitude);
    }

    public Station(String name, int line, int sharedWith, double latitude, double longitude) {
        this.name = name;
        this.line = line;
        this.sharedWithLineNum = sharedWith;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Station(Parcel in) {
        name = in.readString();
        line = in.readInt();
        sharedWithLineNum = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public String getName() {
        return name;
    }

    public MetroLine getLine() {
        return MetroLine.getLine(line);
    }


    public int getLineNumber() {
        return line;
    }

    public boolean isTransition() {
        return getSharedWithLineNum() > 0;
    }

    public int getSharedWithLineNum() {
        return sharedWithLineNum;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        String baseInfo = name;
//        if (isTransition()) {
//            baseInfo += ", Transition to Line " + sharedWithLineNum;
//        }
//        baseInfo += ")";
        return baseInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(line);
        dest.writeInt(sharedWithLineNum);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
