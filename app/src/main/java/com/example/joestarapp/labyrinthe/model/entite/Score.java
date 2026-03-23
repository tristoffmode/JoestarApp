package com.example.joestarapp.labyrinthe.model.entite;

public class Score
{
    private int niveau;
    private int points;

    private int temps;

    public Score(int niveau)
    {
        this.niveau = niveau;
        this.temps  = 0;
        this.points = 0;
    }

    public int getPoints() { return this.points ;}
    public int getNiveau() { return this.niveau ;}

    public long getTemps() { return this.temps  ;}

    public int calculerPointBonus(long tempsLimite)
    {
        int bonus = (int) Math.max(0,(tempsLimite-this.temps)/100);
        this.ajouterPoints(bonus);

        return bonus;
    }

    public void ajouterPoints(int points) { this.points += points ; }
    public void ajouterTemps (long temps ){ this.temps  += temps  ; }


}
