package fr.iutlens.mmi.rogue

import android.util.Log
import android.util.Pair
import fr.iutlens.mmi.rogue.Room.Opening
import java.util.*

/**
 * Created by dubois on 28/12/2019.
 */
internal class RoomGenerator(level: Level) {
    private val rooms: MutableSet<Room>
    private val anchors: MutableMap<Int?, Room.Link>
    private val rand: Random
    private val level: Level
    private val roomList: Vector<String>
    fun generate() {
        var room = Room("Corridor")
        var opening: Opening? = null
        var x: Int
        var y: Int
        var dir: Int
        do {
            x = rand.nextInt(level.sizeX)
            y = rand.nextInt(level.sizeY)
            dir = rand.nextInt(4)
            opening = room.canPlace(level, dir, x, y)
        } while (opening == null)
        room.place(level, opening, x, y)
        rooms.add(room)
        room.generateLinks(level, anchors)
        while (!roomList!!.isEmpty()) {
            room = Room(roomList.removeAt(0))

//            Log.d("room",room.spec.name+" "+anchors.size());
            val chooser = RandomChooser<Pair<Opening, Room.Link>>()
            for (link in anchors.values) {
                if (link.to == null) {
                    opening = room.canPlace(level, link)
                    if (opening != null) {
                        var weight = 1
                        if (link.from.spec!!.connect.contains(room.spec!!.name)) weight += 10
                        val size = link.from.currentLinkCount
                        if (link.from.spec!!.min > size) weight += link.from.spec!!.min - size
                        //                        Log.d("option",weight+" "+link.from.spec.name+" "+link.dir);
                        if (size < link.from.spec!!.max) chooser.addOption(Pair<Opening, Room.Link>(opening, link), 1, weight)
                    }
                }
            }
            val pair = chooser.one
            if (pair != null) {
                room.place(level, pair.first, pair.second!!.x, pair.second!!.y)
                pair.second!!.to = room
                rooms.add(room)
                room.generateLinks(level, anchors)
            }
        }
        for (r in rooms) {
            r.addWall(level)
        }
        for (link in anchors.values) {
            if (link.to != null) level[link.x, link.y] = Tile.Companion.PAVEMENT else
                if (link.from.spec!!.connect.contains<String?>(null)) {
                val id = level[link.x, link.y, link.dir]
                if (id != -1 &&
                        Tile.Companion.get(id)!!.hasOneFlag(Tile.Companion.F_WALK)
                        && !Tile.Companion.get(id)!!.hasOneFlag(Tile.Companion.F_BUILDING)) {
                    level[link.x, link.y] = Tile.Companion.PAVEMENT
                    level.setContent(link.x, link.y, Sprite(Tile.Companion.DOOR, link.x, link.y))
                }
            }
        }
        val stairsChooser = RandomChooser<Int>()
        x = 0
        while (x < level.coord.sizeX) {
            y = 0
            while (y < level.coord.sizeY) {
                val tile: Tile? = Tile.Companion.get(level[x, y])
                if (tile != null && tile.hasOneFlag(Tile.Companion.F_BUILDING)) {
                    var count = 0
                    for (i in 0..3) {
                        val neighbor: Tile? = Tile.Companion.get(level[x, y, i])
                        if (neighbor != null && neighbor.hasAllFlag(Tile.Companion.F_WALL)) count++
                    }
                    if (count == 3) stairsChooser.addOption(level.coord.getNdx(x, y), 1, 1)
                }
                ++y
            }
            ++x
        }
        val result = stairsChooser.get(2)
        val start = result!![0]
        val end = result[1]
        x = level.coord.getX(start)
        y = level.coord.getY(start)
        level.setContent(x, y, Sprite(Tile.Companion.STAIRS_UP, x, y))
        x = level.coord.getX(end)
        y = level.coord.getY(end)
        level.setContent(x, y, Sprite(Tile.Companion.STAIRS_DOWN, x, y))
        level.start = start
    }

    init {
        rooms = HashSet()
        anchors = HashMap()
        this.level = level
        val roomChooser = RandomChooser<String>()
        roomChooser.addOption("Hall", 10, 1)
        roomChooser.addOption("Kitchen", 3, 2)
        roomChooser.addOption("Apartment", 2, 4)
        roomChooser.addOption("Barracks", 5, 2)
        roomChooser.addOption("Diner", 6, 2)
        roomChooser.addOption("Corridor", 4, 5)
        roomList = roomChooser.get(400)
        Log.d("roomList", roomList.size.toString() + " ")
        rand = Random()
    }
}