package com.cacheserverdeploy.deploy;

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

        int[][] capacity = new int[vNum][vNum];
        int[][] fee = new int[vNum][vNum];
        int[][] consumerNode = new int[cNum][2];


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

        line ++;
        for(int i = 0; i<cNum;i++){
            String[] consumer = graphContent[line++].trim().split(" ");
            consumerNode[i][0] = Integer.parseInt(consumer[1]);
            consumerNode[i][1] = Integer.parseInt(consumer[2]);
        }

//        for (int[] s : consumerNode)
//            System.out.println(s[0]+" " + s[1]);

        zstTest(capacity,fee);

        /**do your work here**/
        return new String[]{"17", "\r\n", "0 8 0 20"};
    }
    
    public static void zstTest(int[][] capacity, int[][] fee){


       /* int start = 24, end = 22, flow = 28;
        List<String> list = Graph.getAllFlowPath(start, end, flow, capacity, fee);
        for (String s : list)
            System.out.println(s);*/
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

}


