package com.park_and_go.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlacesResponse {

    @SerializedName("@graph")
    @Expose
    public final ArrayList<Places> graph = null;

    public static class Places implements Parcelable {

        public String title = null;
        public MyLocation location = null;
        public Float distance = null;
        private String tipo;
        private boolean favorito;

        public Places(String title, MyLocation location, Float distance,String tipo) {
            this.title = title;
            this.location = location;
            this.distance = distance;
            this.tipo=tipo;
        }

        public Places(Parcel in) {
            title = in.readString();
            location = (MyLocation) in.readParcelable(MyLocation.class.getClassLoader());
            distance = in.readFloat();
            tipo=in.readString();
            favorito=in.readBoolean();
        }

        public static void ordenarDistancia(List<Places> listaDesordenada){
            Comparator<Places> compareByDistance = new Comparator<Places>() {
                @Override
                public int compare(Places o1, Places o2) {
                    return o1.distance.compareTo(o2.distance);
                }
            };
            Collections.sort(listaDesordenada, compareByDistance);
        }

        public boolean isFavorito() {
            return favorito;
        }

        public void setFavorito(boolean favorito) {
            this.favorito = favorito;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getTipo() {
            return tipo;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(title);
            parcel.writeParcelable(location, flags);
            parcel.writeFloat(distance);
            parcel.writeString(tipo);
            parcel.writeBoolean(favorito);
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
