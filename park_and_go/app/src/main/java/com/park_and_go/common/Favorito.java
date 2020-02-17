package com.park_and_go.common;

public class Favorito {

    private String title;
    private Tipos tipo;
    private Double latitude;
    private Double longitude;
    public enum Tipos{
        CONSULADO, OCIO, PARKING;
    }

    public Favorito() {
    }

    public Favorito(String title, Tipos tipo, Double latitude, Double longitude) {
        this.title = title;
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
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
