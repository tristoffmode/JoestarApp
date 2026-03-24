package com.example.joestarapp.labyrinthe.model.sauvegarde;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe permettant de gérer les préférences utilisateur
 * (vibration, capteur, etc.) via SharedPreferences.
 */
public class GestionPreferences
{
    /*-------------------------------------------*/
    /*                 Constantes                */
    /*-------------------------------------------*/

    private static final String PREFS_NOM   = "labyrinthe_preferences";
    private static final String CLE_VIBRATION = "vibration_active";
    private static final String CLE_CAPTEUR   = "capteur_actif";


    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private SharedPreferences prefs;


    /*-------------------------------------------*/
    /*                Constructeur               */
    /*-------------------------------------------*/

    /**
     * Initialise la gestion des préférences.
     *
     * @param context Contexte Android.
     */
    public GestionPreferences(Context context)
    {
        this.prefs = context.getSharedPreferences(GestionPreferences.PREFS_NOM, Context.MODE_PRIVATE);
    }


    /*-------------------------------------------*/
    /*                 Accesseurs                */
    /*-------------------------------------------*/

    /**
     * @return true si la vibration est activée.
     */
    public boolean isVibrationActive()
    {
        return this.prefs.getBoolean(GestionPreferences.CLE_VIBRATION, true);
    }

    /**
     * @return true si le capteur est actif.
     */
    public boolean isCapteurActif()
    {
        return this.prefs.getBoolean(GestionPreferences.CLE_CAPTEUR, true);
    }


    /*-------------------------------------------*/
    /*                Modificateurs              */
    /*-------------------------------------------*/

    /**
     * Active ou désactive la vibration.
     */
    public void setVibrationActive(boolean actif)
    {
        this.prefs.edit().putBoolean(GestionPreferences.CLE_VIBRATION, actif).apply();
    }

    /**
     * Active ou désactive le capteur.
     */
    public void setCapteurActif(boolean actif)
    {
        this.prefs.edit().putBoolean(GestionPreferences.CLE_CAPTEUR, actif).apply();
    }
}