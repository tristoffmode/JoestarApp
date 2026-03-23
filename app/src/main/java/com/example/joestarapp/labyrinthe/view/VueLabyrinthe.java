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

public class VueLabyrinthe extends View
{
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

    private final RectF rectTemp = new RectF();


    private final List<int[]> chemin        = new ArrayList<>();
    private boolean           cheminValide  = false;
    private boolean           enDessin      = false;

    public interface EcouteDessin
    {
        void onDebutDessin();       // demande la pause
        void onFinDessinValide();   // chemin valide → suivre le chemin
        void onFinDessinInvalide(); // chemin invalide → reprendre
    }

    private EcouteDessin ecouteDessin;

    public void setEcouteDessin(EcouteDessin e) { this.ecouteDessin = e; }

    public VueLabyrinthe(Context context)
    {
        this(context,null);
    }

    public VueLabyrinthe(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.init();
    }

    private void init()
    {
        paintMur = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMur.setColor(Color.parseColor("#1E2A3A"));
        paintMur.setStyle(Paint.Style.FILL);

        paintBordureMur = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBordureMur.setColor(Color.parseColor("#2E3D52"));
        paintBordureMur.setStyle(Paint.Style.STROKE);
        paintBordureMur.setStrokeWidth(1f);

        paintSol = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSol.setColor(Color.parseColor("#F0EAD6"));
        paintSol.setStyle(Paint.Style.FILL);

        paintJoueur = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintJoueur.setColor(Color.parseColor("#4A90E2"));
        paintJoueur.setStyle(Paint.Style.FILL);
        paintJoueur.setShadowLayer(6f, 0f, 0f, Color.parseColor("#804A90E2"));

        paintArrive = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArrive.setColor(Color.parseColor("#2ECC71"));
        paintArrive.setStyle(Paint.Style.FILL);

        paintDepart = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDepart.setColor(Color.parseColor("#E67E22"));
        paintDepart.setStyle(Paint.Style.FILL);
        paintDepart.setAlpha(120);

        // Jaune semi-transparent pendant le tracé
        paintCheminDessin = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCheminDessin.setColor(Color.parseColor("#FFEB3B"));
        paintCheminDessin.setStyle(Paint.Style.FILL);
        paintCheminDessin.setAlpha(160);

        // Or plein quand le chemin est validé
        paintCheminValide = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCheminValide.setColor(Color.parseColor("#FFC107"));
        paintCheminValide.setStyle(Paint.Style.FILL);
        paintCheminValide.setAlpha(200);
    }

