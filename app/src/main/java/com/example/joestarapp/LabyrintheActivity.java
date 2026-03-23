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

import com.example.joestarapp.labyrinthe.model.sauvegarde.GestionSauvegarde;
import com.example.joestarapp.labyrinthe.view.JeuLabyrintheActivity;

public class LabyrintheActivity extends AppCompatActivity
{
    // Nombre total de niveaux — changer uniquement cette constante pour en ajouter
    private static final int NOMBRE_NIVEAUX = 5;

    private Button           boutonQuitter;
    private Button           boutonParametres;
    private LinearLayout     conteneurNiveaux;
    private GestionSauvegarde sauvegarde;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labyrinthe);

        this.sauvegarde      = new GestionSauvegarde(this);
        this.conteneurNiveaux = findViewById(R.id.conteneurNiveaux);
        this.boutonParametres = findViewById(R.id.boutonParametres);
        this.boutonQuitter    = findViewById(R.id.boutonQuitter);

        boutonParametres.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.toast_parametres_bientot), Toast.LENGTH_SHORT).show());

        boutonQuitter.setOnClickListener(v -> finish());

        construireBoutonsNiveaux();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Rafraîchit les boutons à chaque retour (ex: après avoir terminé un niveau)
        construireBoutonsNiveaux();
    }

    /**
     * Génère dynamiquement les boutons de niveaux.
     * Modulaire : ajoute un fichier niveau6.txt et change NOMBRE_NIVEAUX = 6.
     */
    private void construireBoutonsNiveaux()
    {
        conteneurNiveaux.removeAllViews();

        int niveauDebloque = sauvegarde.getNiveauDebloque();

        for (int i = 1; i <= NOMBRE_NIVEAUX; i++)
        {
            final int numNiveau  = i;
            boolean   debloque   = (i <= niveauDebloque);
            boolean   reussi     = sauvegarde.estNiveauReussi(i);
            int       meilleurScore = sauvegarde.getMeilleurScore(i);

            Button bouton = new Button(this);

            // Texte du bouton
            String texte = getString(R.string.bouton_niveau_prefix) + i;
            if (reussi)   texte += "  ✓";
            if (!debloque) texte = "🔒  " + getString(R.string.bouton_niveau_prefix) + i;
            bouton.setText(texte);

            // Style selon l'état
            bouton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            bouton.setAllCaps(false);
            bouton.setEnabled(debloque);

            if (reussi)
                bouton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.niveau_reussi));
            else if (debloque)
                bouton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.niveau_debloque));
            else
                bouton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.niveau_verrouille));

            // Marges
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16);
            bouton.setLayoutParams(params);

            // Score si déjà réussi
            if (reussi && meilleurScore > 0)
            {
                TextView scoreView = new TextView(this);
                scoreView.setText(getString(R.string.label_meilleur_score) + " " + meilleurScore + " pts");
                scoreView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                scoreView.setTextSize(12f);
                scoreView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                LinearLayout.LayoutParams sparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                sparams.setMargins(0, -12, 0, 16);
                scoreView.setLayoutParams(sparams);

                bouton.setOnClickListener(v -> lancerNiveau(numNiveau));
                conteneurNiveaux.addView(bouton);
                conteneurNiveaux.addView(scoreView);
            }
            else
            {
                bouton.setOnClickListener(v -> {
                    if (debloque) lancerNiveau(numNiveau);
                    else Toast.makeText(this, getString(R.string.toast_niveau_verrouille), Toast.LENGTH_SHORT).show();
                });
                conteneurNiveaux.addView(bouton);
            }
        }
    }

    private void lancerNiveau(int numNiveau)
    {
        Intent intent = new Intent(this, JeuLabyrintheActivity.class);
        intent.putExtra(JeuLabyrintheActivity.CLE_NIVEAU, numNiveau);
        intent.putExtra(JeuLabyrintheActivity.CLE_NOMBRE_NIVEAUX, NOMBRE_NIVEAUX);
        startActivity(intent);
    }
}
