package com.example.joestarapp.labyrinthe.viewmodel;

import android.content.Context;

import com.example.joestarapp.labyrinthe.model.capteur.Vibration;
import com.example.joestarapp.labyrinthe.model.entite.Joueur;
import com.example.joestarapp.labyrinthe.model.lectureFichier.LectureLabyrinthe;
import com.example.joestarapp.labyrinthe.model.entite.Niveau;
import com.example.joestarapp.labyrinthe.model.entite.Score;
import com.example.joestarapp.labyrinthe.model.sauvegarde.GestionSauvegarde;

public class MetierLabyrinthe
{
    private Joueur joueur;
    private Niveau niveau;
    private Score  score;

    private int  niveauCourant;
    private long temps;

    private final Context           context;
    private final GestionSauvegarde sauvegarde;

    private static final double SENSIBILITE      = 0.008;
    private static final double SEUIL_REPOS      = 0.8;
    private static final double VITESSE_MAX      = 0.18;

    // Rayon de la boule en unités de case (0.4 = la boule occupe 80% d'une case)
    // C'est cette valeur qui empêche la boule d'entrer dans les murs
    public static final double RAYON_BOULE       = 0.4;

    private static final long DELAI_VIBRATION_MS = 200;
    private long dernierTempsVibration           = 0;

    public MetierLabyrinthe(Context context)
    {
        this.context       = context;
        this.sauvegarde    = new GestionSauvegarde(context);
        this.niveauCourant = 1;
        chargerNiveau(niveauCourant);
    }

    public MetierLabyrinthe(Context context, int niveauDepart)
    {
        this.context       = context;
        this.sauvegarde    = new GestionSauvegarde(context);
        this.niveauCourant = niveauDepart;
        chargerNiveau(niveauCourant);
    }

    public Joueur            getJoueur()        { return this.joueur;        }
    public Niveau            getNiveau()        { return this.niveau;        }
    public Score             getScore()         { return this.score;         }
    public int               getNiveauCourant() { return this.niveauCourant; }
    public GestionSauvegarde getSauvegarde()    { return this.sauvegarde;    }

    public void chargerNiveau(int numNiveau)
    {
        String nomFichier = "niveau" + numNiveau + ".txt";
        char[][] grille   = LectureLabyrinthe.initNiveau(this.context, nomFichier);

        if (grille == null)
            throw new RuntimeException("Fichier niveau introuvable : " + nomFichier);

        this.niveau = new Niveau(grille);
        this.joueur = new Joueur(this.niveau.getPosXJoueur(), this.niveau.getPosYJoueur());

        if (this.score == null)
            this.score = new Score(numNiveau);

        this.demarrerChronometre();
    }

    public void gererDeplacementCapteur(double x, double y)
    {
        double ax = 0;
        double ay = 0;
        if (Math.abs(x) > SEUIL_REPOS) ax = -x * SENSIBILITE;
        if (Math.abs(y) > SEUIL_REPOS) ay =  y * SENSIBILITE;
        this.joueur.setAcceleration(ax, ay);
    }

