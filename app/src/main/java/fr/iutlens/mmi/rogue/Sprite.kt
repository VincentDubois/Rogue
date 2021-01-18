package fr.iutlens.mmi.rogue

/**
 * Created by dubois on 30/12/2019.
 */
class Sprite {
    var prob: Float
    var id: Int
    var x: Int
    var y: Int

    constructor(tileId: Int, x: Int, y: Int) {
        id = tileId
        this.x = x
        this.y = y
        prob = 1f
    }

    constructor(id: Int, x: Int, y: Int, prob: Float) {
        this.id = id
        this.x = x
        this.y = y
        this.prob = prob
    }

    fun clone(): Sprite {
        return Sprite(id, x, y, prob)
    }

    fun getProb(): Boolean {
        return if (prob == 1f) true else Math.random() <= prob
    }

    fun block(): Boolean {
        return Tile[id]!!.hasOneFlag(Tile.F_BLOCK)
    }

    fun effect(level: Level?, hero: Hero?) {
        if (Tile[id]!!.hasOneFlag(Tile.F_MONSTER or Tile.F_CONSOMMABLE)) {
            level!!.removeContent(x, y)
        }
    }
}