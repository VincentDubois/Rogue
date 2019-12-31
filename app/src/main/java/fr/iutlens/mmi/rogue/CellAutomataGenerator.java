package fr.iutlens.mmi.rogue;

import fr.iutlens.mmi.rogue.util.Coordinate;

/**
 * Created by dubois on 25/12/2019.
 */

class CellAutomataGenerator {
    final Coordinate coord;
    int[] data;
    int[] buffer;

    public CellAutomataGenerator(int x, int y) {
        coord = new Coordinate(x,y);
        data = new int[x*y];
        buffer = new int[x*y];
    }

    public void cave(int def){
        init(0.4f);
        for(int i = 0; i<2 ; ++i) iterate(5,-1,def);
//        for(int i = 0; i<1 ; ++i) iterate(6,2,1);
        for(int i = 0; i<3 ; ++i) iterate(4,-1,def);
    }

    public void init(float p){
        for(int i = 0; i< data.length; ++i){
            data[i] = Math.random()<p ? 1 : 0;
        }
    }

    public int count(int x, int y, int def){
        int result = 0;
        for(int dx = -1; dx <= 1; ++dx)
            for(int dy = -1; dy <=1; ++dy){
                int ndx = coord.getNdx(x+dx,y+dy);
                if (ndx == -1){
                    if (y+dy>=coord.sizeY) result += ((def&1) == 0) ? 0 : 1;
                    else if (x+dx<0) result += ((def&2) == 0) ? 0 : 1;
                    else if (y+dy<0) result += ((def&4) == 0) ? 0 : 1;
                    else result += ((def&8) == 0) ? 0 : 1;
                } else if (data[ndx]==1) ++result;
            }
        return  result;
    }

    public void iterate(int min, int max, int def){
        for(int x = 0; x < coord.sizeX; ++x)
            for(int y = 0; y < coord.sizeY; ++y){
                int c = count(x,y,def);
                buffer[coord.getNdx(x,y)] = ( (c <=max) || (c>=min)) ? 1 : 0;
            }
        int [] tmp = data;
        data = buffer;
        buffer = tmp;
    }

    public void writeTo(Level level, int x0, int y0, int code0, int code1){
        for(int x = 0; x < coord.sizeX; ++x)
            for(int y = 0; y < coord.sizeY; ++y){
                level.set(x0+x,y0+y, data[coord.getNdx(x,y)] == 0? code1 : code0);
            }
    }
}
