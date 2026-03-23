package com.example.joestarapp.labyrinthe.model.entite;

public class Joueur
{
    private double posX;
    private double posY;

    private double vitesseX;
    private double vitesseY;

    private double accelerationX;
    private double accelerationY;

    // Friction élevée : la boule s'arrête vite quand on repose le téléphone
    private static final double FRICTION = 0.75;

    public Joueur(double posX, double posY)
    {
        this.posX          = posX;
        this.posY          = posY;
        this.vitesseX      = 0.0;
        this.vitesseY      = 0.0;
        this.accelerationX = 0.0;
        this.accelerationY = 0.0;
    }

    /**
     * Met à jour la physique avec une vitesse maximale plafonnée.
     * @param vitesseMax vitesse max en unités/frame
     */
    public void update(double vitesseMax)
    {
        this.vitesseX += this.accelerationX;
        this.vitesseY += this.accelerationY;

        // Friction
        this.vitesseX *= FRICTION;
        this.vitesseY *= FRICTION;

        // Micro-seuil : stoppe le glissement résiduel
        if (Math.abs(this.vitesseX) < 0.001) this.vitesseX = 0.0;
        if (Math.abs(this.vitesseY) < 0.001) this.vitesseY = 0.0;

        // Plafonnement de la vitesse
        this.vitesseX = Math.max(-vitesseMax, Math.min(vitesseMax, this.vitesseX));
        this.vitesseY = Math.max(-vitesseMax, Math.min(vitesseMax, this.vitesseY));

        this.posX += this.vitesseX;
        this.posY += this.vitesseY;
    }

    public void update()
    {
        this.update(Double.MAX_VALUE);
    }

    public void setAcceleration(double ax, double ay)
    {
        this.accelerationX = ax;
        this.accelerationY = ay;
    }

    public void stopX() { this.vitesseX = 0.0; this.accelerationX = 0.0; }
    public void stopY() { this.vitesseY = 0.0; this.accelerationY = 0.0; }

    public double getPosX()     { return this.posX    ; }
    public double getPosY()     { return this.posY    ; }
    public double getVitesseX() { return this.vitesseX; }
    public double getVitesseY() { return this.vitesseY; }

    public void setPosition(double x, double y)
    {
        this.posX = x;
        this.posY = y;
    }
}
