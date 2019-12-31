package fr.iutlens.mmi.rogue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import fr.iutlens.mmi.rogue.util.Coordinate;
import fr.iutlens.mmi.rogue.util.SpriteSheet;


public class GameView extends View implements View.OnTouchListener {
    public static final int MIN_VISIBLE_TILES = 14;
    private Level level;
    private Hero hero;

    private Matrix transform,inverse;

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Initialisation de la vue
     *
     * Tous les constructeurs (au-dessus) renvoient ici.
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {

        // Chargement des feuilles de sprites
        SpriteSheet.register(R.drawable.tileset,  16,16,this.getContext());
        SpriteSheet.register(R.drawable.tilesetbg,16,16,this.getContext());

        level = new Level(120,120,R.drawable.tileset,R.drawable.tilesetbg);
        transform = new Matrix();
        inverse = new Matrix();

        if (isInEditMode()){
            generate();
        }

        this.setOnTouchListener(this);
    }


    public void generate(){
        level.generate();
        final int start = level.getStart();
        hero  = new Hero(R.drawable.tileset,1, level.coord.getX(start),level.coord.getY(start));
    }

    /**
     * Méthode appelée (automatiquement) pour afficher la vue
     * C'est là que l'on dessine le décor et les sprites
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // On met une couleur de fond
        canvas.drawColor(0xff000000);

        if (hero ==null) return;
        // On choisit la transformation à appliquer à la vue i.e. la position
        // de la "camera"
        setCamera(canvas);

        // Dessin des différents éléments
        level.paint(canvas, hero.getX(),hero.getY(),MIN_VISIBLE_TILES);
        hero.paint(canvas);

    }

    private void setCamera(Canvas canvas) {

        // On calcul le facteur de zoom nécessaire pour afficher au moins 7 tiles
        // dans chaque direction
        float tiles_x = (1.0f*getWidth())/ level.getTileWidth();
        float tiles_y =  (1.0f*getHeight())/ level.getTileHeight();
        float min_tiles = Math.min(tiles_x,tiles_y);
        float scale = (min_tiles)/ MIN_VISIBLE_TILES;

        // La suite de transfomations est à interpréter "à l'envers"

        // On termine par un centrage de l'origine (le héro donc) dans la fenêtre
        transform.setTranslate(getWidth()/2,getHeight()/2);

        // On mets à l'échelle calculée au dessus
        transform.preScale(scale, scale);

        // On centre sur la position actuelle du héro (qui se retrouve en 0,0 )
        transform.preTranslate(-(hero.getX()+0.5f) * level.getTileWidth(),-(hero.getY()+0.5f) * level.getTileHeight());

        // Calcul de l'inverse, utilisé pour retrouver les coordonnées dans le jeu d'un clic
        transform.invert(inverse);

        // On applique les transformations
        canvas.concat(transform);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()== MotionEvent.ACTION_UP){
            float[] coord = {event.getX(),event.getY()};
            inverse.mapPoints(coord);
            moveToward(coord[0],coord[1]);
        }
        return true;
    }

    private void moveToward(float x, float y) {

        //Calcul des coordonnées dans la grille, par rapport au héro
        float dx = x/level.getTileWidth()-hero.getX();
        float dy = y/level.getTileHeight()-hero.getY();

        // Recherche de la direction à utiliser en calculant le produit scalaire :
        // On se déplacera dans le sens du vecteur avec le produit scalaire le plus grand
        int dir =-1;
        float ps = 0;
        for(int i = 0; i< 4; ++i){
            float ps_i = dx* Coordinate.dir_coord[i][0]+ dy*Coordinate.dir_coord[i][1];
            if (ps_i>ps){
                ps = ps_i;
                dir = i;
            }
        }

        // On demande le déplacement, si possible
        if (dir != -1 && canMove(dir)) move(dir);

    }

    private void move(int dir) {
        // Déplacement proprement dit
        hero.move(dir);

        // On récupère le contenu de la case, et on applique l'effet si on trouve qq chose
        final Sprite sprite = level.getContent(hero.getX(), hero.getY());
        if (sprite != null) sprite.effect(level,hero);

        // Le jeu a besoin d'être affiché à nouveau, puisqu'on a bougé
        invalidate();
    }

    private boolean canMove(int dir) {

        // On cherche ce qu'il y a dans la direction ou on veut bouger
        int ndx = level.coord.getNext(hero.getX(),hero.getY(),dir);
        int id = level.get(ndx);
        // Si on est en dehors du plateau, le mouvement est impossible
        if (id == -1) return false;

        // Sinon, on regarde la case
        Tile tile = Tile.get(id);
        if (tile == null) return false; // Si il n'y a pas de case, il y a un (gros) problème

        // On regarde si le mode de déplacement du héro est compatible avec celui de la case
        if (!tile.hasOneFlag(hero.getMobility())) return false;

        // On regarde si le contenu de la case est bloquant
        Sprite sprite = level.getContent(ndx);
        return !(sprite != null && sprite.block());
    }
}
