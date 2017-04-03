package com.cacheserverdeploy.deploy;

import java.util.*;

/**
 * Created by peter on 2017/4/2.
 */
public class ToolBox {
    /**
     * 二维int型数组复制
     *
     * @param src  源数组
     * @param dest 目标数组
     */
    public static void copyTwoDArr(int[][] src, int[][] dest) {
        for (int i = 0; i < src.length; i++)
            System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
    }

    /**
     * 打印一个int型二维数组
     *
     * @param m 二维数组
     */
    public static void printMatri(int[][] m) {
        System.out.println();
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; ++j) {
                if (m[i][j] < Integer.MAX_VALUE) {
                    System.out.print(m[i][j] + " ");
                } else {
                    System.out.print(-1 + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * 按照输入文件的格式在控制台上打印图
     *
     * @param c 容量表
     * @param f 费用表
     */
    public static void printG(int[][] c, int[][] f) {
        for (int i = 0; i < c.length; i++)
            for (int j = 0; j < c[0].length; j++)
                if (c[i][j] < Integer.MAX_VALUE)
                    System.out.println(i + " " + j + " " + c[i][j] + " " + f[i][j]);
    }

    /**
     * Print Pair-type in Queue
     *
     * @param q
     */
    public static void printQueuePair(Queue<Pair> q) {
        Iterator<Pair> it = q.iterator();
        Pair tmp;// = new Pair();
        while (it.hasNext()) {
            tmp = it.next();
            System.out.printf("<%d,%d>\n", tmp.first, tmp.second);
        }
    }

    /**
     * Print Pair-type in TreeMap
     *
     * @param map
     */
    public static void printPairTreeMap(TreeMap map) {
        if (map == null)
            return;
        System.out.println("\nIterator TreeMap By Entry Set:\n");
        Pair key;
        Integer val;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();

            key = (Pair) entry.getKey();
            val = (Integer) entry.getValue();
            System.out.printf("<<%d,%d>%d>\n", key.first, key.second, val);
        }
    }


    public static TreeMap topKTreeMap(TreeMap map, int k) {
        TreeMap re = new TreeMap<Double, Integer>(new desPairCmp());
        Pair key;
        Integer val;
        Iterator it = map.entrySet().iterator();
        while (k-- > 0 && it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            key = (Pair) entry.getKey();
            val = (Integer) entry.getValue();
            re.put(key, val);
        }
        return re;
    }


    public static TreeMap topKTreeMap(TreeMap map, int[][] consumerNode, double multi) {
        TreeMap re = new TreeMap<Double, Integer>(new desPairCmp());
        Pair key;
        Integer val, s1=0,s2=0,TH;

        for(int i =0;i<consumerNode.length;i++)
            s1+=consumerNode[i][2];
        TH = (int)(s1*multi);

        Iterator it = map.entrySet().iterator();
        while (s2 < TH && it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            key = (Pair) entry.getKey();
            val = (Integer) entry.getValue();
            s2 += key.first;
            re.put(key, val);
        }
        return re;
    }

    /**
     * @param c The Capability Mat
     * @return Queue<Pair>
     */
    private static Queue<Pair> sumNodeCap(int[][] c) {
        int sum = 0;
        Comparator<Pair> cmp;
        cmp = new Comparator<Pair>() {
            public int compare(Pair e1, Pair e2) {
                if (e2.first != e1.first) {
                    return e2.first - e1.first;
                } else {
                    return e2.second - e1.second;
                }
            }
        };
        Queue<Pair> re = new PriorityQueue<>(c.length, cmp);
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[0].length; j++) {
                sum += c[i][j]; // sum of out-flow from node[i]
            }
            re.add(new Pair(sum, i));
            sum = 0;
        }
        return re;
    }

    public static TreeMap sumNodeCap_TreeMap(int[][] c, HashSet<Integer> s) {
        int sum = 0;
        TreeMap m = new TreeMap<Double, Integer>(new desPairCmp());
        Pair p;
        for (int i = 0; i < c.length; i++) {
            if(s.contains(i))
                continue;
            for (int j = 0; j < c[0].length; j++) {
                sum += c[i][j];    // sum of out-flow from node[i]
            }
            m.put(new Pair(sum, i), i);
            sum = 0;
        }
        return m;
    }
}
