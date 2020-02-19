package com.park_and_go.common;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorito implements Parcelable {

    private String title;
    private String tipo;
    private Double latitude;
    private Double longitude;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(tipo);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public enum Tipos {
        CONSULADO, OCIO, PARKING;
    }

    public static final Parcelable.Creator<Favorito> CREATOR =
            new Parcelable.Creator<Favorito>() {

                public Favorito createFromParcel(Parcel in) {
                    return new Favorito(in);
                }

                public Favorito[] newArray(int size) {
                    return new Favorito[size];
                }
            };

    public Favorito(Parcel in) {
        title = in.readString();
        tipo = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public Favorito(String title, String tipo, Double latitude, Double longitude) {
        this.title = title;
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
