package com.example.joestarapp.labyrinthe.viewmodel;

import android.content.Context;

import com.example.joestarapp.labyrinthe.model.capteur.Vibration;
import com.example.joestarapp.labyrinthe.model.entite.Joueur;
import com.example.joestarapp.labyrinthe.model.lectureFichier.LectureLabyrinthe;
import com.example.joestarapp.labyrinthe.model.entite.Niveau;
import com.example.joestarapp.labyrinthe.model.entite.Score;
import com.example.joestarapp.labyrinthe.model.sauvegarde.GestionPreferences;
import com.example.joestarapp.labyrinthe.model.sauvegarde.GestionSauvegarde;

/**
 * Gère toute la logique métier du labyrinthe :
 * chargement des niveaux, gestion du joueur, collisions,
 * chronomètre, score et préférences.
 */
public class MetierLabyrinthe
{
    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private Joueur joueur;
    private Niveau niveau;
    private Score  score;

    private int  niveauCourant;
    private long temps;

    private final Context            context;
    private final GestionSauvegarde  sauvegarde;
    private final GestionPreferences preferences;

    private static final double SENSIBILITE        = 0.025;
    private static final double VITESSE_MAX        = 0.12;
    public  static final double RAYON_BOULE        = 0.25;

    private static final long DELAI_VIBRATION_MS   = 200;
    private long              dernierTempsVibration = 0;


    /*-------------------------------------------*/
    /*                Constructeurs              */
    /*-------------------------------------------*/

    /**
     * Constructeur par défaut : commence au niveau 1.
     *
     * @param context Contexte Android.
     */
    public MetierLabyrinthe(Context context)
    {
        this.context       = context;
        this.sauvegarde    = new GestionSauvegarde(context);
        this.preferences   = new GestionPreferences(context);
        this.niveauCourant = 1;
        this.chargerNiveau(this.niveauCourant);
    }

    /**
     * Constructeur permettant de choisir le niveau de départ.
     *
     * @param context      Contexte Android.
     * @param niveauDepart Niveau initial.
     */
    public MetierLabyrinthe(Context context, int niveauDepart)
    {
        this.context       = context;
        this.sauvegarde    = new GestionSauvegarde(context);
        this.preferences   = new GestionPreferences(context);
        this.niveauCourant = niveauDepart;
        this.chargerNiveau(this.niveauCourant);
    }


    /*-------------------------------------------*/
    /*                 Accesseurs                */
    /*-------------------------------------------*/

    public Joueur             getJoueur()        { return this.joueur;        }
    public Niveau             getNiveau()        { return this.niveau;        }
    public Score              getScore()         { return this.score;         }
    public int                getNiveauCourant() { return this.niveauCourant; }
    public GestionSauvegarde  getSauvegarde()    { return this.sauvegarde;    }
    public GestionPreferences getPreferences()   { return this.preferences;   }


    /*-------------------------------------------*/
    /*               Autres Mehtodes             */
    /*-------------------------------------------*/

    /**
     * Charge un niveau depuis un fichier texte.
     *
     * @param numNiveau Numéro du niveau à charger.
     */
    public void chargerNiveau(int numNiveau)
    {
        String nomFichier = "niveau" + numNiveau + ".txt";
        char[][] grille   = LectureLabyrinthe.initNiveau(this.context, nomFichier);

        if (grille == null)
        {
            throw new RuntimeException("Fichier niveau introuvable : " + nomFichier);
        }

        this.niveau = new Niveau(grille);
        this.joueur = new Joueur(
                this.niveau.getPosXJoueur() + 0.5,
                this.niveau.getPosYJoueur() + 0.5
        );

        if (this.score == null)
        {
            this.score = new Score(numNiveau);
        }

        this.demarrerChronometre();
    }

    /**
     * Applique l'accélération provenant du capteur.
     *
     * @param capteurX Valeur du capteur sur X.
     * @param capteurY Valeur du capteur sur Y.
     */
    public void gererDeplacementCapteur(double capteurX, double capteurY)
    {
        if (!this.preferences.isCapteurActif())
        {
            return;
        }

        double ax = capteurY * MetierLabyrinthe.SENSIBILITE;
        double ay = capteurX * MetierLabyrinthe.SENSIBILITE;

        this.joueur.setAcceleration(ax, ay);
    }

