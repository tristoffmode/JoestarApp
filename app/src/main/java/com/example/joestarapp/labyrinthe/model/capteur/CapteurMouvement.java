package com.example.joestarapp.labyrinthe.model.capteur;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.joestarapp.labyrinthe.model.capteur.interfaces.IEcouteDeplacement;

/**
 * Gère le capteur de mouvement (accéléromètre).
 */
public class CapteurMouvement implements SensorEventListener
{
    /*-------------------------------------------*/
    /*                 Constantes                */
    /*-------------------------------------------*/

    private static final String CapteurMouvement_TAG = "CapteurMouvement";


    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private SensorManager      sensorManager;
    private Sensor             accelerometre;
    private IEcouteDeplacement ecouteur;


    /*-------------------------------------------*/
    /*                Constructeur               */
    /*-------------------------------------------*/

    public CapteurMouvement(Context context, IEcouteDeplacement ecouteur)
    {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometre = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.ecouteur      = ecouteur;
    }


    /*-------------------------------------------*/
    /*               Méthodes publiques          */
    /*-------------------------------------------*/

    public void demarrer()
    {
        if (this.sensorManager != null && this.accelerometre != null)
        {
            this.sensorManager.registerListener(
                    this,
                    this.accelerometre,
                    SensorManager.SENSOR_DELAY_GAME
            );
        }
    }

    public void arreter()
    {
        if (this.sensorManager != null)
        {
            this.sensorManager.unregisterListener(this);
        }
    }


    /*-------------------------------------------*/
    /*         Implémentation capteur            */
    /*-------------------------------------------*/

    @Override
    public void onSensorChanged(SensorEvent e)
    {
        float x = e.values[0];
        float y = e.values[1];

        if (this.ecouteur != null)
        {
            this.ecouteur.deplacer(x, y);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}