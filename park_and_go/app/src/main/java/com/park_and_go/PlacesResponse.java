package com.park_and_go;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesResponse {

    @SerializedName("@graph")
    @Expose
    public final List<Places> graph = null;

    public static class Places {

        public String title = null;
        public final MyLocation location = null;
        public Float distance = null;
    }

    public class MyLocation {
        public final Double latitude = null;
        public final Double longitude = null;
    }
}
