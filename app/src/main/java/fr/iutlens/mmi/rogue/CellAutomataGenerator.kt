package fr.iutlens.mmi.rogue

import fr.iutlens.mmi.rogue.util.Coordinate

/**
 * Created by dubois on 25/12/2019.
 */
class CellAutomataGenerator(x: Int, y: Int) {
    val coord: Coordinate
    var data: IntArray
    var buffer: IntArray
    fun cave(def: Int) {
        init(0.4f)
        for (i in 0..1) iterate(5, -1, def)
        //        for(int i = 0; i<1 ; ++i) iterate(6,2,1);
        for (i in 0..2) iterate(4, -1, def)
    }

    fun init(p: Float) {
        for (i in data.indices) {
            data[i] = if (Math.random() < p) 1 else 0
        }
    }

    fun count(x: Int, y: Int, def: Int): Int {
        var result = 0
        for (dx in -1..1) for (dy in -1..1) {
            val ndx = coord.getNdx(x + dx, y + dy)
            if (ndx == -1) {
                result += if (y + dy >= coord.sizeY) if (def and 1 == 0) 0 else 1 else if (x + dx < 0) if (def and 2 == 0) 0 else 1 else if (y + dy < 0) if (def and 4 == 0) 0 else 1 else if (def and 8 == 0) 0 else 1
            } else if (data[ndx] == 1) ++result
        }
        return result
    }

    fun iterate(min: Int, max: Int, def: Int) {
        for (x in 0 until coord.sizeX) for (y in 0 until coord.sizeY) {
            val c = count(x, y, def)
            buffer[coord.getNdx(x, y)] = if (c <= max || c >= min) 1 else 0
        }
        val tmp = data
        data = buffer
        buffer = tmp
    }

    fun writeTo(level: Level, x0: Int, y0: Int, code0: Int, code1: Int) {
        for (x in 0 until coord.sizeX) for (y in 0 until coord.sizeY) {
            level[x0 + x, y0 + y] = if (data[coord.getNdx(x, y)] == 0) code1 else code0
        }
    }

    init {
        coord = Coordinate(x, y)
        data = IntArray(x * y)
        buffer = IntArray(x * y)
    }
}