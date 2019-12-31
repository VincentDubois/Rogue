package fr.iutlens.mmi.rogue;

import android.util.Log;
import android.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

/**
 * Created by dubois on 28/12/2019.
 */

class RoomGenerator {

    private final Set<Room> rooms;
    private final Map<Integer, Room.Link> anchors;
    private final Random rand;
    private Level level;
    private final Vector<String> roomList;

    public RoomGenerator(Level level) {
        rooms = new HashSet<>();
        anchors = new HashMap<>();
        this.level = level;
        RandomChooser<String> roomChooser = new RandomChooser<>();
        roomChooser.addOption("Hall", 10,1);
        roomChooser.addOption("Kitchen",3,2);
        roomChooser.addOption("Apartment",2,4 );
        roomChooser.addOption("Barracks",5,2);
        roomChooser.addOption("Diner",6,2);
        roomChooser.addOption("Corridor",4,5);

        roomList = roomChooser.get(400);
        Log.d("roomList", roomList.size()+" ");

        rand = new Random();
    }

    public void generate() {
        Room room = new Room("Corridor");
        Room.Opening opening = null;

        int x,y,dir;
        do {
            x = rand.nextInt(level.getSizeX());
            y = rand.nextInt(level.getSizeY());
            dir = rand.nextInt(4);
            opening = room.canPlace(level,dir,x,y);
        } while (opening == null);

        room.place(level, opening,x,y);
        rooms.add(room);
        room.generateLinks(level,anchors);

        while (!roomList.isEmpty()){
            room = new Room(roomList.remove(0));

//            Log.d("room",room.spec.name+" "+anchors.size());

            RandomChooser<Pair<Room.Opening, Room.Link> > chooser = new RandomChooser<>();

            for(Room.Link link : anchors.values()){
                if (link.to == null) {
                    opening = room.canPlace(level, link);
                    if (opening != null) {
                        int weight = 1;
                        if (link.from.spec.connect.contains(room.spec.name)) weight += 10;
                        final int size = link.from.getCurrentLinkCount();
                        if (link.from.spec.min > size) weight += link.from.spec.min-size;
//                        Log.d("option",weight+" "+link.from.spec.name+" "+link.dir);
                        if (size < link.from.spec.max)
                            chooser.addOption(new Pair(opening,link), 1, weight);
                    }
                }
            }

            Pair<Room.Opening, Room.Link> pair =  chooser.getOne();
            if (pair != null){
                room.place(level,pair.first, pair.second.x, pair.second.y);
                pair.second.to = room;
                rooms.add(room);
                room.generateLinks(level,anchors);
            }

        }




        for(Room r : rooms){
            r.addWall(level);
        }

        for(Room.Link link : anchors.values()){
            if (link.to != null)
                level.set(link.x, link.y,Tile.PAVEMENT);
            else if (link.from.spec.connect.contains(null)){
                int id = level.get(link.x,link.y,link.dir);
                if (id != -1 &&
                        Tile.get(id).hasOneFlag(Tile.F_WALK)
                        && !Tile.get(id).hasOneFlag(Tile.F_BUILDING)){
                    level.set(link.x,link.y,Tile.PAVEMENT);
                    level.setContent(link.x,link.y, new Sprite(Tile.DOOR,link.x,link.y));
                }
            }
        }

        RandomChooser<Integer> stairsChooser = new RandomChooser<>();

        for(x = 0; x < level.coord.sizeX; ++x)
            for(y = 0; y < level.coord.sizeY; ++y){
                Tile tile = Tile.get(level.get(x,y));
                if (tile.hasOneFlag(Tile.F_BUILDING)){
                    int count = 0;
                    for(int i = 0; i< 4; ++i){
                        Tile neighbor = Tile.get(level.get(x,y,i));
                        if (neighbor != null && neighbor.hasAllFlag(Tile.F_WALL)) count++;
                    }
                    if (count ==3) stairsChooser.addOption(level.coord.getNdx(x,y),1,1);
                }
            }

        Vector<Integer> result = stairsChooser.get(2);
        int start = result.get(0);
        int end = result.get(1);

        x = level.coord.getX(start);
        y = level.coord.getY(start);
        level.setContent(x,y, new Sprite(Tile.STAIRS_UP,x,y));

        x = level.coord.getX(end);
        y = level.coord.getY(end);
        level.setContent(x,y, new Sprite(Tile.STAIRS_DOWN,x,y));

        level.setStart(start);

    }
}
