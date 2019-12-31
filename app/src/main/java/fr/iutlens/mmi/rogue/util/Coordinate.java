package fr.iutlens.mmi.rogue.util;

/**
 * Created by dubois on 23/12/2019.
 */

public class Coordinate {
    public final int sizeX,sizeY;

    public static final int[][] dir_coord= {{0,1},{-1,0},{0,-1},{1,0}};

    public Coordinate(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public int getNdx(int x, int y){
        if ((x<0) || (x>= sizeX)) return -1;
        if ((y<0) || (y>= sizeY)) return -1;
        return x*sizeY+y;
    }

    public int getX(int ndx){
        return ndx/sizeY;
    }

    public int getY(int ndx){
        return ndx%sizeY;
    }

    public int getNext(int ndx, int dir){
        return getNdx(getX(ndx)+dir_coord[dir][0], getY(ndx)+dir_coord[dir][1]);

    }

    public int getNext(int x, int y, int dir) {
        return getNdx(x+dir_coord[dir][0], y+dir_coord[dir][1]);
    }

    public int getNext(int x, int y, int dir, int n) {
        return getNdx(x+n*dir_coord[dir][0], y+n*dir_coord[dir][1]);
    }

}
