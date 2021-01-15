package fr.iutlens.mmi.rogue

import fr.iutlens.mmi.rogue.util.Coordinate
import java.util.*

/**
 * Created by dubois on 28/12/2019.
 */
internal class Room(type: String?) {
    class Spec {
        val connect: MutableSet<String>
        val sprite: MutableSet<Sprite>
        val openings: Vector<Opening>
        val name: String
        val width: Int
        val height: Int
        val min: Int
        val max: Int
        private var sym: Spec?

        constructor(name: String, width: Int, height: Int, min: Int, max: Int) {
            this.name = name
            this.width = width
            this.height = height
            this.min = min
            this.max = max
            connect = HashSet()
            sprite = HashSet()
            openings = Vector()
            specs[name] = this
            sym = null
        }

        private constructor(spec: Spec) {
            name = spec.name
            width = spec.height
            height = spec.width
            min = spec.min
            max = spec.max
            connect = spec.connect
            openings = Vector()
            for (opening in spec.openings) {
                openings.add(Opening(opening.dy, opening.dx, 3 - opening.dir, this))
            }
            sprite = HashSet()
            for (s in spec.sprite) {
                sprite.add(Sprite(s.id, s.y, s.x, s.prob))
            }
            sym = spec
        }

        fun getSym(): Spec {
            if (sym == null) sym = Spec(this)
            return sym!!
        }

        fun connect(name: String): Spec {
            connect.add(name)
            return this
        }

        fun anchor(dir: Int, pos: Float): Spec {
            var pos = pos
            var x = 0
            var y = 0
            if (dir == 3) {
                x = width - 1
                pos = 1 - pos
            } else if (dir == 0) {
                y = height - 1
                pos = 1 - pos
            }
            if (dir == 1 || dir == 3) {
                y = Math.round(pos * (height - 1))
            } else {
                x = Math.round(pos * (width - 1))
            }
            x += Coordinate.Companion.dir_coord.get(dir).get(0)
            y += Coordinate.Companion.dir_coord.get(dir).get(1)
            openings.add(Opening(x, y, dir, this))
            return this
        }

        fun defaultAnchor(): Spec {
            for (i in 0..3) anchor(i, 0.5f)
            return this
        }

        fun addDinerFurniture(): Spec {
            for (x in 2..4) for (y in 2..4) sprite.add(Sprite(Tile.Companion.TABLE, x, y))
            for (x in 1..5) {
                sprite.add(Sprite(Tile.Companion.CHAIR, x, 1))
                sprite.add(Sprite(Tile.Companion.CHAIR, x, 5))
            }
            for (y in 2..4) {
                sprite.add(Sprite(Tile.Companion.CHAIR, 1, y))
                sprite.add(Sprite(Tile.Companion.CHAIR, 5, y))
            }
            return this
        }

        fun addApartementFurniture(): Spec {
            sprite.add(Sprite(Tile.Companion.BED, 0, 1))
            sprite.add(Sprite(Tile.Companion.BUFFET, 0, 3))
            sprite.add(Sprite(Tile.Companion.TABLE, 4, 1))
            sprite.add(Sprite(Tile.Companion.CHAIR, 4, 0))
            sprite.add(Sprite(Tile.Companion.TABLE, 4, 2))
            sprite.add(Sprite(Tile.Companion.CHAIR, 4, 3))
            return this
        }

        fun addBarracksFurniture(): Spec {
            var y = 1
            while (y < 13) {
                sprite.add(Sprite(Tile.Companion.BED, 0, y))
                sprite.add(Sprite(Tile.Companion.BUFFET, 1, y))
                sprite.add(Sprite(Tile.Companion.BUFFET, 3, y))
                sprite.add(Sprite(Tile.Companion.BED, 4, y))
                y += 2
            }
            return this
        }

        fun addKitchenFurniture(): Spec {
            sprite.add(Sprite(Tile.Companion.TABLE, 0, 0))
            sprite.add(Sprite(Tile.Companion.TABLE, 0, 1))
            sprite.add(Sprite(Tile.Companion.TABLE, 1, 0))
            sprite.add(Sprite(Tile.Companion.FOOD, 1, 1))
            sprite.add(Sprite(Tile.Companion.BUFFET, 3, 0))
            sprite.add(Sprite(Tile.Companion.BUFFET, 4, 0))
            sprite.add(Sprite(Tile.Companion.TABLE, 0, 4))
            sprite.add(Sprite(Tile.Companion.TABLE, 1, 4))
            sprite.add(Sprite(Tile.Companion.TABLE, 3, 4))
            sprite.add(Sprite(Tile.Companion.TABLE, 4, 4))
            return this
        }

        fun addHallFurniture(): Spec {
            var y = 1
            while (y <= 11) {
                sprite.add(Sprite(Tile.Companion.VASE, 1, y))
                sprite.add(Sprite(Tile.Companion.VASE, 7, y))
                y += 2
            }
            return this
        }

        fun addMonster(x: Int, y: Int, id: Int, prob: Float) {
            sprite.add(Sprite(id, x, y, prob))
        }
    }

