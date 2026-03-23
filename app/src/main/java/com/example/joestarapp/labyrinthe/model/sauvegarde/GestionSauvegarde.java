package com.example.joestarapp.labyrinthe.model.sauvegarde;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gère la sauvegarde des données de progression via SharedPreferences.
 * Modulaire : fonctionne quel que soit le nombre de niveaux.
 */
public class GestionSauvegarde
{
    private static final String PREFS_NOM          = "labyrinthe_sauvegarde";
    private static final String CLE_NIVEAU_MAX     = "niveau_debloque_max";
    private static final String CLE_NIVEAU_REUSSI  = "niveau_reussi_";   // + numéro
    private static final String CLE_MEILLEUR_SCORE = "meilleur_score_";  // + numéro

    private  SharedPreferences prefs;

    public GestionSauvegarde(Context context)
    {
        this.prefs = context.getSharedPreferences(PREFS_NOM, Context.MODE_PRIVATE);
    }

    public int getNiveauDebloque()
    {
        return this.prefs.getInt(CLE_NIVEAU_MAX, 1);
    }

    public void debloquerNiveau(int numNiveau)
    {
        if (numNiveau > getNiveauDebloque())
        {
            this.prefs.edit().putInt(CLE_NIVEAU_MAX, numNiveau).apply();
        }
    }

    public boolean estNiveauReussi(int numNiveau)
    {
        return this.prefs.getBoolean(CLE_NIVEAU_REUSSI + numNiveau, false);
    }

    public void marquerNiveauReussi(int numNiveau)
    {
        this.prefs.edit().putBoolean(CLE_NIVEAU_REUSSI + numNiveau, true).apply();
        this.debloquerNiveau(numNiveau + 1);
    }

    public int getMeilleurScore(int numNiveau)
    {
        return this.prefs.getInt(CLE_MEILLEUR_SCORE + numNiveau, 0);
    }

    public void sauvegarderScore(int numNiveau, int score)
    {
        if (score > getMeilleurScore(numNiveau))
        {
            this.prefs.edit().putInt(CLE_MEILLEUR_SCORE + numNiveau, score).apply();
        }
    }

    public void reinitialiser()
    {
        this.prefs.edit().clear().apply();
    }
}
