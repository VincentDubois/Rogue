package fr.iutlens.mmi.rogue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import fr.iutlens.mmi.rogue.util.Coordinate;

/**
 * Created by dubois on 28/12/2019.
 */

class Room {



    public static class Spec {

        final Set<String> connect;
        final Set<Sprite> sprite;
        final Vector<Opening> openings;
        final String name;
        final int width;
        final int height;
        final int min;
        final int max;
        private Spec sym;

        public Spec(String name, int width, int height, int min, int max) {
            this.name = name;
            this.width = width;
            this.height = height;
            this.min = min;
            this.max = max;

            connect = new HashSet<>();
            sprite = new HashSet<>();
            openings = new Vector<>();


            specs.put(name,this);

            sym = null;
        }

        private Spec(Spec spec){
            this.name = spec.name;
            this.width = spec.height;
            this.height = spec.width;
            this.min = spec.min;
            this.max = spec.max;

            connect = spec.connect;
            openings = new Vector<>();
            for(Opening opening : spec.openings){
                openings.add(new Opening(opening.dy, opening.dx,3- opening.dir,this));
            }

            this.sprite = new HashSet<>();
            for(Sprite s : spec.sprite){
                this.sprite.add(new Sprite(s.id, s.y, s.x, s.prob));
            }

            sym = spec;
        }

        public Spec getSym(){
            if (sym == null) sym = new Spec(this);
            return sym;
        }

        public Spec connect(String name){
            connect.add(name);
            return this;
        }

        public Spec anchor(int dir, float pos) {
            int x = 0;
            int y = 0;
            if (dir == 3) {
                x = width-1;
                pos = 1-pos;
            } else if (dir == 0) {
                y = height-1;
                pos = 1-pos;
            }
            if ( dir == 1 || dir == 3){
                y = Math.round(pos*(height-1));
            } else {
                x = Math.round(pos*(width-1));
            }

            x+= Coordinate.dir_coord[dir][0];
            y+= Coordinate.dir_coord[dir][1];

            openings.add(new Opening(x,y,dir,this));
            return this;
        }

        public Spec defaultAnchor(){
            for(int i=0; i< 4; ++i) anchor(i,0.5f);
            return this;
        }

        public Spec addDinerFurniture() {
            for(int x = 2; x<=4; ++x )
                for(int y = 2; y<=4; ++y )
                    sprite.add(new Sprite(Tile.TABLE,x,y));
            for(int x = 1; x<=5; ++x ){
                sprite.add(new Sprite(Tile.CHAIR,x,1));
                sprite.add(new Sprite(Tile.CHAIR,x,5));
            }
            for(int y = 2; y<=4; ++y ){
                sprite.add(new Sprite(Tile.CHAIR,1,y));
                sprite.add(new Sprite(Tile.CHAIR,5,y));
            }
            return this;
        }

        public Spec addApartementFurniture() {
            sprite.add(new Sprite(Tile.BED,0,1));
            sprite.add(new Sprite(Tile.BUFFET,0,3));
            sprite.add(new Sprite(Tile.TABLE,4,1));
            sprite.add(new Sprite(Tile.CHAIR,4,0));
            sprite.add(new Sprite(Tile.TABLE,4,2));
            sprite.add(new Sprite(Tile.CHAIR,4,3));
            return this;
        }

        public Spec addBarracksFurniture() {
            for(int y = 1; y < 13; y+=2){
                sprite.add(new Sprite(Tile.BED,0,y));
                sprite.add(new Sprite(Tile.BUFFET,1,y));
                sprite.add(new Sprite(Tile.BUFFET,3,y));
                sprite.add(new Sprite(Tile.BED,4,y));
            }
            return this;
        }

        public Spec addKitchenFurniture() {
            sprite.add(new Sprite(Tile.TABLE,0,0));
            sprite.add(new Sprite(Tile.TABLE,0,1));
            sprite.add(new Sprite(Tile.TABLE,1,0));
            sprite.add(new Sprite(Tile.FOOD,1,1));

            sprite.add(new Sprite(Tile.BUFFET,3,0));
            sprite.add(new Sprite(Tile.BUFFET,4,0));

            sprite.add(new Sprite(Tile.TABLE,0,4));
            sprite.add(new Sprite(Tile.TABLE,1,4));
            sprite.add(new Sprite(Tile.TABLE,3,4));
            sprite.add(new Sprite(Tile.TABLE,4,4));


            return this;
        }

        public Spec addHallFurniture() {
            for(int y = 1; y<=11; y+=2 ){
                sprite.add(new Sprite(Tile.VASE,1,y));
                sprite.add(new Sprite(Tile.VASE,7,y));

            }
            return this;
        }

        public void addMonster(int x, int y, int id, float prob) {
            sprite.add(new Sprite(id,x,y,prob));
        }
    }

