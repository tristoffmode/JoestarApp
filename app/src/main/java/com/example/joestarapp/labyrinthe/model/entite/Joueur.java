package com.example.joestarapp.labyrinthe.model.entite;

/**
 * Représente un joueur dans le labyrinthe, possédant une position,
 * une vitesse et une accélération sur les axes X et Y.
 */
public class Joueur
{
    /*-------------------------------------------*/
    /*                 Constantes                */
    /*-------------------------------------------*/

    /**
     * Coefficient de friction appliqué à la vitesse du joueur.
     */
    private static final double FRICTION = 0.80;


    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    /**
     * Position du joueur sur l'axe X.
     */
    private double posX;

    /**
     * Position du joueur sur l'axe Y.
     */
    private double posY;

    /**
     * Vitesse du joueur sur l'axe X.
     */
    private double vitesseX;

    /**
     * Vitesse du joueur sur l'axe Y.
     */
    private double vitesseY;

    /**
     * Accélération du joueur sur l'axe X.
     */
    private double accelerationX;

    /**
     * Accélération du joueur sur l'axe Y.
     */
    private double accelerationY;


    /*-------------------------------------------*/
    /*                Constructeur               */
    /*-------------------------------------------*/

    /**
     * Crée un joueur à une position donnée.
     *
     * @param posX Position initiale sur l'axe X.
     * @param posY Position initiale sur l'axe Y.
     */
    public Joueur(double posX, double posY)
    {
        this.posX          = posX;
        this.posY          = posY;
        this.vitesseX      = 0.0;
        this.vitesseY      = 0.0;
        this.accelerationX = 0.0;
        this.accelerationY = 0.0;
    }


    /*-------------------------------------------*/
    /*                 Accesseurs                */
    /*-------------------------------------------*/

    /**
     * @return La position X du joueur.
     */
    public double getPosX()
    {
        return this.posX;
    }

    /**
     * @return La position Y du joueur.
     */
    public double getPosY()
    {
        return this.posY;
    }

    /**
     * @return La vitesse sur l'axe X.
     */
    public double getVitesseX()
    {
        return this.vitesseX;
    }

    /**
     * @return La vitesse sur l'axe Y.
     */
    public double getVitesseY()
    {
        return this.vitesseY;
    }


    /*-------------------------------------------*/
    /*                Modificateurs              */
    /*-------------------------------------------*/

    /**
     * Définit l'accélération du joueur.
     *
     * @param ax Accélération sur l'axe X.
     * @param ay Accélération sur l'axe Y.
     */
    public void setAcceleration(double ax, double ay)
    {
        this.accelerationX = ax;
        this.accelerationY = ay;
    }

    /**
     * Stoppe le mouvement horizontal.
     */
    public void stopX()
    {
        this.vitesseX      = 0.0;
        this.accelerationX = 0.0;
    }

    /**
     * Stoppe le mouvement vertical.
     */
    public void stopY()
    {
        this.vitesseY      = 0.0;
        this.accelerationY = 0.0;
    }

    /**
     * Modifie directement la position du joueur.
     *
     * @param x Nouvelle position X.
     * @param y Nouvelle position Y.
     */
    public void setPosition(double x, double y)
    {
        this.posX = x;
        this.posY = y;
    }


    /*-------------------------------------------*/
    /*               Autres Mehtodes             */
    /*-------------------------------------------*/

    /**
     * Met à jour la position et la vitesse du joueur en appliquant
     * l'accélération, la friction et une limite de vitesse.
     *
     * @param vitesseMax Vitesse maximale autorisée.
     */
    public void update(double vitesseMax)
    {
        // Application de l'accélération
        this.vitesseX += this.accelerationX;
        this.vitesseY += this.accelerationY;

        // Application de la friction
        this.vitesseX *= Joueur.FRICTION;
        this.vitesseY *= Joueur.FRICTION;

        // Suppression des très petites vitesses
        if (Math.abs(this.vitesseX) < 0.0005)
        {
            this.vitesseX = 0.0;
        }

        if (Math.abs(this.vitesseY) < 0.0005)
        {
            this.vitesseY = 0.0;
        }

        // Limitation de la vitesse
        this.vitesseX = Math.max(-vitesseMax, Math.min(vitesseMax, this.vitesseX));
        this.vitesseY = Math.max(-vitesseMax, Math.min(vitesseMax, this.vitesseY));

        // Mise à jour de la position
        this.posX += this.vitesseX;
        this.posY += this.vitesseY;
    }

    /**
     * Met à jour le joueur sans limite de vitesse.
     */
    public void update()
    {
        this.update(Double.MAX_VALUE);
    }
}