    public void setNiveau(Niveau niveau) { this.niveau = niveau; effacerChemin(); invalidate(); }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }


    public void effacerChemin()
    {
        this.chemin.clear();
        this.cheminValide = false;
        this.enDessin     = false;
        this.invalidate();
    }

    public List<int[]> getCheminValide()
    {
        return this.cheminValide ? new ArrayList<>(this.chemin) : null;
    }

    // ------------------------------------------------------------------ touch
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (this.niveau == null || this.joueur == null) return false;

        float largeurCase = (float) this.getWidth()  / this.niveau.getLargeur();
        float hauteurCase = (float) this.getHeight() / this.niveau.getHauteur();

        int colTouche   = (int)(event.getX() / largeurCase);
        int ligneTouche = (int)(event.getY() / hauteurCase);

        // Clamp pour rester dans la grille
        colTouche   = Math.max(0, Math.min(colTouche,   this.niveau.getLargeur()  - 1));
        ligneTouche = Math.max(0, Math.min(ligneTouche, this.niveau.getHauteur() - 1));

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                this.onDoigtPose(colTouche, ligneTouche);
                break;

            case MotionEvent.ACTION_MOVE:
                if (this.enDessin) this.onDoigtBouge(colTouche, ligneTouche);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (this.enDessin) this.onDoigtRelache();
                break;
        }
        return true;
    }

    private void onDoigtPose(int col, int ligne)
    {
        int casJoueurCol   = (int) Math.round(this.joueur.getPosX());
        int casJoueurLigne = (int) Math.round(this.joueur.getPosY());

        if (col != casJoueurCol || ligne != casJoueurLigne) return;

        if (this.niveau.estMur(col, ligne)) return;

        this.enDessin     = true;
        this.cheminValide = false;
        this.chemin.clear();
        this.chemin.add(new int[]{col, ligne});

        if (this.ecouteDessin != null) this.ecouteDessin.onDebutDessin();
        this.invalidate();
    }

    private void onDoigtBouge(int col, int ligne)
    {
        if (this.niveau.estMur(col, ligne)) return;

        if (!this.chemin.isEmpty())
        {
            int[] derniere = this.chemin.get(this.chemin.size() - 1);
            if (derniere[0] == col && derniere[1] == ligne) return;
        }

        if (this.chemin.size() >= 2)
        {
            int[] avantDerniere = this.chemin.get(this.chemin.size() - 2);
            if (avantDerniere[0] == col && avantDerniere[1] == ligne)
            {
                this.chemin.remove(this.chemin.size() - 1);
                this.invalidate();
                return;
            }
        }

        this.chemin.add(new int[]{col, ligne});
        this.invalidate();
    }

    private void onDoigtRelache()
    {
        this.enDessin = false;

        if (this.chemin.isEmpty()) return;

        // Vérifier si la dernière case du chemin est l'arrivée
        int[] derniere = this.chemin.get(this.chemin.size() - 1);
        boolean attentArrivee = derniere[0] == this.niveau.getPosXArriver()
                && derniere[1] == this.niveau.getPosYArriver();

        if (attentArrivee)
        {
            this.cheminValide = true;
            if (this.ecouteDessin != null) this.ecouteDessin.onFinDessinValide();
        }
        else
        {
            this.cheminValide = false;
            this.chemin.clear();
            if (this.ecouteDessin != null) this.ecouteDessin.onFinDessinInvalide();
        }

        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (this.niveau == null || this.joueur == null) return;

        final float largeurCase = (float) getWidth()  / this.niveau.getLargeur();
        final float hauteurCase = (float) getHeight() / this.niveau.getHauteur();

        // 1. Grille
        for (int ligne = 0; ligne < this.niveau.getHauteur(); ligne++)
        {
            for (int col = 0; col < this.niveau.getLargeur(); col++)
            {
                float g = col   * largeurCase;
                float h = ligne * hauteurCase;
                this.rectTemp.set(g, h, g + largeurCase, h + hauteurCase);

                if (this.niveau.estMur(col, ligne))
                {
                    canvas.drawRect(this.rectTemp, paintMur);
                    canvas.drawRect(this.rectTemp, paintBordureMur);
                }
                else
                {
                    canvas.drawRect(this.rectTemp, this.paintSol);
                }
            }
        }

        // 2. Case départ
        float dpx = this.niveau.getPosXJoueur() * largeurCase;
        float dpy = this.niveau.getPosYJoueur() * hauteurCase;
        this.rectTemp.set(dpx, dpy, dpx + largeurCase, dpy + hauteurCase);
        canvas.drawRect(this.rectTemp, this.paintDepart);

        // 3. Chemin (dessin en cours ou validé)
        if (!this.chemin.isEmpty())
        {
            Paint p = this.cheminValide ? this.paintCheminValide : this.paintCheminDessin;
            for (int[] caseChemin : this.chemin)
            {
                float cx = caseChemin[0] * largeurCase;
                float cy = caseChemin[1] * hauteurCase;
                // Légèrement inséré dans la case pour bien voir les bords
                this.rectTemp.set(cx + 2, cy + 2, cx + largeurCase - 2, cy + hauteurCase - 2);
                canvas.drawRect(this.rectTemp, p);
            }
        }

        // 4. Arrivée (dessinée après le chemin pour rester visible)
        float ax = this.niveau.getPosXArriver() * largeurCase + largeurCase / 2f;
        float ay = this.niveau.getPosYArriver() * hauteurCase + hauteurCase / 2f;
        canvas.drawCircle(ax, ay, Math.min(largeurCase, hauteurCase) / 2.8f, this.paintArrive);

        // 5. Boule joueur
        float px    = (float)(this.joueur.getPosX() * largeurCase + largeurCase / 2.0);
        float py    = (float)(this.joueur.getPosY() * hauteurCase + hauteurCase / 2.0);
        float rayon = (float)(MetierLabyrinthe.RAYON_BOULE * Math.min(largeurCase, hauteurCase));
        canvas.drawCircle(px, py, rayon, this.paintJoueur);
    }
}
