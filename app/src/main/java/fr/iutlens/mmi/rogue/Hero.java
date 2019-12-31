package fr.iutlens.mmi.rogue;

import android.graphics.Canvas;

import fr.iutlens.mmi.rogue.util.Coordinate;
import fr.iutlens.mmi.rogue.util.SpriteSheet;

/**
 * Created by dubois on 24/12/2019.
 */

class Hero {
    private final SpriteSheet spriteSheet;
    private final int id;
    private  int x;
    private  int y;

    public Hero(int tileset, int id, int x, int y) {
        this.spriteSheet = SpriteSheet.get(tileset);
        this.id = id;
        this.x = x;
        this.y = y;
    }


    public void paint(Canvas canvas) {
        spriteSheet.paint(canvas,id,x*spriteSheet.w,y*spriteSheet.h,0xffc1b5);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMobility() {
        return Tile.F_WALK | Tile.F_SWIM;
    }

    public void move(int dir) {
        x += Coordinate.dir_coord[dir][0];
        y += Coordinate.dir_coord[dir][1];
    }
}