    /**
     * Met à jour la physique du joueur et gère les collisions.
     */
    public void mettreAJourPhysique()
    {
        double ancienX = this.joueur.getPosX();
        double ancienY = this.joueur.getPosY();

        this.joueur.update(MetierLabyrinthe.VITESSE_MAX);

        double nouveauX = this.joueur.getPosX();
        double nouveauY = this.joueur.getPosY();

        boolean collision = false;

        if (this.estEnCollisionX(nouveauX, ancienY))
        {
            nouveauX  = ancienX;
            this.joueur.stopX();
            collision = true;
        }

        if (this.estEnCollisionY(nouveauX, nouveauY))
        {
            nouveauY  = ancienY;
            this.joueur.stopY();
            collision = true;
        }

        nouveauX = Math.max(MetierLabyrinthe.RAYON_BOULE,
                Math.min(nouveauX, this.niveau.getLargeur() - 1 - MetierLabyrinthe.RAYON_BOULE));

        nouveauY = Math.max(MetierLabyrinthe.RAYON_BOULE,
                Math.min(nouveauY, this.niveau.getHauteur() - 1 - MetierLabyrinthe.RAYON_BOULE));

        this.joueur.setPosition(nouveauX, nouveauY);

        if (collision
                && this.preferences.isVibrationActive()
                && (Math.abs(this.joueur.getVitesseX()) > 0.001
                || Math.abs(this.joueur.getVitesseY()) > 0.001))
        {
            long maintenant = System.currentTimeMillis();

            if (maintenant - this.dernierTempsVibration > MetierLabyrinthe.DELAI_VIBRATION_MS)
            {
                Vibration.vibrer(this.context, 60);
                this.dernierTempsVibration = maintenant;
            }
        }
    }

    private boolean estEnCollisionX(double cx, double cy)
    {
        int ligneHaut = (int) Math.floor(cy - MetierLabyrinthe.RAYON_BOULE);
        int ligneBas  = (int) Math.floor(cy + MetierLabyrinthe.RAYON_BOULE);
        int colGauche = (int) Math.floor(cx - MetierLabyrinthe.RAYON_BOULE);
        int colDroite = (int) Math.floor(cx + MetierLabyrinthe.RAYON_BOULE);

        if (this.estMurSur(colGauche, ligneHaut) || this.estMurSur(colGauche, ligneBas))
        {
            return true;
        }

        if (this.estMurSur(colDroite, ligneHaut) || this.estMurSur(colDroite, ligneBas))
        {
            return true;
        }

        return false;
    }

    private boolean estEnCollisionY(double cx, double cy)
    {
        int colGauche = (int) Math.floor(cx - MetierLabyrinthe.RAYON_BOULE);
        int colDroite = (int) Math.floor(cx + MetierLabyrinthe.RAYON_BOULE);
        int ligneHaut = (int) Math.floor(cy - MetierLabyrinthe.RAYON_BOULE);
        int ligneBas  = (int) Math.floor(cy + MetierLabyrinthe.RAYON_BOULE);

        if (this.estMurSur(colGauche, ligneHaut) || this.estMurSur(colDroite, ligneHaut))
        {
            return true;
        }

        if (this.estMurSur(colGauche, ligneBas) || this.estMurSur(colDroite, ligneBas))
        {
            return true;
        }

        return false;
    }

    private boolean estMurSur(int col, int ligne)
    {
        if (col < 0 || col >= this.niveau.getLargeur())
        {
            return true;
        }

        if (ligne < 0 || ligne >= this.niveau.getHauteur())
        {
            return true;
        }

        return this.niveau.estMur(col, ligne);
    }

    /**
     * Vérifie si le joueur a atteint la zone d'arrivée.
     *
     * @return true si le joueur est dans la zone d'arrivée.
     */
    public boolean aGagne()
    {
        double epsilon = 0.6;

        return Math.abs(this.joueur.getPosX() - (this.niveau.getPosXArriver() + 0.5)) < epsilon
                && Math.abs(this.joueur.getPosY() - (this.niveau.getPosYArriver() + 0.5)) < epsilon;
    }

    /**
     * Enregistre le temps écoulé dans le score.
     */
    public void enregistrerTemps()
    {
        long tempsEcoule = System.currentTimeMillis() - this.temps;
        this.score.ajouterTemps(tempsEcoule);
        this.score.calculerPointBonus(60000);
    }

    /**
     * Sauvegarde la victoire du joueur.
     */
    public void sauvegarderVictoire()
    {
        this.sauvegarde.marquerNiveauReussi(this.niveauCourant);
        this.sauvegarde.sauvegarderScore(this.niveauCourant, this.score.getPoints());
    }

    /**
     * Démarre le chronomètre du niveau.
     */
    public void demarrerChronometre()
    {
        this.temps = System.currentTimeMillis();
    }

    /**
     * @return Le temps écoulé depuis le début du niveau.
     */
    public long getTempsEcouleMs()
    {
        return System.currentTimeMillis() - this.temps;
    }

    /**
     * Passe au niveau suivant.
     */
    public void niveauSuivant()
    {
        this.niveauCourant++;
        this.chargerNiveau(this.niveauCourant);
    }
}