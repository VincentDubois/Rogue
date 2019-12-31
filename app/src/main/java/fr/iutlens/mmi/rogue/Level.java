package fr.iutlens.mmi.rogue;

import android.graphics.Canvas;

import java.util.HashMap;
import java.util.Map;

import fr.iutlens.mmi.rogue.util.Coordinate;
import fr.iutlens.mmi.rogue.util.SpriteSheet;

/**
 * Created by dubois on 27/12/2017.
 */

public class Level {
    public final Coordinate coord;
    public final SpriteSheet spriteSheet;
    public final SpriteSheet spriteSheetBG;

    private boolean ready;
    private int start;

    private  int[] data;
    private Map<Integer,Sprite> content;
    protected final Zone[] zone;

    public Level(int sizeX, int sizeY, int sprite_id, int sprite_bg_id){
        spriteSheet = SpriteSheet.get(sprite_id);
        spriteSheetBG = SpriteSheet.get(sprite_bg_id);
        coord = new Coordinate(sizeX,sizeY);
        data = new int[sizeX*sizeY];
        content = new HashMap<>();
        zone = new Zone[9];
        ready = false;
    }

    public void generate(){

        Zone.generateZone(this);

        RoomGenerator generator = new RoomGenerator(this);
        generator.generate();
        ready = true;
    }

    public void paint(Canvas canvas, int x, int y, int radius){
        if (!ready) return;
        for(int i = x-radius; i < x+radius ; ++i) {
            for (int j = y-radius; j < y+radius; ++j) {
                Tile.paint(canvas, this, i ,j);
            }
        }
    }

    public Sprite getContent(int x, int y) {
        return content.get(coord.getNdx(x,y));
    }
    public Sprite getContent(int ndx) {
        return content.get(ndx);
    }

    public void setContent(int x, int y, Sprite sprite){
        content.put(coord.getNdx(x,y),sprite);
        sprite.x = x;
        sprite.y = y;
    }
    public void setContent(int ndx, Sprite sprite){
        content.put(ndx,sprite);
        sprite.x = coord.getX(ndx);
        sprite.y = coord.getY(ndx);
    }

    public void removeContent(int ndx){
        content.remove(ndx);
    }
    public void removeContent(int x, int y){
        removeContent(coord.getNdx(x,y));
    }



    public int get(int x, int y){
        int ndx = coord.getNdx(x,y);
        if (ndx==-1) return -1;
        return data[ndx];
    }
    public int get(int ndx) {
        if (ndx==-1) return -1;
        return data[ndx];
    }


    public int get(int x, int y, int dir){
        int ndx = coord.getNext(x,y,dir);
        if (ndx==-1) return -1;
        return data[ndx];
    }

    public void set(int x, int y, int value){
        data[coord.getNdx(x,y)] = value;
    }

    public void set(int ndx, int tile) {
        data[ndx] = tile;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    public int getSizeY(){
        return coord.sizeY;
    }
    public int getSizeX(){
        return coord.sizeX;
    }

    public int getTileWidth(){
        return spriteSheet.w;
    }
    public int getTileHeight(){
        return spriteSheet.h;
    }



    private void fillRectangle(int x0, int y0, int x1, int y1, int code) {
        for(int x = x0; x < x1; ++x)
            for(int y = y0; y < y1; ++y) set(x,y,code);
    }
    private void rectangle(int x0, int y0, int x1, int y1, int code) {
        for(int x = x0; x <= x1; ++x) {
            set(x,y0,code);
            set(x,y1,code);
        }
        for(int y = y0; y <= y1; ++y){
            set(x0,y,code);
            set(x1,y,code);
        }
    }


}
