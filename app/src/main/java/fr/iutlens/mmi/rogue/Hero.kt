package fr.iutlens.mmi.rogue

import android.graphics.Canvas
import android.graphics.Paint
import fr.iutlens.mmi.rogue.util.Coordinate
import fr.iutlens.mmi.rogue.util.SpriteSheet

/**
 * Created by dubois on 24/12/2019.
 */
class Hero(tileset: Int, private val id: Int, var x: Int, var y: Int) {
    private val spriteSheet: SpriteSheet? = SpriteSheet[tileset]

    fun paint(canvas: Canvas) {
        spriteSheet!!.paint(canvas, id, x * spriteSheet.w.toFloat(), y * spriteSheet.h.toFloat(), 0xffc1b5)
    }

    val mobility: Int
        get() = Tile.F_WALK or Tile.F_SWIM

    fun move(dir: Int) {
        x += Coordinate.dir_coord[dir][0]
        y += Coordinate.dir_coord[dir][1]
    }
}