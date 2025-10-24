package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.iniSesion;

// This Activity shows the splash screen at the start.
public class CargaActivity extends AppCompatActivity {

    // Duration of the splash screen in milliseconds (3 seconds)
    private static final int SPLASH_DURATION = 3000;
    private static final String TAG = "CargaActivity"; // Tag for Logcat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure this is the correct name for your loading layout (R.layout.carga o R.layout.activity_carga)
        setContentView(R.layout.activity_carga);

        // Creates a timer to wait 3 seconds
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // 1. CRITICAL POINT: The destination is now the iniSesion Activity
                    Intent intent = new Intent(CargaActivity.this, MainActivity.class);

                    // 2. Start the Activity
                    startActivity(intent);

                    // 3. Close the loading screen
                    finish();
                } catch (Exception e) {
                    // If the Activity does not exist or is not defined in the Manifest, it prints an error
                    Log.e(TAG, "Error starting iniSesion Activity. Check AndroidManifest and class name: " + e.getMessage());
                }
            }
        }, SPLASH_DURATION);
    }
}