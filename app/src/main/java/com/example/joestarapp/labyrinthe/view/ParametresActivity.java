package com.example.joestarapp.labyrinthe.view;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import com.example.joestarapp.R;
import androidx.appcompat.app.AppCompatActivity;
import com.example.joestarapp.labyrinthe.model.sauvegarde.GestionPreferences;

/**
 * Activité permettant de gérer les paramètres du jeu :
 * vibration et capteur de mouvement.
 */
public class ParametresActivity extends AppCompatActivity
{
    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private GestionPreferences preferences;
    private Switch             switchVibration;
    private Switch             switchCapteur;


    /*-------------------------------------------*/
    /*               Cycle de vie                */
    /*-------------------------------------------*/

    /**
     * Initialise l'activité et configure les paramètres.
     *
     * @param savedInstanceState État sauvegardé.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_parametres);

        this.preferences     = new GestionPreferences(this);
        this.switchVibration = this.findViewById(R.id.switchVibration);
        this.switchCapteur   = this.findViewById(R.id.switchCapteur);

        this.switchVibration.setChecked(this.preferences.isVibrationActive());
        this.switchCapteur.setChecked(this.preferences.isCapteurActif());

        this.switchVibration.setOnCheckedChangeListener((btn, actif) -> this.preferences.setVibrationActive(actif));
        this.switchCapteur.setOnCheckedChangeListener((btn, actif) -> this.preferences.setCapteurActif(actif));

        this.findViewById(R.id.boutonRetourParametres).setOnClickListener(v -> this.finish());
    }
}