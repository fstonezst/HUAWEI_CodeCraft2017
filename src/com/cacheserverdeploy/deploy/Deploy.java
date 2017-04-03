package com.cacheserverdeploy.deploy;

import com.cacheserverdeploy.graphoperat.Graph;
import com.cacheserverdeploy.matrix.MatriX;

import java.util.*;

public class Deploy {
    /**
     * 你需要完成的入口
     * <功能详细描述>
     *
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
    public static String[] deployServer(String[] graphContent) {
        String[] detail = graphContent[0].trim().split(" ");
        int vNum = Integer.parseInt(detail[0]);
        int eNum = Integer.parseInt(detail[1]);
        int cNum = Integer.parseInt(detail[2]);
        int cost = Integer.parseInt(graphContent[2].trim());

        Queue sNodeCap;
        TreeMap treeMap;
        TreeMap<Double,Integer> tmap;

        int[][] capacity = new int[vNum][vNum];
        int[][] fee = new int[vNum][vNum];

//        for (int i = 0; i < vNum; i++)
//            for (int j = 0; j < vNum; j++)
//                capacity[i][j] = Integer.MAX_VALUE;

//        for(int i = 0;i < vNum ;i++)
//            capacity[i][i] = 0;

        int line = 4;
        for (int i = 0; i < eNum; i++) {
            String[] edge = graphContent[line++].trim().split(" ");
            int start = Integer.parseInt(edge[0]);
            int end = Integer.parseInt(edge[1]);
            capacity[start][end] = Integer.parseInt(edge[2]);
            capacity[end][start] = capacity[start][end];
            fee[start][end] = Integer.parseInt(edge[3]);
            fee[end][start] = fee[start][end];
        }

        zstTest(capacity,fee);

        /**do your work here**/
        return new String[]{"17", "\r\n", "0 8 0 20"};
    }
    
    public static void zstTest(int[][] capacity, int[][] fee){

        int start = 24, end = 22, flow = 28;
        List<String> list = Graph.getAllFlowPath(start, end, flow, capacity, fee);
        for (String s : list)
            System.out.println(s);
    }



    /**
     * @param c       The Capability Mat
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
                sum += c[i][j];
            }
            re.add(new Pair(sum, i));
            sum = 0;
        }
        return re;
    }

    private static void printQueuePair(Queue<Pair> q){
        Iterator<Pair> it  = q.iterator();
        Pair tmp;// = new Pair();
        while (it.hasNext()) {
            tmp = it.next();
            System.out.printf("<%d,%d>\n", tmp.first, tmp.second);
        }
    }

    public static TreeMap sumNodeCap_TreeMap(int[][] c) {
        int sum = 0;
        TreeMap m = new TreeMap<Double,Integer>(new desPairCmp());
        Pair p;
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[0].length; j++) {
                sum += c[i][j];
            }
            m.put(new Pair(sum, i),i);
            sum = 0;
        }
        return m;
    }

    private static void printPairTreeMap(TreeMap map){

        if (map == null)
            return ;

        System.out.println("\nIterator TreeMap By Entry Set:\n");
        Pair key;
        Integer val;
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();

            key = (Pair)entry.getKey();
            val = (Integer)entry.getValue();
            System.out.printf("<<%d,%d>%d>\n",key.first,key.second,val);
        }


    }
}


