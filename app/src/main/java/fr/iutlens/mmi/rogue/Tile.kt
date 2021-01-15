package fr.iutlens.mmi.rogue

import android.graphics.Canvas
import java.util.*

/**
 * Created by dubois on 22/12/2019.
 */
internal class Tile @JvmOverloads constructor(private val color: Int, val code: Int, val flag: Int = 0) {
    companion object {
        private val map = HashMap<Int, Tile>()

        // Constantes pour le terrain
        const val WALL = 1
        const val SAND = 2
        const val WATER = 3
        const val LAVA = 4
        const val GRASS = 5
        const val PAVEMENT = 6
        const val ROCK = 7
        const val EARTH = 8

        // Constantes pour les objets
        const val FOOD = 9
        const val DOOR = 10
        const val TABLE = 11
        const val BED = 12
        const val BUFFET = 13
        const val CHAIR = 14
        const val COFFER = 15
        const val STAIRS_UP = 16
        const val STAIRS_DOWN = 17
        const val VASE = 18

        //Constantes pour les monstres
        const val SPECTER = 19
        const val HUMAN = 20
        const val SPIDER = 21
        const val ELF = 22
        const val SKELETON = 23

        // Indicateurs pour les caractéristiques
        const val F_WALL = 0x1
        const val F_WALK = 0x2
        const val F_SWIM = 0x4
        const val F_BUILDING = 0x8
        const val F_NOBACK = 0x10
        const val F_MONSTER = 0x20
        const val F_CONSOMMABLE = 0x40
        const val F_BLOCK = 0x80
        operator fun get(id: Int): Tile? {
            return map[id]
        }

        fun paint(canvas: Canvas, level: Level, x: Int, y: Int) {
            var tile: Tile? = map[level[x, y]] ?: return

            // Affichage du fond (si nécessaire)
            if (tile != null) {
                if (!tile.hasOneFlag(F_NOBACK)) {
                    level.spriteSheetBG!!.paint(canvas,
                            tile.getCode(x, y, level),
                            x * level.spriteSheet!!.w.toFloat(),
                            y * level.spriteSheet.h.toFloat(), tile.color)
                }

                // Affichage du sprite de terrain
                level.spriteSheet!!.paint(canvas,
                        tile.getCode(x, y, level),
                        x * level.spriteSheet.w.toFloat(),
                        y * level.spriteSheet.h.toFloat(),
                        tile.color)

            }
            // Affichage du sprite de contenu
            val sprite = level.getContent(x, y) ?: return
            tile = map[sprite.id]
            if (tile == null) return
            level.spriteSheet?.paint(canvas,
                    tile.getCode(x, y, level),
                    x * level.spriteSheet.w.toFloat(),
                    y * level.spriteSheet.h.toFloat(),
                    tile.color)
        }

        init {
            // Initialisation des différentes cases possibles
            // Chaque type de case a un id, une couleur, un n° de sprite et des caractéristiques
            map[WALL] = Tile(0xEEBB99, 0xc7, F_WALL)
            map[SAND] = Tile(0xfad12d, 0x2E, F_WALK)
            map[WATER] = Tile(0x4d72eb, 0x7E, F_SWIM)
            map[LAVA] = Tile(0xeb520c, 0x7E)
            map[GRASS] = Tile(0x16a818, 0x2C, F_WALK)
            map[PAVEMENT] = Tile(0x999999, 0x86, F_WALK or F_BUILDING)
            map[ROCK] = Tile(0x999999, 0xF5)
            map[EARTH] = Tile(0x59390a, 0x2E, F_WALK)
            map[FOOD] = Tile(0xEEBB99, 0xE0, F_CONSOMMABLE)
            map[DOOR] = Tile(0x999999, 0x92)
            map[TABLE] = Tile(0x59390a, 0xD1, F_BLOCK)
            map[BED] = Tile(0xAAAACC, 0xE9, F_BLOCK)
            map[BUFFET] = Tile(0xBBAA99, 0xE3, F_BLOCK)
            map[CHAIR] = Tile(0xBBAA99, 0xD2)
            map[COFFER] = Tile(0x59390a, 0x08)
            map[STAIRS_UP] = Tile(0x999999, 0x3C)
            map[STAIRS_DOWN] = Tile(0x999999, 0x3E)
            map[VASE] = Tile(0x99AAFF, 0xEE)
            map[SPECTER] = Tile(0xFFFFFF, 0x84, F_MONSTER)
            map[HUMAN] = Tile(0xEEBB99, 0x40, F_MONSTER)
            map[SPIDER] = Tile(0xFFFFFF, 0xA1, F_MONSTER)
            map[ELF] = Tile(0xEEBB99, 0x8C, F_MONSTER)
            map[SKELETON] = Tile(0xFFFFFF, 0xEA, F_MONSTER)
        }
    }

    /*
 *   2          4
 * 1   3      2   8
 *   0          1
 */
    // N° de sprite pour les murs, en fonction des murs voisins
    //    00    01    10    11
    private val WALL_CODE = intArrayOf(
            0xC7, 0xB7, 0xBE, 0xBB,  // 00
            0xBD, 0xBA, 0xBC, 0xB9,  // 01
            0xD4, 0xC9, 0xCD, 0xCB,  // 10
            0xC8, 0xCC, 0xCA, 0xCE) // 11

    fun hasAllFlag(flag: Int): Boolean {
        return this.flag and flag == flag
    }

    fun hasOneFlag(flag: Int): Boolean {
        return this.flag and flag != 0
    }

    fun getCode(x: Int, y: Int, level: Level): Int {
        if (!hasAllFlag(WALL)) return code
        var result = 0
        for (i in 0..3) {
            val neighbor = map[level[x, y, i]]
            if (neighbor != null && neighbor.hasAllFlag(F_WALL)) result += 1 shl i
        }
        return WALL_CODE[result]
    }

}