    /**
     * Physique + collisions avec prise en compte du rayon de la boule.
     *
     * La boule est traitée comme un cercle de rayon RAYON_BOULE.
     * Pour chaque axe, on teste les 4 coins du carré englobant la boule
     * afin qu'elle ne puisse jamais chevaucher un mur.
     */
    public void mettreAJourPhysique()
    {
        double ancienX = this.joueur.getPosX();
        double ancienY = this.joueur.getPosY();

        this.joueur.update(VITESSE_MAX);

        double nouveauX = this.joueur.getPosX();
        double nouveauY = this.joueur.getPosY();

        boolean collision = false;

        // --- Collision axe X ---
        // On teste les deux côtés horizontaux de la boule avec l'ancienne Y
        if (estEnCollisionX(nouveauX, ancienY))
        {
            nouveauX  = ancienX;
            this.joueur.stopX();
            collision = true;
        }

        // --- Collision axe Y ---
        // On teste les deux côtés verticaux avec la X corrigée
        if (estEnCollisionY(nouveauX, nouveauY))
        {
            nouveauY  = ancienY;
            this.joueur.stopY();
            collision = true;
        }

        // Bornes absolues de la grille (marge = rayon)
        nouveauX = Math.max(RAYON_BOULE, Math.min(nouveauX, this.niveau.getLargeur()  - 1 - RAYON_BOULE));
        nouveauY = Math.max(RAYON_BOULE, Math.min(nouveauY, this.niveau.getHauteur() - 1 - RAYON_BOULE));

        this.joueur.setPosition(nouveauX, nouveauY);

        if (collision)
        {
            long maintenant = System.currentTimeMillis();
            if (maintenant - dernierTempsVibration > DELAI_VIBRATION_MS)
            {
                Vibration.vibrer(context, 60);
                dernierTempsVibration = maintenant;
            }
        }
    }

    /**
     * Vérifie si la boule touche un mur sur l'axe X.
     * On teste le bord gauche (cx - rayon) et le bord droit (cx + rayon).
     */
    private boolean estEnCollisionX(double cx, double cy)
    {
        int ligneHaut = (int)(cy - RAYON_BOULE);
        int ligneBas  = (int)(cy + RAYON_BOULE);

        // Bord gauche
        int colGauche = (int)(cx - RAYON_BOULE);
        if (estMurSur(colGauche, ligneHaut) || estMurSur(colGauche, ligneBas)) return true;

        // Bord droit
        int colDroite = (int)(cx + RAYON_BOULE);
        if (estMurSur(colDroite, ligneHaut) || estMurSur(colDroite, ligneBas)) return true;

        return false;
    }

    /**
     * Vérifie si la boule touche un mur sur l'axe Y.
     * On teste le bord haut (cy - rayon) et le bord bas (cy + rayon).
     */
    private boolean estEnCollisionY(double cx, double cy)
    {
        int colGauche = (int)(cx - RAYON_BOULE);
        int colDroite = (int)(cx + RAYON_BOULE);

        // Bord haut
        int ligneHaut = (int)(cy - RAYON_BOULE);
        if (estMurSur(colGauche, ligneHaut) || estMurSur(colDroite, ligneHaut)) return true;

        // Bord bas
        int ligneBas = (int)(cy + RAYON_BOULE);
        if (estMurSur(colGauche, ligneBas) || estMurSur(colDroite, ligneBas)) return true;

        return false;
    }

    /** Vérifie qu'une case est dans la grille ET est un mur. */
    private boolean estMurSur(int col, int ligne)
    {
        if (col  < 0 || col  >= this.niveau.getLargeur())  return true; // hors grille = mur
        if (ligne < 0 || ligne >= this.niveau.getHauteur()) return true;
        return this.niveau.estMur(col, ligne);
    }

    public boolean aGagne()
    {
        double epsilon = 0.5;
        return Math.abs(this.joueur.getPosX() - this.niveau.getPosXArriver()) < epsilon
                && Math.abs(this.joueur.getPosY() - this.niveau.getPosYArriver()) < epsilon;
    }

    public void enregistrerTemps()
    {
        long tempsEcoule = System.currentTimeMillis() - this.temps;
        this.score.ajouterTemps(tempsEcoule);
        this.score.calculerPointBonus(60000);
    }

    public void sauvegarderVictoire()
    {
        sauvegarde.marquerNiveauReussi(niveauCourant);
        sauvegarde.sauvegarderScore(niveauCourant, score.getPoints());
    }

    public void demarrerChronometre()
    {
        this.temps = System.currentTimeMillis();
    }

    public long getTempsEcouleMs()
    {
        return System.currentTimeMillis() - this.temps;
    }

    public void niveauSuivant()
    {
        this.niveauCourant++;
        chargerNiveau(this.niveauCourant);
    }
}
