package fr.iutlens.mmi.rogue;

/**
 * Created by dubois on 30/12/2019.
 */

class Sprite {
    public float prob;
    protected int id;
    int x,y;

    public Sprite(int tileId, int x, int y) {
        this.id = tileId;
        this.x = x;
        this.y = y;
        this.prob = 1;
    }

    public Sprite(int id, int x, int y, float prob) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.prob = prob;
    }

    public Sprite clone(){
        return new Sprite(id,x,y,prob);
    }

    public boolean getProb(){
        if (prob == 1) return true;
        return Math.random()<= prob;
    }

    int getId(){
        return id;
    }

    boolean block(){
        return Tile.get(id).hasOneFlag(Tile.F_BLOCK);
    }

    public void effect(Level level, Hero hero) {
        if (Tile.get(id).hasOneFlag(Tile.F_MONSTER|Tile.F_CONSOMMABLE)){
            level.removeContent(x,y);
        }
    }
}
