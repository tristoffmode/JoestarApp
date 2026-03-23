package com.example.joestarapp.labyrinthe.model.entite;

public class Niveau
{
    private char[][] tabLabyrinthe;
    private int largeur;
    private int hauteur;

    private int posXJoueur;
    private int posYJoueur;
    private int posXArriver;
    private int posYArriver;

    public Niveau(char[][] tabLabyrinthe)
    {
        this.tabLabyrinthe = tabLabyrinthe;
        this.hauteur       = this.tabLabyrinthe.length;
        this.largeur       = this.tabLabyrinthe[0].length;

        this.localiserJoueurArriver();
    }

    public boolean estMur(int x,int y)
    {
        return this.tabLabyrinthe[y][x] == '#';
    }

    public int getLargeur    () { return this.largeur     ; }
    public int getHauteur    () { return this.hauteur     ; }
    public int getPosXJoueur () { return this.posXJoueur  ; }
    public int getPosYJoueur () { return this.posYJoueur  ; }
    public int getPosXArriver() { return  this.posXArriver; }
    public int getPosYArriver() { return this.posYArriver ; }

    private void localiserJoueurArriver()
    {
        for(int cptY = 0; cptY < this.hauteur; cptY++)
        {
            for(int cptX = 0; cptX < this.largeur; cptX++)
            {
                if(this.tabLabyrinthe[cptY][cptX] == 'S')
                {
                    this.posXJoueur = cptX;
                    this.posYJoueur = cptY;
                }

                if(this.tabLabyrinthe[cptY][cptX] == 'E')
                {
                    this.posXArriver = cptX;
                    this.posYArriver = cptY;
                }

            }
        }
    }
}
