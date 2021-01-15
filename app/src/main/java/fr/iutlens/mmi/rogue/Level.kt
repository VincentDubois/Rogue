package fr.iutlens.mmi.rogue

import android.graphics.Canvas
import fr.iutlens.mmi.rogue.util.Coordinate
import fr.iutlens.mmi.rogue.util.SpriteSheet
import java.util.*

/**
 * Created by dubois on 27/12/2017.
 */
class Level(sizeX: Int, sizeY: Int, sprite_id: Int, sprite_bg_id: Int) {
    val coord: Coordinate
    val spriteSheet: SpriteSheet?
    val spriteSheetBG: SpriteSheet?
    private var ready: Boolean
    var start = 0
    private val data: IntArray
    private val content: MutableMap<Int, Sprite?>
    val zone: Array<Zone?>
    fun generate() {
        Zone.generateZone(this)
        val generator = RoomGenerator(this)
        generator.generate()
        ready = true
    }

    fun paint(canvas: Canvas, x: Int, y: Int, radius: Int) {
        if (!ready) return
        for (i in x - radius until x + radius) {
            for (j in y - radius until y + radius) {
                Tile.paint(canvas, this, i, j)
            }
        }
    }

    fun getContent(x: Int, y: Int): Sprite? {
        return content[coord.getNdx(x, y)]
    }

    fun getContent(ndx: Int): Sprite? {
        return content[ndx]
    }

    fun setContent(x: Int, y: Int, sprite: Sprite?) {
        content[coord.getNdx(x, y)] = sprite
        sprite!!.x = x
        sprite.y = y
    }

    fun setContent(ndx: Int, sprite: Sprite) {
        content[ndx] = sprite
        sprite.x = coord.getX(ndx)
        sprite.y = coord.getY(ndx)
    }

    fun removeContent(ndx: Int) {
        content.remove(ndx)
    }

    fun removeContent(x: Int, y: Int) {
        removeContent(coord.getNdx(x, y))
    }

    operator fun get(x: Int, y: Int): Int {
        val ndx = coord.getNdx(x, y)
        return if (ndx == -1) -1 else data[ndx]
    }

    operator fun get(ndx: Int): Int {
        return if (ndx == -1) -1 else data[ndx]
    }

    operator fun get(x: Int, y: Int, dir: Int): Int {
        val ndx = coord.getNext(x, y, dir)
        return if (ndx == -1) -1 else data[ndx]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        data[coord.getNdx(x, y)] = value
    }

    operator fun set(ndx: Int, tile: Int) {
        data[ndx] = tile
    }

    val sizeY: Int
        get() = coord.sizeY

    val sizeX: Int
        get() = coord.sizeX

    val tileWidth: Int
        get() = spriteSheet!!.w

    val tileHeight: Int
        get() = spriteSheet!!.h

    private fun fillRectangle(x0: Int, y0: Int, x1: Int, y1: Int, code: Int) {
        for (x in x0 until x1) for (y in y0 until y1) set(x, y, code)
    }

    private fun rectangle(x0: Int, y0: Int, x1: Int, y1: Int, code: Int) {
        for (x in x0..x1) {
            set(x, y0, code)
            set(x, y1, code)
        }
        for (y in y0..y1) {
            set(x0, y, code)
            set(x1, y, code)
        }
    }

    init {
        spriteSheet = SpriteSheet.Companion.get(sprite_id)
        spriteSheetBG = SpriteSheet.Companion.get(sprite_bg_id)
        coord = Coordinate(sizeX, sizeY)
        data = IntArray(sizeX * sizeY)
        content = HashMap()
        zone = arrayOfNulls(9)
        ready = false
    }
}