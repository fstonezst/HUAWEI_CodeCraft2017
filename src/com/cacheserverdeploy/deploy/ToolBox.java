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
    public static TreeMap topKTreeMap(TreeMap map, int k,float rate) {
        TreeMap re = new TreeMap<Double, Integer>(new desPairCmp());
        Pair key;
        Integer val;
        Iterator it = map.entrySet().iterator();
        int choiceRage = (int)(rate * map.size());
        Random r= new Random();
        while (choiceRage-- > 0 && it.hasNext()) {
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
            s1+=consumerNode[i][1];
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

    public static TreeMap sumNodeCap_TreeMap(int[][] c, HashSet<Integer> ss,HashSet<Integer> badSet) {
        int sum = 0;
        TreeMap m = new TreeMap<Double, Integer>(new desPairCmp());
        HashSet<Integer> s = new HashSet<>();
        s.addAll(ss);
        s.addAll(badSet);

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

    public static int[] getOutCap(int[][]c ){
        int[] outCap = new int[c.length];
        for(int i = 0; i< c.length-2;++i){
            for(int j = 0;j<c.length-2;j++){
                outCap[i] += c[i][j];
            }
        }
        outCap[c.length-2] = 0;
        outCap[c.length-1] = 0;
        for(int i = 0;i<c.length-2;i++){
            outCap[i]+= c[i][c.length-1];
        }
        return outCap;
    }

    public static HashSet<Integer> randomGetKServer(int[][] c,int k){
        int[] OutCap = getOutCap(c);
        HashSet<Integer> res = new HashSet<>();
        int length = 1000;
        int[] serverIdDis = new int[length];
        for(int i = 0;i<serverIdDis.length;i++){
            serverIdDis[i] = -1;
        }
        int totalCap = 0;
        for(int i=0; i<OutCap.length;i++)
            totalCap+= OutCap[i];
        int p =0;
        for(int id = 0;id < OutCap.length; id++){
            double rate = OutCap[id]/(double)totalCap;
            int l = (int)(length*rate);
            for(int j = p;j<p+l;j++){
                serverIdDis[j] = id;
            }
            p += l;
        }

        Random r = new Random();
        for(int i = 0; i<k;i++){
            int id = r.nextInt(length);
            while(serverIdDis[id] == -1 )
                id = r.nextInt(length);
            res.add(serverIdDis[id]);
        }
        return res;
    }

    public static void setServerCost(int[][] fee,HashSet<Integer> servers,int serverCost){
        for(int i:servers)
            fee[fee.length-2][i] = serverCost;
    }

    public static void setServerCap(int[][] cap, HashSet<Integer> servers, int[] outCap){
        for(int i:servers)
            cap[cap.length-2][i] = outCap[i];
    }

    public static void connectServers(int[][] cap,int[][] fee,HashSet<Integer> servers,int serverCost,int[] outCap){
        setServerCost(fee,servers,serverCost);
        setServerCap(cap,servers,outCap);
    }

    public static void connectConsumers(int[][] cap,int[][] consumers){
        for(int[] con:consumers)
            cap[con[0]][cap.length-1] = con[1];
    }

    public static int countPathListFee(List<String> paths,int[][] fee,int serverCost){
        int cost = 0;
        HashSet<Integer> server = new HashSet<>();
        for(String p:paths){
            String[] v =  p.trim().split(" ");
            server.add(Integer.parseInt(v[1]));
            int flow = Integer.parseInt(v[v.length-1]);
            for(int i = 1;i<v.length-3;i++){
                int start = Integer.parseInt(v[i]);
                int end = Integer.parseInt(v[i+1]);
                cost += fee[start][end] * flow;
            }
        }
        cost += server.size()*serverCost;
        return cost;
    }
}
