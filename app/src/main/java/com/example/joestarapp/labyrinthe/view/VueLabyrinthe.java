package com.example.joestarapp.labyrinthe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.joestarapp.labyrinthe.model.capteur.interfaces.EcouteDessin;
import com.example.joestarapp.labyrinthe.model.entite.Joueur;
import com.example.joestarapp.labyrinthe.model.entite.Niveau;
import com.example.joestarapp.labyrinthe.viewmodel.MetierLabyrinthe;

import java.util.ArrayList;
import java.util.List;

/**
 * Vue personnalisée permettant d'afficher le labyrinthe,
 * le joueur, l'arrivée et de gérer le dessin du chemin tactile.
 */
public class VueLabyrinthe extends View
{
    /*-------------------------------------------*/
    /*             Attributs d'instance          */
    /*-------------------------------------------*/

    private Niveau niveau;
    private Joueur joueur;

    private Paint paintMur;
    private Paint paintSol;
    private Paint paintJoueur;
    private Paint paintArrive;
    private Paint paintBordureMur;
    private Paint paintDepart;
    private Paint paintCheminDessin;
    private Paint paintCheminValide;

    private RectF rectTemp;

    private List<int[]> chemin;
    private boolean     cheminValide;
    private boolean     enDessin;

    private EcouteDessin ecouteDessin;


    /*-------------------------------------------*/
    /*                Constructeurs              */
    /*-------------------------------------------*/

    public VueLabyrinthe(Context context)
    {
        this(context, null);
    }

    public VueLabyrinthe(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.init();
    }


    /*-------------------------------------------*/
    /*                Initialisation             */
    /*-------------------------------------------*/

    private void init()
    {
        this.rectTemp     = new RectF();
        this.chemin       = new ArrayList<>();
        this.cheminValide = false;
        this.enDessin     = false;

        this.paintMur = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintMur.setColor(Color.parseColor("#1E2A3A"));
        this.paintMur.setStyle(Paint.Style.FILL);

        this.paintBordureMur = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintBordureMur.setColor(Color.parseColor("#2E3D52"));
        this.paintBordureMur.setStyle(Paint.Style.STROKE);
        this.paintBordureMur.setStrokeWidth(1f);

        this.paintSol = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintSol.setColor(Color.parseColor("#F0EAD6"));

        this.paintJoueur = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintJoueur.setColor(Color.parseColor("#4A90E2"));
        this.paintJoueur.setShadowLayer(6f, 0f, 0f, Color.parseColor("#804A90E2"));

        this.paintArrive = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintArrive.setColor(Color.parseColor("#2ECC71"));

        this.paintDepart = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintDepart.setColor(Color.parseColor("#E67E22"));
        this.paintDepart.setAlpha(120);

        this.paintCheminDessin = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintCheminDessin.setColor(Color.parseColor("#FFEB3B"));
        this.paintCheminDessin.setAlpha(160);

        this.paintCheminValide = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintCheminValide.setColor(Color.parseColor("#FFC107"));
        this.paintCheminValide.setAlpha(200);
    }


    /*-------------------------------------------*/
    /*                 Accesseurs                */
    /*-------------------------------------------*/

    public void setEcouteDessin(EcouteDessin e)
    {
        this.ecouteDessin = e;
    }

    public void setNiveau(Niveau niveau)
    {
        this.niveau = niveau;
        this.effacerChemin();
        this.invalidate();
    }

    public void setJoueur(Joueur joueur)
    {
        this.joueur = joueur;
    }

    public List<int[]> getCheminValide()
    {
        if (this.cheminValide)
        {
            return new ArrayList<>(this.chemin);
        }

        return null;
    }


    /*-------------------------------------------*/
    /*               Gestion chemin              */
    /*-------------------------------------------*/

    public void effacerChemin()
    {
        this.chemin.clear();
        this.cheminValide = false;
        this.enDessin     = false;
        this.invalidate();
    }


    /*-------------------------------------------*/
    /*              Gestion tactile              */
    /*-------------------------------------------*/

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (this.niveau == null || this.joueur == null)
        {
            return false;
        }

        float largeurCase = (float) this.getWidth()  / this.niveau.getLargeur();
        float hauteurCase = (float) this.getHeight() / this.niveau.getHauteur();

