package fr.iutlens.mmi.rogue

import android.graphics.Canvas
import fr.iutlens.mmi.rogue.util.Coordinate
import fr.iutlens.mmi.rogue.util.SpriteSheet

/**
 * Created by dubois on 24/12/2019.
 */
class Hero(tileset: Int, id: Int, x: Int, y: Int) {
    private val spriteSheet: SpriteSheet? = SpriteSheet.get(tileset)
    private val id: Int = id
    var x: Int
        private set
    var y: Int
        private set

    fun paint(canvas: Canvas) {
        spriteSheet!!.paint(canvas, id, x * spriteSheet.w.toFloat(), y * spriteSheet.h.toFloat(), 0xffc1b5)
    }

    val mobility: Int
        get() = Tile.F_WALK or Tile.F_SWIM

    fun move(dir: Int) {
        x += Coordinate.dir_coord.get(dir).get(0)
        y += Coordinate.dir_coord.get(dir).get(1)
    }

    init {
        this.x = x
        this.y = y
    }
}