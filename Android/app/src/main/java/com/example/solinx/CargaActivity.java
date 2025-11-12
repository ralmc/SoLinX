package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.iniSesion;
//INICAR PROYECTO EN: CargaActivity
public class CargaActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;
    private static final String TAG = "CargaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carga);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(CargaActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error starting iniSesion Activity. Check AndroidManifest and class name: " + e.getMessage());
                }
            }
        }, SPLASH_DURATION);
    }
}