    companion object {
        val specs: MutableMap<String, Spec> = HashMap()

        init {
            Spec("Hall", 9, 13, 3, 4)
                    .connect("Diner").connect("Barracks").connect("Corridor")
                    .defaultAnchor()
                    .anchor(1, 0.33f).anchor(1, 0.66f)
                    .anchor(3, 0.33f).anchor(3, 0.66f)
                    .addHallFurniture()
                    .addMonster(4, 6, Tile.Companion.HUMAN, 0.4f)
            Spec("Diner", 7, 7, 2, 3)
                    .connect("Hall").connect("Apartment").connect("Kitchen").defaultAnchor()
                    .addDinerFurniture()
                    .addMonster(3, 3, Tile.Companion.SPECTER, 0.2f)
            Spec("Apartment", 5, 4, 1, 2)
                    .connect("Corridor").connect("Diner").anchor(0, 0.5f).anchor(2, 0.5f)
                    .addApartementFurniture()
                    .addMonster(2, 2, Tile.Companion.SKELETON, 0.6f)
            Spec("Kitchen", 5, 5, 2, 4)
                    .connect("Diner").connect("Barracks").defaultAnchor()
                    .addKitchenFurniture()
                    .addMonster(2, 2, Tile.Companion.SPIDER, 0.4f)
            Spec("Barracks", 5, 13, 2, 4)
                    .connect("Hall").connect("Kitchen").connect("Corridor")
                    .anchor(0, 0.5f).anchor(2, 0.5f)
                    .anchor(1, 0.33f).anchor(1, 0.66f)
                    .anchor(3, 0.33f).anchor(3, 0.66f)
                    .addBarracksFurniture()
                    .addMonster(2, 6, Tile.Companion.SKELETON, 0.6f)
            Spec("Corridor", 9, 1, 3, 8)
                    .connect("Corridor").connect("Hall").connect("Apartment").connect("Corridor")
                    .defaultAnchor()
                    .anchor(0, 0.25f).anchor(0, 0.75f)
                    .anchor(2, 0.25f).anchor(2, 0.75f)
                    .anchor(0, 0f).anchor(0, 1f)
                    .anchor(2, 0f).anchor(2, 1f)
                    .addMonster(4, 0, Tile.Companion.ELF, 0.1f)
        }
    }

    var spec: Spec?
    private var x0 = 0
    private var y0 = 0
    private val links: MutableSet<Link>
    fun generateLinks(level: Level, anchors: MutableMap<Int?, Link>) {
        for (opening in spec!!.openings) {
            val x = x0 + opening.dx
            val y = y0 + opening.dy
            val ndx = level.coord.getNdx(x, y)
            val existingLink = anchors[ndx]
            if (existingLink == null) {
                val link = Link(this, opening, x, y)
                anchors[ndx] = link
                links.add(link)
            } else {
                existingLink.to = this
            }
        }
    }

    fun getLinks(): Set<Link> {
        return links
    }

    val currentLinkCount: Int
        get() {
            var result = 0
            for (link in links) if (link.to != null) ++result
            return result
        }

    fun canPlace(level: Level, dir: Int, x: Int, y: Int): Opening? {
//        Log.d("canPlace", dir+" "+x+" "+y);
        for (a in spec!!.openings) {
            if ((a.dir + 2) % 4 == dir) {
                val x0 = x - a.dx
                val y0 = y - a.dy
                var can_place = true
                var dx = -1
                while (dx <= spec!!.width && can_place) {
                    var dy = -1
                    while (dy <= spec!!.height && can_place) {
                        val id = level[x0 + dx, y0 + dy]
                        if (id == -1 || Tile.Companion.get(id)!!.hasOneFlag(Tile.Companion.F_BUILDING or Tile.Companion.F_SWIM)) can_place = false
                        ++dy
                    }
                    ++dx
                }
                if (can_place) return a
            }
        }
        return null
    }

    fun canPlace(level: Level, link: Link): Opening? {
        return canPlace(level, link.dir, link.x, link.y)
    }

    fun place(level: Level, opening: Opening?, x: Int, y: Int) {
        x0 = x - opening!!.dx
        y0 = y - opening.dy
        for (dx in 0 until spec!!.width) for (dy in 0 until spec!!.height) {
            level[x0 + dx, y0 + dy] = Tile.Companion.PAVEMENT
        }
        for (s in spec!!.sprite) {
//                Log.d("place",s.id+" "+s.prob);
            if (s.getProb()) level.setContent(x0 + s.x, y0 + s.y, s.clone())
        }
    }

    fun addWall(level: Level) {
        for (dx in -1..spec!!.width) {
            level[x0 + dx, y0 - 1] = Tile.Companion.F_WALL
            level[x0 + dx, y0 + spec!!.height] = Tile.Companion.F_WALL
        }
        for (dy in -1..spec!!.height) {
            level[x0 - 1, y0 + dy] = Tile.Companion.F_WALL
            level[x0 + spec!!.width, y0 + dy] = Tile.Companion.F_WALL
        }
    }

    internal class Opening(val dx: Int, val dy: Int, val dir: Int, val spec: Spec)

    class Link(var from: Room, opening: Opening, x: Int, y: Int) {
        val x: Int
        val y: Int
        var to: Room?
        var dir: Int

        init {
            dir = opening.dir
            this.x = x
            this.y = y
            to = null
        }
    }

    init {
        spec = specs[type]
        if (Math.random() < 0.5f) spec = spec!!.getSym()
        links = HashSet()
    }
}