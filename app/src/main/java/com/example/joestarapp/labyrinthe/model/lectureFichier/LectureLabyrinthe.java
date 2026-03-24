package com.example.joestarapp.labyrinthe.model.lectureFichier;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Permet de charger un niveau depuis les assets.
 */

public abstract class LectureLabyrinthe
{

    /*-------------------------------------------*/
    /*               Méthodes statiques          */
    /*-------------------------------------------*/
    public static char[][] initNiveau(Context context,String nomFichier)
    {
        try
        {

            InputStream fichier = context.getAssets().open(nomFichier);
            Scanner sc = new Scanner(fichier);

            String ligne = "";

            List<char[]> lstLigne = new ArrayList<char[]>();

            while(sc.hasNextLine())
            {
                ligne = sc.nextLine();

                lstLigne.add(ligne.toCharArray());
            }

            sc.close();

            char[][] tabLabyrinthe = new char[lstLigne.size()][];

            for(int cpt = 0; cpt < lstLigne.size(); cpt ++)
            {
                tabLabyrinthe[cpt] = lstLigne.get(cpt);
            }

            return tabLabyrinthe;


        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
