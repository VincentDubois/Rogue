package fr.iutlens.mmi.rogue;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by dubois on 24/12/2019.
 */

class RandomChooser<T> {
    private Random rand = new Random();

    private class Choice{

        private final T description;
        private final int size;
        private final int weight;

        public Choice(T description, int size, int weight) {
            this.description = description;
            this.size = size;
            this.weight = weight;
        }
    }

    Vector<Choice> v;

    RandomChooser(){
        v = new Vector<>();
    }

    RandomChooser(T[] list){
        v = new Vector<>();

        for(T t : list){
            addOption(t,1,1);
        }
    }

    public void addOption(T description, int size, int weight) {
        v.add(new Choice(description,size,weight));
    }

    private Choice getOne(Vector<Choice> possible){
        int sumW = 0;
        for(Choice c : v) sumW+= c.weight;
        if (sumW <=0) return null; //FIXME
        sumW = rand.nextInt(sumW);
        for(Choice c :possible){
            sumW -=c.weight;
            if (sumW <0){
                return c;
            }
        }
        return null;
    }

    public T getOne(){
        final Choice choice = getOne(v);
        if (choice==null) return null;
        return choice.description;
    }

    public T getOne(T except){
        Vector<Choice> possible = new Vector<>(v);

        Iterator it = possible.iterator();
        while (it.hasNext()){
            if (except.equals(it.next())) it.remove();
        }
        return getOne(possible).description;
    }


    public Vector<T> get(int maxSize){
        Vector<T> result = new Vector<>();
        Vector<Choice> possible = new Vector<>();

        for(Choice c : v) if (c.size <= maxSize) possible.add(c);

        while (maxSize >0 && possible.size()>0) {
            Choice c = getOne(possible);
            if (c == null) return result; // FIXME
            result.add(c.description);
            maxSize -= c.size;


            Iterator<Choice> it = possible.iterator();
            while(it.hasNext()) if (it.next().size> maxSize) it.remove();
        }
        return  result;
    }
}
