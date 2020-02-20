package com.park_and_go.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.park_and_go.assets.Constants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.park_and_go.activities.FavoritosPlaces.mFavs;


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

    public static void writeFav(String file, PlacesResponse.Places p, String tipo) {
        try {
            Writer writer = new FileWriter(file);
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            Favorito f = new Favorito(p.title, tipo, p.location.latitude, p.location.longitude);
            mFavs.add(f);
            gson.toJson(mFavs, writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFav(String file) {
        try {
            Reader reader = new FileReader(file);
            Gson gson = new Gson();
            Type types = new TypeToken<ArrayList<Favorito>>() {
            }.getType();
            mFavs = gson.fromJson(reader, types);
            reader.close();

            boolean encontrado = false;
            for (Favorito fav : mFavs) {
                if (fav.getTitle().equals(Constants.TODOS)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado && mFavs.size() != 0) {
                mFavs.add(0, new Favorito(Constants.TODOS, Constants.TODOS, 0.0, 0.0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
