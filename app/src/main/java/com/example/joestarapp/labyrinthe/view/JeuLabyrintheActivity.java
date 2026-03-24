package com.example.joestarapp.labyrinthe.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.joestarapp.LabyrintheActivity;
import com.example.joestarapp.R;
import com.example.joestarapp.labyrinthe.model.capteur.CapteurMouvement;
import com.example.joestarapp.labyrinthe.model.capteur.interfaces.EcouteDessin;
import com.example.joestarapp.labyrinthe.model.capteur.interfaces.IEcouteDeplacement;
import com.example.joestarapp.labyrinthe.viewmodel.MetierLabyrinthe;

import java.util.List;

/**
 * Activité principale du jeu : gère l'affichage, les interactions,
 * le capteur, le HUD, la progression et les transitions entre niveaux.
 */
public class JeuLabyrintheActivity extends AppCompatActivity
{
    /*-------------------------------------------*/
    /*                 Constantes                */
    /*-------------------------------------------*/

    public static final String CLE_NIVEAU         = "niveau_depart";
    public static final String CLE_NOMBRE_NIVEAUX = "nombre_niveaux";

    private static final long DELAI_SUIVI_MS = 120;


    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private MetierLabyrinthe metier;
    private CapteurMouvement capteur;
    private VueLabyrinthe    vue;

    private Button   boutonPause;
    private Button   boutonRetour;
    private TextView texteScore;
    private TextView texteNiveau;
    private TextView texteTemps;

    private boolean estEnPause    = false;
    private boolean niveauTermine = false;
    private boolean enSuiviChemin = false;
    private int     nombreNiveaux;

    private final Handler handler = new Handler(Looper.getMainLooper());


