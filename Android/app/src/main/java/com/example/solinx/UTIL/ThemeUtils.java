package com.example.solinx.UTIL;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemeUtils {

    private static final String TAG = "ThemeUtils";
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "current_theme";

    /**
     * Aplica el tema guardado localmente. Se llama al inicio de cada Activity.
     */
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int mode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Cambia a modo oscuro localmente Y sincroniza con el backend.
     */
    public static void setDarkMode(Context context) {
        saveTheme(context, AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        sincronizarTemaConBackend(context, "oscuro");
    }

    /**
     * Cambia a modo claro localmente Y sincroniza con el backend.
     */
    public static void setLightMode(Context context) {
        saveTheme(context, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sincronizarTemaConBackend(context, "claro");
    }

    /**
     * Fuerza modo claro SOLO localmente, sin tocar el backend.
     * Usado al cerrar sesión para que la app siempre se quede en modo claro.
     */
    public static void forceLightModeLocal(Context context) {
        saveTheme(context, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * Aplica un tema leído de la BD (string "claro" u "oscuro") localmente, sin re-sincronizar.
     * Usado al iniciar sesión para aplicar el tema que el usuario tenía guardado.
     */
    public static void applyThemeFromBackend(Context context, String tema) {
        if (tema == null) tema = "claro";
        int mode = tema.equalsIgnoreCase("oscuro")
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;
        saveTheme(context, mode);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private static void saveTheme(Context context, int mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, mode).apply();
    }

    public static int getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static boolean isDarkMode(Context context) {
        return getSavedTheme(context) == AppCompatDelegate.MODE_NIGHT_YES;
    }

    /**
     * Envía el tema al backend para que se guarde en la tabla Perfil del usuario actual.
     * Se ejecuta en segundo plano, si falla no afecta al usuario.
     */
    private static void sincronizarTemaConBackend(Context context, String tema) {
        int idUsuario = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)
                .getInt("idUsuario", -1);
        if (idUsuario == -1) {
            Log.w(TAG, "No hay sesión activa, no se sincroniza tema");
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        HashMap<String, String> body = new HashMap<>();
        body.put("tema", tema);

        api.actualizarTema(idUsuario, body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Tema sincronizado con backend: " + tema);
                } else {
                    Log.w(TAG, "Error al sincronizar tema: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Fallo de red al sincronizar tema: " + t.getMessage());
            }
        });
    }
}
