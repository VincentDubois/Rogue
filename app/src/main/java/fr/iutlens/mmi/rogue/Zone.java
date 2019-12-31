package fr.iutlens.mmi.rogue;

/**
 * Created by dubois on 30/12/2019.
 */
class Zone {
    final static Integer[] zoneType = {Tile.ROCK,Tile.EARTH,Tile.GRASS,Tile.SAND,Tile.WATER};
    final static RandomChooser<Integer> typeChooser = new RandomChooser<>(zoneType);


    final int typeA, typeB;
    int border;

    public Zone() {
        this.typeA = Tile.ROCK; //typeChooser.getOne();
        this.typeB = typeChooser.getOne(typeA);
    }

    public Zone(Zone z, int dir) {
        if (Math.random() < 0.5f) {
            this.typeA = z.typeA;
            z.addBorderTypeA((dir + 2) % 4);
        } else {
            this.typeA = z.typeB;
        }
        if (Math.random() < 0.5f) addBorderTypeA((dir + 2) % 4);
        addBorderTypeA(dir);
        this.typeB = typeChooser.getOne(typeA);
    }

    public Zone(Zone z1, int d1, Zone z2, int d2) {
        if (Math.random() < 0.5f) {
            this.typeA = z1.typeA;
            z1.addBorderTypeA((d1 + 2) % 4);
        } else {
            this.typeA = z1.typeB;
        }
        addBorderTypeA(d1);

        if (z2.typeA == typeA || z2.typeB == typeA) {
            this.typeB = typeChooser.getOne(typeA);
        } else if (Math.random() < 0.5f) {
            this.typeB = z2.typeA;
        } else {
            this.typeB = z2.typeB;
        }

        if (z2.typeA == typeA) {
            z2.addBorderTypeA((d2 + 2) % 4);
            addBorderTypeA(d2);
        } else if (z2.typeA == typeB) {
            z2.addBorderTypeA((d2 + 2) % 4);
        } else if (z2.typeB == typeA) {
            addBorderTypeA(d2);
        }


        if (Math.random() < 0.5f) addBorderTypeA((d1 + 2) % 4);
        if (Math.random() < 0.5f) addBorderTypeA((d2 + 2) % 4);


    }

    public void addBorderTypeA(int dir) {
        border |= 1 << dir;
    }

    public int getBorderType(int dir) {
        return (border | (1 << dir)) == 0 ? typeB : typeA;
    }

    public void generate(Level level, CellAutomataGenerator cgen, int x, int y) {
        cgen.cave(border);
        cgen.writeTo(level, x, y, typeA, typeB);
    }


    public static void generateZone(Level level) {
        // 0 1 2     2
        // 3 4 5   1   3
        // 6 7 8     0

        level.zone[4] = new Zone();
        level.zone[3] = new Zone(level.zone[4],3);
        level.zone[1] = new Zone(level.zone[4],0);
        level.zone[5] = new Zone(level.zone[4],1);
        level.zone[7] = new Zone(level.zone[4],2);
        level.zone[0] = new Zone(level.zone[1],3, level.zone[3], 0);
        level.zone[6] = new Zone(level.zone[7],3, level.zone[3], 2);
        level.zone[2] = new Zone(level.zone[1],1, level.zone[5], 0);
        level.zone[8] = new Zone(level.zone[5],2, level.zone[7], 1);


        CellAutomataGenerator cgen = new CellAutomataGenerator(level.coord.sizeX/3,level.coord.sizeY/3);

        for(int x = 0; x< 3; ++x)
            for(int y = 0; y < 3; ++y)
                level.zone[x+3*y].generate(level, cgen,(x*level.coord.sizeX)/3,(y*level.coord.sizeY)/3);
    }

}
