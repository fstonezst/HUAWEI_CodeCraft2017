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

        line++;
        for (int i = 0; i < cNum; i++) {
            String[] consumer = graphContent[line++].trim().split(" ");
            consumerNode[i][0] = Integer.parseInt(consumer[1]);
            consumerNode[i][1] = Integer.parseInt(consumer[2]);
        }

//        for (int[] s : consumerNode)
//            System.out.println(s[0]+" " + s[1]);

        zstTest(capacity, fee);

        /**do your work here**/
        return new String[]{"17", "\r\n", "0 8 0 20"};
    }

    public static void zstTest(int[][] capacity, int[][] fee) {


       /* int start = 24, end = 22, flow = 28;
        List<String> list = Graph.getAllFlowPath(start, end, flow, capacity, fee);
        for (String s : list)
            System.out.println(s);*/
    }


}


