package fr.iutlens.mmi.rogue

/**
 * Created by dubois on 30/12/2019.
 */
class Zone {
    val typeA: Int
    val typeB: Int
    var border = 0

    constructor() {
        typeA = Tile.ROCK //typeChooser.getOne();
        typeB = typeChooser.getOne(typeA)
    }

    constructor(z: Zone?, dir: Int) {
        if (Math.random() < 0.5f) {
            typeA = z!!.typeA
            z.addBorderTypeA((dir + 2) % 4)
        } else {
            typeA = z!!.typeB
        }
        if (Math.random() < 0.5f) addBorderTypeA((dir + 2) % 4)
        addBorderTypeA(dir)
        typeB = typeChooser.getOne(typeA)
    }

    constructor(z1: Zone?, d1: Int, z2: Zone?, d2: Int) {
        if (Math.random() < 0.5f) {
            typeA = z1!!.typeA
            z1.addBorderTypeA((d1 + 2) % 4)
        } else {
            typeA = z1!!.typeB
        }
        addBorderTypeA(d1)
        if (z2!!.typeA == typeA || z2.typeB == typeA) {
            typeB = typeChooser.getOne(typeA)
        } else if (Math.random() < 0.5f) {
            typeB = z2.typeA
        } else {
            typeB = z2.typeB
        }
        if (z2.typeA == typeA) {
            z2.addBorderTypeA((d2 + 2) % 4)
            addBorderTypeA(d2)
        } else if (z2.typeA == typeB) {
            z2.addBorderTypeA((d2 + 2) % 4)
        } else if (z2.typeB == typeA) {
            addBorderTypeA(d2)
        }
        if (Math.random() < 0.5f) addBorderTypeA((d1 + 2) % 4)
        if (Math.random() < 0.5f) addBorderTypeA((d2 + 2) % 4)
    }

    fun addBorderTypeA(dir: Int) {
        border = border or (1 shl dir)
    }

    fun getBorderType(dir: Int): Int {
        return if (border or (1 shl dir) == 0) typeB else typeA
    }

    fun generate(level: Level, cgen: CellAutomataGenerator, x: Int, y: Int) {
        cgen.cave(border)
        cgen.writeTo(level, x, y, typeA, typeB)
    }

    companion object {
        val zoneType = arrayOf<Int>(Tile.Companion.ROCK, Tile.Companion.EARTH, Tile.Companion.GRASS, Tile.Companion.SAND, Tile.Companion.WATER)
        val typeChooser = RandomChooser(zoneType)
        fun generateZone(level: Level) {
            // 0 1 2     2
            // 3 4 5   1   3
            // 6 7 8     0
            level.zone[4] = Zone()
            level.zone[3] = Zone(level.zone[4], 3)
            level.zone[1] = Zone(level.zone[4], 0)
            level.zone[5] = Zone(level.zone[4], 1)
            level.zone[7] = Zone(level.zone[4], 2)
            level.zone[0] = Zone(level.zone[1], 3, level.zone[3], 0)
            level.zone[6] = Zone(level.zone[7], 3, level.zone[3], 2)
            level.zone[2] = Zone(level.zone[1], 1, level.zone[5], 0)
            level.zone[8] = Zone(level.zone[5], 2, level.zone[7], 1)
            val cgen = CellAutomataGenerator(level.coord.sizeX / 3, level.coord.sizeY / 3)
            for (x in 0..2) for (y in 0..2) level.zone[x + 3 * y]!!.generate(level, cgen, x * level.coord.sizeX / 3, y * level.coord.sizeY / 3)
        }
    }
}