package com.park_and_go.common;

import android.os.Parcel;
import android.os.Parcelable;

public class MyLocation implements Parcelable {
    public Double latitude = null;
    public Double longitude = null;


    public MyLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<MyLocation> CREATOR =
            new Parcelable.Creator<MyLocation>() {

                public MyLocation createFromParcel(Parcel in) {
                    return new MyLocation(in);
                }

                public MyLocation[] newArray(int size) {
                    return new MyLocation[size];
                }
            };
}
