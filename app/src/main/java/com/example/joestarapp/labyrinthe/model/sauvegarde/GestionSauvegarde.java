package com.example.joestarapp.labyrinthe.model.sauvegarde;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gère la sauvegarde des données de progression via SharedPreferences.
 * Fonctionne avec un nombre de niveaux dynamique.
 */
public class GestionSauvegarde
{
    /*-------------------------------------------*/
    /*                 Constantes                */
    /*-------------------------------------------*/

    private static final String PREFS_NOM          = "labyrinthe_sauvegarde";
    private static final String CLE_NIVEAU_MAX     = "niveau_debloque_max";
    private static final String CLE_NIVEAU_REUSSI  = "niveau_reussi_";
    private static final String CLE_MEILLEUR_SCORE = "meilleur_score_";


    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private SharedPreferences prefs;


    /*-------------------------------------------*/
    /*                Constructeur               */
    /*-------------------------------------------*/

    /**
     * Initialise la gestion de la sauvegarde.
     *
     * @param context Contexte Android.
     */
    public GestionSauvegarde(Context context)
    {
        this.prefs = context.getSharedPreferences(GestionSauvegarde.PREFS_NOM, Context.MODE_PRIVATE);
    }


    /*-------------------------------------------*/
    /*                 Accesseurs                */
    /*-------------------------------------------*/

    /**
     * @return Le niveau maximum débloqué.
     */
    public int getNiveauDebloque()
    {
        return this.prefs.getInt(GestionSauvegarde.CLE_NIVEAU_MAX, 1);
    }

    /**
     * Indique si un niveau est déjà réussi.
     */
    public boolean estNiveauReussi(int numNiveau)
    {
        return this.prefs.getBoolean(GestionSauvegarde.CLE_NIVEAU_REUSSI + numNiveau, false);
    }

    /**
     * @return Le meilleur score pour un niveau donné.
     */
    public int getMeilleurScore(int numNiveau)
    {
        return this.prefs.getInt(GestionSauvegarde.CLE_MEILLEUR_SCORE + numNiveau, 0);
    }


    /*-------------------------------------------*/
    /*                Modificateurs              */
    /*-------------------------------------------*/

    /**
     * Débloque un niveau si celui-ci est supérieur au niveau actuel.
     */
    public void debloquerNiveau(int numNiveau)
    {
        if (numNiveau > this.getNiveauDebloque())
        {
            this.prefs.edit()
                    .putInt(GestionSauvegarde.CLE_NIVEAU_MAX, numNiveau)
                    .apply();
        }
    }

    /**
     * Marque un niveau comme réussi et débloque le suivant.
     */
    public void marquerNiveauReussi(int numNiveau)
    {
        this.prefs.edit()
                .putBoolean(GestionSauvegarde.CLE_NIVEAU_REUSSI + numNiveau, true)
                .apply();

        this.debloquerNiveau(numNiveau + 1);
    }

    /**
     * Sauvegarde le score si celui-ci est meilleur que l'existant.
     */
    public void sauvegarderScore(int numNiveau, int score)
    {
        if (score > this.getMeilleurScore(numNiveau))
        {
            this.prefs.edit()
                    .putInt(GestionSauvegarde.CLE_MEILLEUR_SCORE + numNiveau, score)
                    .apply();
        }
    }

    /**
     * Réinitialise toutes les données sauvegardées.
     */
    public void reinitialiser()
    {
        this.prefs.edit().clear().apply();
    }
}