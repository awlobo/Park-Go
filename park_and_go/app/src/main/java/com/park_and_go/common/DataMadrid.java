package com.park_and_go.common;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface DataMadrid {
    @Headers("Accept: application/geo+json")
    @GET("/egob/catalogo/202625-0-aparcamientos-publicos.json")
    Call<PlacesResponse> getPlaces(@Query("latitud") double latitude,
                                   @Query("longitud") double longitude,
                                   @Query("distancia") int distance);

    @Headers("Accept: application/geo+json")
    @GET("/egob/catalogo/201000-0-embajadas-consulados.json")
    Call<PlacesResponse> getConsulates(@Query("latitud") double latitude,
                                       @Query("longitud") double longitude,
                                       @Query("distancia") int distance);

    @Headers("Accept: application/geo+json")
    @GET("/egob/catalogo/208862-7650046-ocio_salas.json")
    Call<PlacesResponse> getTheatres(@Query("latitud") double latitude,
                                     @Query("longitud") double longitude,
                                     @Query("distancia") int distance);
}
