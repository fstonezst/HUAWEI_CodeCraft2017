package com.cacheserverdeploy.deploy;

import java.util.Comparator;

/**
 * Created by root on 17-4-2.
 */
public class Pair{
    public int first;
    public int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public Pair(){
        this.first = 0;
        this.second = 0;
    }
}

/**
 * TreeMap's Comparator
 */
class desPairCmp implements Comparator {
    public int compare(Object o1,Object o2)
    {
        Pair p1 = (Pair)o1;
        Pair p2 = (Pair)o2;
        if(p1.first!=p2.first)
            return p2.first-p1.first;
        else
            return p2.second-p2.second;
    }
}
