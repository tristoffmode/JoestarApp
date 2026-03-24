package com.example.joestarapp.labyrinthe.model.entite;

/**
 * Représente un niveau du labyrinthe.
 * Contient la grille, la taille et les positions du joueur et de l'arrivée.
 */
public class Niveau
{
    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private char[][] tabLabyrinthe;

    private int largeur;
    private int hauteur;

    private int posXJoueur;
    private int posYJoueur;

    private int posXArriver;
    private int posYArriver;


    /*-------------------------------------------*/
    /*                Constructeur               */
    /*-------------------------------------------*/

    public Niveau(char[][] tabLabyrinthe)
    {
        this.tabLabyrinthe = tabLabyrinthe;

        this.hauteur = this.tabLabyrinthe.length;
        this.largeur = this.tabLabyrinthe[0].length;

        this.posXJoueur  = 0;
        this.posYJoueur  = 0;
        this.posXArriver = 0;
        this.posYArriver = 0;

        this.localiserJoueurArriver();
    }


    /*-------------------------------------------*/
    /*                 Accesseurs                */
    /*-------------------------------------------*/

    public int getLargeur()
    {
        return this.largeur;
    }

    public int getHauteur()
    {
        return this.hauteur;
    }

    public int getPosXJoueur()
    {
        return this.posXJoueur;
    }

    public int getPosYJoueur()
    {
        return this.posYJoueur;
    }

    public int getPosXArriver()
    {
        return this.posXArriver;
    }

    public int getPosYArriver()
    {
        return this.posYArriver;
    }


    /*-------------------------------------------*/
    /*               Méthodes utiles             */
    /*-------------------------------------------*/

    public boolean estMur(int x, int y)
    {
        // Sécurité anti-crash
        if (x < 0 || x >= this.largeur || y < 0 || y >= this.hauteur)
        {
            return true;
        }

        return this.tabLabyrinthe[y][x] == '#';
    }


    /*-------------------------------------------*/
    /*           Méthodes internes               */
    /*-------------------------------------------*/

    private void localiserJoueurArriver()
    {
        for (int cptY = 0; cptY < this.hauteur; cptY++)
        {
            for (int cptX = 0; cptX < this.largeur; cptX++)
            {
                if (this.tabLabyrinthe[cptY][cptX] == 'S')
                {
                    this.posXJoueur = cptX;
                    this.posYJoueur = cptY;
                }

                if (this.tabLabyrinthe[cptY][cptX] == 'E')
                {
                    this.posXArriver = cptX;
                    this.posYArriver = cptY;
                }
            }
        }
    }
}