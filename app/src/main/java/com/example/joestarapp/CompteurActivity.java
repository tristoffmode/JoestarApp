package com.example.joestarapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CompteurActivity extends AppCompatActivity implements SensorEventListener {

    // Éléments demandés : Capteurs et Widgets (ProgressBar, Chronomètre)
    private SensorManager gestionnaireCapteurs;
    private Sensor accelerometre;
    private TextView tvPas;
    private ProgressBar barreProgression;
    private Chronometer chronometre;
    private Button btnRetour;

    private int nbPas = 0;
    private boolean pasDetecte = false;
    private static final double SEUIL_PAS = 12.0;
    private static final double SEUIL_REINITIALISATION = 9.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialisation de l'activité et configuration des composants UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compteur);

        tvPas = findViewById(R.id.tv_pas);
        barreProgression = findViewById(R.id.barre_progression);
        chronometre = findViewById(R.id.chronometre);
        btnRetour = findViewById(R.id.btn_retour);

        // Action du bouton retour
        btnRetour.setOnClickListener(v -> {
            finish();
        });

        // Initialisation du service de capteurs
        gestionnaireCapteurs = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (gestionnaireCapteurs != null) {
            accelerometre = gestionnaireCapteurs.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Configuration du chronomètre
        chronometre.setBase(SystemClock.elapsedRealtime());
        chronometre.start();
    }

    @Override
    protected void onResume() {
        // Cycle de vie : Activation du capteur lors de la reprise
        super.onResume();
        if (accelerometre != null) {
            gestionnaireCapteurs.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        // Cycle de vie : Désactivation du capteur pour économiser la batterie
        super.onPause();
        if (gestionnaireCapteurs != null) {
            gestionnaireCapteurs.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Traitement des données du capteur accéléromètre
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x * x + y * y + z * z);

            // Logique de détection de pas (hystérésis)
            if (magnitude > SEUIL_PAS && !pasDetecte) {
                nbPas++;
                pasDetecte = true;
                tvPas.setText(String.valueOf(nbPas));
                barreProgression.setProgress(nbPas);
            } else if (magnitude < SEUIL_REINITIALISATION) {
                pasDetecte = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Méthode requise par SensorEventListener
    }
}