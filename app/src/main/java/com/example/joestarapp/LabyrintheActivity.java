package com.example.joestarapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.joestarapp.labyrinthe.view.ParametresActivity;
import com.example.joestarapp.labyrinthe.model.sauvegarde.GestionSauvegarde;
import com.example.joestarapp.labyrinthe.view.JeuLabyrintheActivity;

/**
 * Activité principale du mode Labyrinthe.
 * Permet de choisir un niveau, accéder aux paramètres ou quitter.
 */
public class LabyrintheActivity extends AppCompatActivity
{
    /*-------------------------------------------*/
    /*                 Constantes                */
    /*-------------------------------------------*/

    private static final int LabyrintheActivity_NOMBRE_NIVEAUX = 7;


    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private Button            boutonQuitter;
    private Button            boutonParametres;
    private LinearLayout      conteneurNiveaux;
    private GestionSauvegarde sauvegarde;


    /*-------------------------------------------*/
    /*                Cycle de vie               */
    /*-------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_labyrinthe);

        this.sauvegarde       = new GestionSauvegarde(this);
        this.conteneurNiveaux = this.findViewById(R.id.conteneurNiveaux);
        this.boutonParametres = this.findViewById(R.id.boutonParametres);
        this.boutonQuitter    = this.findViewById(R.id.boutonQuitter);

        this.boutonParametres.setOnClickListener(v ->
        {
            this.startActivity(new Intent(this, ParametresActivity.class));
        });

        this.boutonQuitter.setOnClickListener(v ->
        {
            this.finish();
        });

        this.construireBoutonsNiveaux();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Rafraîchir les boutons (ex : retour après un niveau)
        this.construireBoutonsNiveaux();
    }


    /*-------------------------------------------*/
    /*               Méthodes privées            */
    /*-------------------------------------------*/

    private void construireBoutonsNiveaux()
    {
        this.conteneurNiveaux.removeAllViews();

        int niveauDebloque = this.sauvegarde.getNiveauDebloque();

        for (int i = 1; i <= LabyrintheActivity_NOMBRE_NIVEAUX; i++)
        {
            final int numNiveau = i;

            boolean debloque     = (i <= niveauDebloque);
            boolean reussi       = this.sauvegarde.estNiveauReussi(i);
            int     meilleurScore = this.sauvegarde.getMeilleurScore(i);

            Button bouton = new Button(this);

            /*-------------------------------------------*/
            /*                Texte bouton               */
            /*-------------------------------------------*/

            String texte = this.getString(R.string.bouton_niveau_prefix) + i;

            if (reussi)
            {
                texte += "  Réussi";
            }

            if (!debloque)
            {
                texte = this.getString(R.string.bouton_niveau_prefix) + i + " est verrouillé";
            }

            bouton.setText(texte);


            /*-------------------------------------------*/
            /*                  Style                   */
            /*-------------------------------------------*/

            bouton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            bouton.setAllCaps(false);
            bouton.setEnabled(debloque);

            if (reussi)
            {
                bouton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.niveau_reussi));
            }
            else if (debloque)
            {
                bouton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.niveau_debloque));
            }
            else
            {
                bouton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.niveau_verrouille));
            }


            /*-------------------------------------------*/
            /*                  Layout                  */
            /*-------------------------------------------*/

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 16);
            bouton.setLayoutParams(params);


            /*-------------------------------------------*/
            /*           Gestion du score affiché       */
            /*-------------------------------------------*/

            if (reussi && meilleurScore > 0)
            {
                TextView scoreView = new TextView(this);

                scoreView.setText(
                        this.getString(R.string.label_meilleur_score) + " " + meilleurScore + " pts"
                );

                scoreView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                scoreView.setTextSize(12f);
                scoreView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                LinearLayout.LayoutParams sparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                sparams.setMargins(0, -12, 0, 16);
                scoreView.setLayoutParams(sparams);

                bouton.setOnClickListener(v ->
                {
                    this.lancerNiveau(numNiveau);
                });

                this.conteneurNiveaux.addView(bouton);
                this.conteneurNiveaux.addView(scoreView);
            }
            else
            {
                bouton.setOnClickListener(v ->
                {
                    if (debloque)
                    {
                        this.lancerNiveau(numNiveau);
                    }
                    else
                    {
                        Toast.makeText(
                                this,
                                this.getString(R.string.toast_niveau_verrouille),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

                this.conteneurNiveaux.addView(bouton);
            }
        }
    }


    private void lancerNiveau(int numNiveau)
    {
        Intent intent = new Intent(this, JeuLabyrintheActivity.class);

        intent.putExtra(JeuLabyrintheActivity.CLE_NIVEAU, numNiveau);
        intent.putExtra(JeuLabyrintheActivity.CLE_NOMBRE_NIVEAUX, LabyrintheActivity_NOMBRE_NIVEAUX);

        this.startActivity(intent);
    }
}