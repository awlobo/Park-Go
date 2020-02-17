package com.park_and_go.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlacesResponse {

    @SerializedName("@graph")
    @Expose
    public final ArrayList<Places> graph = null;

    public static class Places implements Parcelable {

        public String title = null;
        public MyLocation location = null;
        public Float distance = null;

        public Places(String title, MyLocation location, Float distance) {
            this.title = title;
            this.location = location;
            this.distance = distance;
        }

        public Places(Parcel in) {
            title = in.readString();
            location = (MyLocation) in.readParcelable(MyLocation.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(title);
            parcel.writeParcelable(location, flags);
        }

        public static final Parcelable.Creator<Places> CREATOR =
                new Parcelable.Creator<Places>() {

                    public Places createFromParcel(Parcel in) {
                        return new Places(in);
                    }

                    public Places[] newArray(int size) {
                        return new Places[size];
                    }
                };
    }
}
