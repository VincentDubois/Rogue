package fr.iutlens.mmi.rogue;

import android.graphics.Canvas;

import java.util.HashMap;

/**
 * Created by dubois on 22/12/2019.
 */

class Tile {

    private static final HashMap<Integer,Tile> map = new HashMap<>();

    // Constantes pour le terrain
    static final Integer WALL = 1;
    static final Integer SAND = 2;
    static final Integer WATER = 3;
    static final Integer LAVA = 4;
    static final Integer GRASS = 5;
    static final Integer PAVEMENT = 6;
    static final Integer ROCK = 7;
    static final Integer EARTH = 8;

    // Constantes pour les objets
    static final Integer FOOD = 9;
    static final Integer DOOR = 10;
    static final Integer TABLE = 11;
    static final Integer BED = 12;
    static final Integer BUFFET = 13;
    static final Integer CHAIR = 14;
    static final Integer COFFER = 15;
    static final Integer STAIRS_UP = 16;
    static final Integer STAIRS_DOWN = 17;
    static final Integer VASE = 18;

    //Constantes pour les monstres
    static final Integer SPECTER = 19;
    static final Integer HUMAN = 20;
    static final Integer SPIDER = 21;
    static final Integer ELF = 22;
    static final Integer SKELETON = 23;

    // Indicateurs pour les caractéristiques
    public static final int F_WALL = 0x1;
    public static final int F_WALK = 0x2;
    public static final int F_SWIM = 0x4;
    public static final int F_BUILDING = 0x8;
    public static final int F_NOBACK = 0x10;
    public static final int F_MONSTER = 0x20;
    public static final int F_CONSOMMABLE = 0x40;
    public static final int F_BLOCK = 0x80;



    static {
        // Initialisation des différentes cases possibles
        // Chaque type de case a un id, une couleur, un n° de sprite et des caractéristiques

        map.put(WALL,new Tile(0xEEBB99,0xc7,F_WALL));
        map.put(SAND,new Tile(0xfad12d, 0x2E,F_WALK));
        map.put(WATER,new Tile(0x4d72eb, 0x7E,F_SWIM));
        map.put(LAVA,new Tile(0xeb520c, 0x7E));
        map.put(GRASS,new Tile(0x16a818,0x2C,F_WALK));
        map.put(PAVEMENT,new Tile(0x999999,0x86,F_WALK | F_BUILDING));
        map.put(ROCK,new Tile(0x999999, 0xF5));
        map.put(EARTH,new Tile(0x59390a, 0x2E,F_WALK));

        map.put(FOOD,new Tile(0xEEBB99, 0xE0,F_CONSOMMABLE));

        map.put(DOOR,new Tile(0x999999, 0x92));
        map.put(TABLE,new Tile(0x59390a, 0xD1,F_BLOCK));
        map.put(BED,new Tile(0xAAAACC, 0xE9,F_BLOCK));
        map.put(BUFFET,new Tile(0xBBAA99, 0xE3,F_BLOCK));
        map.put(CHAIR,new Tile(0xBBAA99, 0xD2));
        map.put(COFFER,new Tile(0x59390a, 0x08));
        map.put(STAIRS_UP,new Tile(0x999999, 0x3C));
        map.put(STAIRS_DOWN,new Tile(0x999999, 0x3E));
        map.put(VASE,new Tile(0x99AAFF, 0xEE));

        map.put(SPECTER,new Tile(0xFFFFFF, 0x84,F_MONSTER));
        map.put(HUMAN,new Tile(0xEEBB99, 0x40,F_MONSTER));
        map.put(SPIDER,new Tile(0xFFFFFF, 0xA1,F_MONSTER));
        map.put(ELF,new Tile(0xEEBB99, 0x8C,F_MONSTER));
        map.put(SKELETON,new Tile(0xFFFFFF, 0xEA,F_MONSTER));



    }

/*
 *   2          4
 * 1   3      2   8
 *   0          1
 */
    // N° de sprite pour les murs, en fonction des murs voisins
       //    00    01    10    11
    private final int[] WALL_CODE = {
            0xC7, 0xB7, 0xBE, 0xBB,   // 00
            0xBD, 0xBA, 0xBC, 0xB9,   // 01
            0xD4, 0xC9, 0xCD, 0xCB,   // 10
            0xC8, 0xCC, 0xCA, 0xCE};  // 11


    private final int color;
    final int code;
    final int flag;

    public Tile(int color, int code, int flag) {
        this.color = color;
        this.code = code;
        this.flag = flag;
    }

    public Tile(int color, int code) {
        this(color,code,0);
    }

    public boolean hasAllFlag(int flag){
        return (this.flag & flag) == flag;
    }

    public boolean hasOneFlag(int flag){
        return (this.flag & flag) != 0;
    }


    public int getCode(int x,int y, Level level){
        if (!hasAllFlag(WALL)) return code;
        int result = 0;
        for(int i = 0; i< 4; ++i){
            Tile neighbor = map.get(level.get(x,y,i));
            if (neighbor != null && neighbor.hasAllFlag(F_WALL)) result += 1 << i;
        }
        return WALL_CODE[result];
    }

    public static Tile get(int id){
        return map.get(id);
    }

    public static void paint(Canvas canvas, Level level, int x, int y){

        Tile tile = map.get(level.get(x,y));
        if (tile == null) return;

        // Affichage du fond (si nécessaire)
        if (!tile.hasOneFlag(F_NOBACK)) {
            level.spriteSheetBG.paint(canvas,
                    tile.getCode(x, y, level),
                    x * (level.spriteSheet.w),
                    y * (level.spriteSheet.h), tile.color);
        }

        // Affichage du sprite de terrain
        level.spriteSheet.paint(canvas,
                tile.getCode(x,y,level),
                x * (level.spriteSheet.w),
                y * (level.spriteSheet.h),
                tile.color);


        // Affichage du sprite de contenu
        Sprite sprite = level.getContent(x,y);
        if (sprite == null) return;
        tile = map.get(sprite.getId());
        if (tile == null) return;
        level.spriteSheet.paint(canvas,
                tile.getCode(x,y,level),
                x * (level.spriteSheet.w),
                y * (level.spriteSheet.h),
                tile.color);

    }


}
