package com.example.joestarapp.labyrinthe.model.entite;

/**
 * Représente le score d'un niveau.
 * Gère les points et le temps du joueur.
 */
public class Score
{
    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private int  niveau;
    private int  points;
    private long temps;


    /*-------------------------------------------*/
    /*                Constructeur               */
    /*-------------------------------------------*/

    public Score(int niveau)
    {
        this.niveau = niveau;
        this.points = 0;
        this.temps  = 0;
    }


    /*-------------------------------------------*/
    /*                 Accesseurs                */
    /*-------------------------------------------*/

    public int getPoints()
    {
        return this.points;
    }

    public int getNiveau()
    {
        return this.niveau;
    }

    public long getTemps()
    {
        return this.temps;
    }


    /*-------------------------------------------*/
    /*               Méthodes métier             */
    /*-------------------------------------------*/

    public int calculerPointBonus(long tempsLimite)
    {
        int bonus = (int) Math.max(0, (tempsLimite - this.temps) / 100);

        this.ajouterPoints(bonus);

        return bonus;
    }

    public void ajouterPoints(int points)
    {
        this.points += points;
    }

    public void ajouterTemps(long temps)
    {
        this.temps += temps;
    }
}