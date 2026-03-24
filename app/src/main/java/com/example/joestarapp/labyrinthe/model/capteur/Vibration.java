package com.example.joestarapp.labyrinthe.model.capteur;

import android.content.Context;
import android.os.Vibrator;

/**
 * Gère la vibration du téléphone.
 */
public class Vibration
{

    /*-------------------------------------------*/
    /*               Méthodes statiques          */
    /*-------------------------------------------*/
    public static void vibrer(Context context , int duree)
    {
        Vibrator vibreur = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if(vibreur != null && vibreur.hasVibrator())
        {
            vibreur.vibrate(duree);
        }
    }
}
