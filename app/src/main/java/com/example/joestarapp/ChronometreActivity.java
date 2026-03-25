package com.example.joestarapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChronometreActivity extends AppCompatActivity implements SensorEventListener {
    SharedPreferences sp;
    private TextView chronoText;
    private Button btnStart, btnStop, btnReset, btnLap, btnShare;
    private Button btnRetour;
    private ListView listView;

    private Handler handler = new Handler();
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private boolean isRunning = false;

    private ArrayList<String> lapList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    // Capteur
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastAcceleration = 0;

    // Runnable pour chrono
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;
                chronoText.setText(formatTime(elapsedTime));
                handler.postDelayed(this, 10);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometre);

        //SharedPreferences
        sp = getSharedPreferences("chrono", MODE_PRIVATE);

        chronoText = findViewById(R.id.chronoText);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnLap = findViewById(R.id.btnLap);
        btnShare = findViewById(R.id.btnShare);
        listView = findViewById(R.id.listView);
        btnRetour = findViewById(R.id.btn_retour);

        // Action du bouton retour
        btnRetour.setOnClickListener(v -> {
            finish();
        });

        // restauration
        elapsedTime = sp.getLong("time", 0);
        lapList = new ArrayList<>(sp.getStringSet("laps", new java.util.HashSet<>()));
        chronoText.setText(formatTime(elapsedTime));

        adapter = new ArrayAdapter<>(this, R.layout.item_lap, R.id.lapText, lapList);
        listView.setAdapter(adapter);

        // Capteur
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Bundle entrant
        String source = getIntent().getStringExtra("source");
        /*new AlertDialog.Builder(this)
                .setTitle("Bienvenue")
                .setMessage("Bienvenue dans le Chronomètre, lancé depuis : " + source)
                .setPositiveButton("OK", null)
                .show();*/

        btnStart.setOnClickListener(v -> startChrono());
        btnStop.setOnClickListener(v -> stopChrono());
        btnReset.setOnClickListener(v -> resetChrono());
        btnLap.setOnClickListener(v -> addLap());
        btnShare.setOnClickListener(v -> shareTime());
    }

    private void startChrono() {
        startTime = System.currentTimeMillis() - elapsedTime;
        isRunning = true;
        handler.post(runnable);
        Toast.makeText(this, "Chronomètre démarré", Toast.LENGTH_SHORT).show();
    }

    private void stopChrono() {
        isRunning = false;
        Toast.makeText(this, "Chronomètre mis en pause", Toast.LENGTH_SHORT).show();

        // sauvegarde
        saveData();
    }

    private void resetChrono() {
        new AlertDialog.Builder(this)
                .setMessage("Voulez-vous vraiment remettre à zéro ?")
                .setPositiveButton("OUI", (d, w) -> {
                    isRunning = false;
                    elapsedTime = 0;
                    chronoText.setText("00:00:000");
                    lapList.clear();
                    adapter.notifyDataSetChanged();

                    saveData();
                })
                .setNegativeButton("NON", null)
                .show();
    }

    private void addLap() {
        String lap = formatTime(elapsedTime);
        lapList.add("Tour " + (lapList.size() + 1) + " : " + lap);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Tour enregistré : " + lap, Toast.LENGTH_SHORT).show();
    }

    private void shareTime() {
        String message = "Mon chronomètre : " + formatTime(elapsedTime) + " - Tours : " + lapList;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(intent, "Partager via"));
    }

    // méthode sauvegarde
    private void saveData() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("time", elapsedTime);
        editor.putStringSet("laps", new java.util.HashSet<>(lapList));
        editor.apply();
    }

    private String formatTime(long time) {
        int ms = (int) (time % 1000);
        int sec = (int) (time / 1000) % 60;
        int min = (int) (time / (1000 * 60));

        return String.format("%02d:%02d:%03d", min, sec, ms);
    }

    // CAPTEUR

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x*x + y*y + z*z);

        if (Math.abs(acceleration - lastAcceleration) > 15) {
            addLap();
            Toast.makeText(this, "Tour enregistré par secousse !", Toast.LENGTH_SHORT).show();
        }

        lastAcceleration = acceleration;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    //CYCLE DE VIE

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        saveData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("time", elapsedTime);
        outState.putBoolean("running", isRunning);
        outState.putStringArrayList("laps", lapList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        elapsedTime = savedInstanceState.getLong("time");
        isRunning = savedInstanceState.getBoolean("running");
        lapList = savedInstanceState.getStringArrayList("laps");
        adapter = new ArrayAdapter<>(this, R.layout.item_lap, R.id.lapText, lapList);
        listView.setAdapter(adapter);
    }
}