package com.example.solinx.API;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    // IMPORTANTE: Cambia esta IP por la de tu PC si usas celular físico
    // Si usas emulador: usa 10.0.2.2
    // Si usas celular físico: usa la IP de tu PC (ejemplo: 192.168.1.75)
    private static final String BASE_URL = "http://192.168.1.167:8080/SoLinX/api/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // Configurar timeout más largo para evitar errores intermitentes
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create()) // Para recibir String
                    .addConverterFactory(GsonConverterFactory.create())    // Para recibir JSON
                    .build();
        }
        return retrofit;
    }
}