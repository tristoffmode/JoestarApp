package com.example.joestarapp.labyrinthe.model.capteur;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.joestarapp.labyrinthe.model.capteur.interfaces.IEcouteDeplacement;

public class CapteurMouvement implements SensorEventListener
{
    private SensorManager       sensorManager;
    private Sensor              accelerometre;
    private IEcouteDeplacement ecouteur;

    public CapteurMouvement(Context context, IEcouteDeplacement ecouteur)
    {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometre = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.ecouteur      = ecouteur;
    }

    public void demarrer()
    {
        if(sensorManager != null && accelerometre != null)
        {
            sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void arreter()
    {
        this.sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent e)
    {
        float x = e.values[0];
        float y = e.values[1];

        if(this.ecouteur != null)
        {
            this.ecouteur.deplacer(x,y);
        }
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy) {}
}
