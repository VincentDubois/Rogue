package fr.iutlens.mmi.rogue

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import fr.iutlens.mmi.rogue.util.Coordinate
import fr.iutlens.mmi.rogue.util.SpriteSheet

class GameView : View, OnTouchListener {
    private lateinit var level: Level
    private lateinit var hero: Hero
    private var transform = Matrix()
    private var inverse = Matrix()

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    /**
     * Initialisation de la vue
     *
     * Tous les constructeurs (au-dessus) renvoient ici.
     *
     * @param attrs
     * @param defStyle
     */
    private fun init(attrs: AttributeSet?, defStyle: Int) {

        // Chargement des feuilles de sprites
        SpriteSheet.Companion.register(R.drawable.tileset, 16, 16, this.context)
        SpriteSheet.Companion.register(R.drawable.tilesetbg, 16, 16, this.context)
        level = Level(120, 120, R.drawable.tileset, R.drawable.tilesetbg)
        transform = Matrix()
        inverse = Matrix()
        if (isInEditMode) {
            generate()
        }
        setOnTouchListener(this)
    }

    fun generate() {
        level.generate()
        val start = level.start
        hero = Hero(R.drawable.tileset, 1, level.coord.getX(start), level.coord.getY(start))
    }

    /**
     * Méthode appelée (automatiquement) pour afficher la vue
     * C'est là que l'on dessine le décor et les sprites
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // On met une couleur de fond
        canvas.drawColor(-0x1000000)

        if (!this::hero.isInitialized) return



        // On choisit la transformation à appliquer à la vue i.e. la position
        // de la "camera"
        setCamera(canvas)

        // Dessin des différents éléments
        level.paint(canvas, hero.x, hero.y, MIN_VISIBLE_TILES)
        hero.paint(canvas)

    }

    private fun setCamera(canvas: Canvas) {

        // On calcul le facteur de zoom nécessaire pour afficher au moins 7 tiles
        // dans chaque direction
        val tiles_x = 1.0f * width / level.tileWidth
        val tiles_y = 1.0f * height / level.tileHeight
        val min_tiles = Math.min(tiles_x, tiles_y)
        val scale = min_tiles / MIN_VISIBLE_TILES

        // La suite de transfomations est à interpréter "à l'envers"

        // On termine par un centrage de l'origine (le héro donc) dans la fenêtre
        transform.setTranslate(width / 2.toFloat(), height / 2.toFloat())

        // On mets à l'échelle calculée au dessus
        transform.preScale(scale, scale)

        // On centre sur la position actuelle du héro (qui se retrouve en 0,0 )
        transform.preTranslate(-(hero.x + 0.5f) * level.tileWidth, -(hero.y + 0.5f) * level.tileHeight)

        // Calcul de l'inverse, utilisé pour retrouver les coordonnées dans le jeu d'un clic
        transform.invert(inverse)

        // On applique les transformations
        canvas.concat(transform)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val coord = floatArrayOf(event.x, event.y)
            inverse!!.mapPoints(coord)
            moveToward(coord[0], coord[1])
        }
        return true
    }

    private fun moveToward(x: Float, y: Float) {

        //Calcul des coordonnées dans la grille, par rapport au héro
        val dx = x / level.tileWidth - hero.x
        val dy = y / level.tileHeight - hero.y

        // Recherche de la direction à utiliser en calculant le produit scalaire :
        // On se déplacera dans le sens du vecteur avec le produit scalaire le plus grand
        var dir = -1
        var ps = 0f
        for (i in 0..3) {
            val ps_i: Float = dx * Coordinate.dir_coord[i][0] + dy * Coordinate.dir_coord[i][1]
            if (ps_i > ps) {
                ps = ps_i
                dir = i
            }
        }

        // On demande le déplacement, si possible
        if (dir != -1 && canMove(dir)) move(dir)
    }

    private fun move(dir: Int) {
        // Déplacement proprement dit
        hero!!.move(dir)

        // On récupère le contenu de la case, et on applique l'effet si on trouve qq chose
        val sprite = level.getContent(hero.x, hero.y)
        sprite?.effect(level, hero)

        // Le jeu a besoin d'être affiché à nouveau, puisqu'on a bougé
        invalidate()
    }

    private fun canMove(dir: Int): Boolean {

        // On cherche ce qu'il y a dans la direction ou on veut bouger
        val ndx = level.coord.getNext(hero.x, hero.y, dir)
        val id = level[ndx]
        // Si on est en dehors du plateau, le mouvement est impossible
        if (id == -1) return false

        // Sinon, on regarde la case
        val tile: Tile = Tile[id] ?: return false
        // Si il n'y a pas de case, il y a un (gros) problème

        // On regarde si le mode de déplacement du héro est compatible avec celui de la case
        if (!tile.hasOneFlag(hero.mobility)) return false

        // On regarde si le contenu de la case est bloquant
        val sprite = level.getContent(ndx)
        return !(sprite != null && sprite.block())
    }

    companion object {
        const val MIN_VISIBLE_TILES = 14
    }
}