    static final Map<String,Spec> specs = new HashMap<>();
    static {
        new Spec("Hall",9,13,3,4)
                .connect("Diner").connect("Barracks").connect("Corridor")
                .defaultAnchor()
                .anchor(1,0.33f).anchor(1,0.66f)
                .anchor(3,0.33f).anchor(3,0.66f)
                .addHallFurniture()
                .addMonster(4,6,Tile.HUMAN,0.4f);

        new Spec("Diner", 7,7,2,3)
                .connect("Hall").connect("Apartment").connect("Kitchen").defaultAnchor()
                .addDinerFurniture()
                .addMonster(3,3,Tile.SPECTER, 0.2f);

        new Spec("Apartment", 5,4,1,2)
                .connect("Corridor").connect("Diner").anchor(0,0.5f).anchor(2,0.5f)
                .addApartementFurniture()
                .addMonster(2,2,Tile.SKELETON,0.6f);

        new Spec("Kitchen", 5,5,2,4)
                .connect("Diner").connect("Barracks").defaultAnchor()
                .addKitchenFurniture()
                .addMonster(2,2,Tile.SPIDER,0.4f);

        new Spec("Barracks", 5, 13,2,4)
                .connect("Hall").connect("Kitchen").connect("Corridor")
                .anchor(0,0.5f).anchor(2,0.5f)
                .anchor(1,0.33f).anchor(1,0.66f)
                .anchor(3,0.33f).anchor(3,0.66f)
                .addBarracksFurniture()
                .addMonster(2,6,Tile.SKELETON,0.6f);
        new Spec("Corridor",9,1,3,8)
                .connect("Corridor").connect("Hall").connect("Apartment").connect("Corridor")
                .defaultAnchor()
                .anchor(0,0.25f).anchor(0,0.75f)
                .anchor(2,0.25f).anchor(2,0.75f)
                .anchor(0,0f).anchor(0,1f)
                .anchor(2,0f).anchor(2,1f)
                .addMonster(4,0,Tile.ELF, 0.1f);;

    }

    Spec spec;

    private int x0;
    private int y0;
    private final Set<Link> links;


    public void generateLinks(Level level, Map<Integer, Link> anchors) {
        for(Opening opening : spec.openings){
            final int x = x0 + opening.dx;
            final int y = y0 + opening.dy;
            int ndx = level.coord.getNdx(x, y);
            final Link existingLink = anchors.get(ndx);
            if (existingLink == null) {
                final Link link = new Link(this, opening, x, y);
                anchors.put(ndx, link);
                links.add(link);
            } else {
                existingLink.to = this;
            }
        }
    }


    Room(String type){
        this.spec = specs.get(type);
        if (Math.random()<0.5f) this.spec = this.spec.getSym();
        this.links = new HashSet<Link>();
    }

    public Set<Link> getLinks() {
        return links;
    }

    public int getCurrentLinkCount(){
        int result = 0;
        for(Link link : links) if (link.to != null) ++result;
        return result;
    }

    Opening canPlace(Level level, int dir, int x, int y){
//        Log.d("canPlace", dir+" "+x+" "+y);
        for(Opening a : spec.openings){
            if ((a.dir+2)%4==dir){
                int x0 = x -a.dx;
                int y0 = y -a.dy;

                boolean can_place = true;
                for(int dx =-1; dx <= spec.width && can_place; ++dx)
                    for(int dy=-1; dy <= spec.height && can_place;++dy){
                        int id = level.get(x0+dx,y0+dy);
                        if (id == -1 || Tile.get(id).hasOneFlag(Tile.F_BUILDING | Tile.F_SWIM))
                            can_place = false;
                    }
                if (can_place) return a;

            }
        }
        return null;
    }

    public Opening canPlace(Level level, Link link) {
        return canPlace(level, link.dir, link.x, link.y);
    }


    void place(Level level, Opening opening, int x, int y){
        this.x0 = x - opening.dx;
        this.y0 = y - opening.dy;

        for(int dx =0; dx < spec.width; ++dx)
            for(int dy=0; dy < spec.height;++dy){
                level.set(x0+dx,y0+dy,Tile.PAVEMENT);
            }

        for(Sprite s : spec.sprite){
//                Log.d("place",s.id+" "+s.prob);
            if (s.getProb())
                level.setContent(x0+s.x,y0+s.y,s.clone());
        }
    }

    void addWall(Level level){
        for(int dx =-1; dx <= spec.width; ++dx){
            level.set(x0+dx, y0-1,Tile.F_WALL);
            level.set(x0+dx, y0+spec.height,Tile.F_WALL);
        }
        for(int dy=-1; dy <= spec.height; ++dy){
            level.set(x0-1, y0+dy,Tile.F_WALL);
            level.set(x0+spec.width, y0+dy,Tile.F_WALL);
        }
    }


    static class Opening {
        final int dx;
        final int dy;
        final int dir;
        final Spec spec;

        public Opening(int dx, int dy, int dir, Spec spec) {

            this.dx = dx;
            this.dy = dy;
            this.dir = dir;
            this.spec = spec;
        }
    }


    public static class Link {
        public final int x;
        public final int y;
        public Room from;
        public Room to;
        public int dir;

        public Link(Room room, Opening opening, int x, int y) {
            this.from = room;
            this.dir = opening.dir;
            this.x = x;
            this.y = y;
            this.to = null;
        }
    }
}