    /*-------------------------------------------*/
    /*               Cycle de vie                */
    /*-------------------------------------------*/

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_jeu_labyrinthe);

        int niveauDepart   = this.getIntent().getIntExtra(JeuLabyrintheActivity.CLE_NIVEAU, 1);
        this.nombreNiveaux = this.getIntent().getIntExtra(JeuLabyrintheActivity.CLE_NOMBRE_NIVEAUX, 5);
        this.metier        = new MetierLabyrinthe(this, niveauDepart);

        this.vue = this.findViewById(R.id.vueLabyrinthe);

        if (this.vue == null)
        {
            Toast.makeText(this, this.getString(R.string.erreur_vue), Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }

        this.vue.setNiveau(this.metier.getNiveau());
        this.vue.setJoueur(this.metier.getJoueur());

        this.texteScore  = this.findViewById(R.id.texteScore);
        this.texteNiveau = this.findViewById(R.id.texteNiveau);
        this.texteTemps  = this.findViewById(R.id.texteTemps);

        this.mettreAJourHUD();

        this.vue.setEcouteDessin(new EcouteDessin()
        {
            @Override
            public void onDebutDessin()
            {
                if (!JeuLabyrintheActivity.this.estEnPause)
                {
                    JeuLabyrintheActivity.this.mettrePauseInterne();
                }

                Toast.makeText(
                        JeuLabyrintheActivity.this,
                        getString(R.string.toast_dessin_debut),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onFinDessinValide()
            {
                Toast.makeText(
                        JeuLabyrintheActivity.this,
                        getString(R.string.toast_dessin_valide),
                        Toast.LENGTH_SHORT
                ).show();

                List<int[]> chemin = JeuLabyrintheActivity.this.vue.getCheminValide();

                if (chemin != null)
                {
                    JeuLabyrintheActivity.this.lancerSuiviChemin(chemin);
                }
            }

            @Override
            public void onFinDessinInvalide()
            {
                Toast.makeText(
                        JeuLabyrintheActivity.this,
                        getString(R.string.toast_dessin_invalide),
                        Toast.LENGTH_SHORT
                ).show();

                JeuLabyrintheActivity.this.reprendreInterne();
            }
        });

        this.capteur = new CapteurMouvement(this, new IEcouteDeplacement()
        {
            @Override
            public void deplacer(float x, float y)
            {
                if (JeuLabyrintheActivity.this.estEnPause
                        || JeuLabyrintheActivity.this.niveauTermine
                        || JeuLabyrintheActivity.this.enSuiviChemin)
                {
                    return;
                }

                JeuLabyrintheActivity.this.metier.gererDeplacementCapteur(x, y);
                JeuLabyrintheActivity.this.metier.mettreAJourPhysique();

                JeuLabyrintheActivity.this.runOnUiThread(() ->
                {
                    JeuLabyrintheActivity.this.vue.invalidate();
                    JeuLabyrintheActivity.this.mettreAJourHUD();

                    if (JeuLabyrintheActivity.this.metier.aGagne())
                    {
                        JeuLabyrintheActivity.this.niveauTermine = true;
                        JeuLabyrintheActivity.this.metier.enregistrerTemps();
                        JeuLabyrintheActivity.this.metier.sauvegarderVictoire();
                        JeuLabyrintheActivity.this.onNiveauReussi();
                    }
                });
            }
        });

        this.boutonPause = this.findViewById(R.id.boutonPause);
        this.boutonPause.setOnClickListener(v -> this.basculerPause());

        this.boutonRetour = this.findViewById(R.id.boutonRetour);
        this.boutonRetour.setOnClickListener(v -> this.retournerAuMenu());
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (!this.estEnPause && !this.niveauTermine && !this.enSuiviChemin)
        {
            this.capteur.demarrer();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.handler.removeCallbacksAndMessages(null);
        this.capteur.arreter();
    }


    /*-------------------------------------------*/
    /*               Autres Mehtodes             */
    /*-------------------------------------------*/

    private void lancerSuiviChemin(List<int[]> chemin)
    {
        this.enSuiviChemin = true;
        this.suivreEtape(chemin, 0);
    }

    private void suivreEtape(List<int[]> chemin, int index)
    {
        if (index >= chemin.size())
        {
            this.enSuiviChemin = false;
            this.vue.effacerChemin();

            if (this.metier.aGagne())
            {
                this.niveauTermine = true;
                this.metier.enregistrerTemps();
                this.metier.sauvegarderVictoire();
                this.onNiveauReussi();
            }
            else
            {
                this.reprendreInterne();
            }

            return;
        }

        this.handler.postDelayed(() ->
        {
            if (this.niveauTermine)
            {
                return;
            }

            int[] caseTarget = chemin.get(index);

            this.metier.getJoueur().setPosition(caseTarget[0] + 0.5, caseTarget[1] + 0.5);
            this.metier.getJoueur().stopX();
            this.metier.getJoueur().stopY();

            this.vue.invalidate();
            this.mettreAJourHUD();

            this.suivreEtape(chemin, index + 1);

        }, JeuLabyrintheActivity.DELAI_SUIVI_MS);
    }

    private void mettrePauseInterne()
    {
        this.estEnPause = true;
        this.capteur.arreter();
        this.boutonPause.setText(this.getString(R.string.bouton_reprendre));
    }

    private void reprendreInterne()
    {
        this.estEnPause = false;
        this.capteur.demarrer();
        this.boutonPause.setText(this.getString(R.string.bouton_pause));
    }

    private void basculerPause()
    {
        if (this.enSuiviChemin)
        {
            return;
        }

        this.estEnPause = !this.estEnPause;

        this.boutonPause.setText(
                this.estEnPause
                        ? this.getString(R.string.bouton_reprendre)
                        : this.getString(R.string.bouton_pause)
        );

        Toast.makeText(
                this,
                this.estEnPause
                        ? this.getString(R.string.toast_pause)
                        : this.getString(R.string.toast_reprise),
                Toast.LENGTH_SHORT
        ).show();

        if (this.estEnPause)
        {
            this.capteur.arreter();
        }
        else
        {
            this.capteur.demarrer();
        }
    }

    private void onNiveauReussi()
    {
        int points            = this.metier.getScore().getPoints();
        boolean dernierNiveau = this.metier.getNiveauCourant() >= this.nombreNiveaux;

        Toast.makeText(
                this,
                this.getString(R.string.toast_niveau_reussi) + " +" + points + " pts",
                Toast.LENGTH_SHORT
        ).show();

        if (dernierNiveau)
        {
            Toast.makeText(this, this.getString(R.string.toast_fin), Toast.LENGTH_LONG).show();
            this.retournerAuMenu();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.toast_niveau_reussi))
                .setMessage(this.getString(R.string.dialog_score) + " " + points + " pts")
                .setPositiveButton(this.getString(R.string.dialog_niveau_suivant), (d, w) ->
                {
                    this.metier.niveauSuivant();
                    this.vue.setNiveau(this.metier.getNiveau());
                    this.vue.setJoueur(this.metier.getJoueur());
                    this.vue.effacerChemin();
                    this.niveauTermine = false;
                    this.enSuiviChemin = false;
                    this.mettreAJourHUD();
                    this.reprendreInterne();

                    Toast.makeText(
                            this,
                            this.getString(R.string.hud_niveau_prefix) + this.metier.getNiveauCourant(),
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton(this.getString(R.string.dialog_menu), (d, w) -> this.retournerAuMenu())
                .setCancelable(false)
                .show();
    }

    private void retournerAuMenu()
    {
        this.handler.removeCallbacksAndMessages(null);

        Toast.makeText(
                this,
                this.getString(R.string.toast_retour_menu),
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(this, LabyrintheActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        this.startActivity(intent);
        this.finish();
    }

    private void mettreAJourHUD()
    {
        if (this.texteScore != null)
        {
            this.texteScore.setText(
                    this.getString(R.string.hud_score_prefix)
                            + this.metier.getScore().getPoints()
            );
        }

        if (this.texteNiveau != null)
        {
            this.texteNiveau.setText(
                    this.getString(R.string.hud_niveau_prefix)
                            + this.metier.getNiveauCourant()
            );
        }

        if (this.texteTemps != null)
        {
            long secondes = this.metier.getTempsEcouleMs() / 1000;

            this.texteTemps.setText(
                    String.format("%02d:%02d", secondes / 60, secondes % 60)
            );
        }
    }
}