        int col   = Math.max(0, Math.min((int)(event.getX() / largeurCase), this.niveau.getLargeur() - 1));
        int ligne = Math.max(0, Math.min((int)(event.getY() / hauteurCase), this.niveau.getHauteur() - 1));

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                this.onDoigtPose(col, ligne);
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (this.enDessin)
                {
                    this.onDoigtBouge(col, ligne);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                if (this.enDessin)
                {
                    this.onDoigtRelache();
                }
                break;
            }
        }

        return this.enDessin || this.cheminValide;
    }


    /*-------------------------------------------*/
    /*             Gestion du dessin             */
    /*-------------------------------------------*/

    private void onDoigtPose(int col, int ligne)
    {
        int joueurCol   = (int) Math.floor(this.joueur.getPosX());
        int joueurLigne = (int) Math.floor(this.joueur.getPosY());

        if (col != joueurCol || ligne != joueurLigne)
        {
            return;
        }

        if (this.niveau.estMur(col, ligne))
        {
            return;
        }

        this.enDessin     = true;
        this.cheminValide = false;
        this.chemin.clear();
        this.chemin.add(new int[]{col, ligne});

        if (this.ecouteDessin != null)
        {
            this.ecouteDessin.onDebutDessin();
        }

        this.invalidate();
    }

    private void onDoigtBouge(int col, int ligne)
    {
        if (this.niveau.estMur(col, ligne))
        {
            return;
        }

        if (!this.chemin.isEmpty())
        {
            int[] derniere = this.chemin.get(this.chemin.size() - 1);

            if (derniere[0] == col && derniere[1] == ligne)
            {
                return;
            }

            if (this.chemin.size() >= 2)
            {
                int[] avant = this.chemin.get(this.chemin.size() - 2);

                if (avant[0] == col && avant[1] == ligne)
                {
                    this.chemin.remove(this.chemin.size() - 1);
                    this.invalidate();
                    return;
                }
            }

            if (Math.abs(col - derniere[0]) + Math.abs(ligne - derniere[1]) != 1)
            {
                return;
            }
        }

        this.chemin.add(new int[]{col, ligne});
        this.invalidate();
    }

    private void onDoigtRelache()
    {
        this.enDessin = false;

        if (this.chemin.isEmpty())
        {
            return;
        }

        int[] derniere = this.chemin.get(this.chemin.size() - 1);

        boolean atteint =
                derniere[0] == this.niveau.getPosXArriver() &&
                        derniere[1] == this.niveau.getPosYArriver();

        if (atteint)
        {
            this.cheminValide = true;

            if (this.ecouteDessin != null)
            {
                this.ecouteDessin.onFinDessinValide();
            }
        }
        else
        {
            this.chemin.clear();

            if (this.ecouteDessin != null)
            {
                this.ecouteDessin.onFinDessinInvalide();
            }
        }

        this.invalidate();
    }


    /*-------------------------------------------*/
    /*                 Dessin                    */
    /*-------------------------------------------*/

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (this.niveau == null || this.joueur == null)
        {
            return;
        }

        float largeurCase = (float) this.getWidth()  / this.niveau.getLargeur();
        float hauteurCase = (float) this.getHeight() / this.niveau.getHauteur();

        for (int y = 0; y < this.niveau.getHauteur(); y++)
        {
            for (int x = 0; x < this.niveau.getLargeur(); x++)
            {
                float g = x * largeurCase;
                float h = y * hauteurCase;

                this.rectTemp.set(g, h, g + largeurCase, h + hauteurCase);

                if (this.niveau.estMur(x, y))
                {
                    canvas.drawRect(this.rectTemp, this.paintMur);
                    canvas.drawRect(this.rectTemp, this.paintBordureMur);
                }
                else
                {
                    canvas.drawRect(this.rectTemp, this.paintSol);
                }
            }
        }

        // Départ
        float dpx = this.niveau.getPosXJoueur() * largeurCase;
        float dpy = this.niveau.getPosYJoueur() * hauteurCase;

        this.rectTemp.set(dpx, dpy, dpx + largeurCase, dpy + hauteurCase);
        canvas.drawRect(this.rectTemp, this.paintDepart);

        // Chemin
        if (!this.chemin.isEmpty())
        {
            Paint p = this.cheminValide ? this.paintCheminValide : this.paintCheminDessin;

            for (int[] c : this.chemin)
            {
                float cx = c[0] * largeurCase;
                float cy = c[1] * hauteurCase;

                this.rectTemp.set(cx + 2, cy + 2, cx + largeurCase - 2, cy + hauteurCase - 2);
                canvas.drawRect(this.rectTemp, p);
            }
        }

        // Arrivée
        float ax = (this.niveau.getPosXArriver() + 0.5f) * largeurCase;
        float ay = (this.niveau.getPosYArriver() + 0.5f) * hauteurCase;

        canvas.drawCircle(ax, ay, Math.min(largeurCase, hauteurCase) / 2.8f, this.paintArrive);

        // Joueur
        float px = (float)(this.joueur.getPosX() * largeurCase);
        float py = (float)(this.joueur.getPosY() * hauteurCase);

        float rayon = (float)(MetierLabyrinthe.RAYON_BOULE * Math.min(largeurCase, hauteurCase));

        canvas.drawCircle(px, py, rayon, this.paintJoueur);
    }
}