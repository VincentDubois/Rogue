package fr.iutlens.mmi.rogue.util

/**
 * Created by dubois on 23/12/2019.
 */
class Coordinate(val sizeX: Int, val sizeY: Int) {
    fun getNdx(x: Int, y: Int): Int {
        if (x < 0 || x >= sizeX) return -1
        return if (y < 0 || y >= sizeY) -1 else x * sizeY + y
    }

    fun getX(ndx: Int): Int {
        return ndx / sizeY
    }

    fun getY(ndx: Int): Int {
        return ndx % sizeY
    }

    fun getNext(ndx: Int, dir: Int): Int {
        return getNdx(getX(ndx) + dir_coord[dir][0], getY(ndx) + dir_coord[dir][1])
    }

    fun getNext(x: Int, y: Int, dir: Int): Int {
        return getNdx(x + dir_coord[dir][0], y + dir_coord[dir][1])
    }

    fun getNext(x: Int, y: Int, dir: Int, n: Int): Int {
        return getNdx(x + n * dir_coord[dir][0], y + n * dir_coord[dir][1])
    }

    companion object {
        val dir_coord = arrayOf(intArrayOf(0, 1), intArrayOf(-1, 0), intArrayOf(0, -1), intArrayOf(1, 0))
    }

}