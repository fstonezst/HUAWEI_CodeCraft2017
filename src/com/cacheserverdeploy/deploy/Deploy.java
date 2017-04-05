package com.cacheserverdeploy.deploy;

import com.cacheserverdeploy.graphoperat.Graph;
//import com.cacheserverdeploy.matrix.MatriX;

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
        int serverCost = Integer.parseInt(graphContent[2].trim());

        int[][] capacity = new int[vNum][vNum];
        int[][] fee = new int[vNum][vNum];
        int[][] consumerNode = new int[cNum][2];

        for(int i = 0;i<fee.length;i++)
            for(int j=0;j<fee[i].length;j++)
                fee[i][j] = Integer.MAX_VALUE;



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
            int consumerId = Integer.parseInt(consumer[0]);
            consumerNode[consumerId][0] = Integer.parseInt(consumer[1]);
            consumerNode[consumerId][1] = Integer.parseInt(consumer[2]);
        }

//        for (int[] s : consumerNode)
//            System.out.println(s[0]+" " + s[1]);

//        zstTest(capacity, fee,consumerNode,serverCost);
//        List<int[][]> l = new LinkedList<>();
//        HashSet<Integer> s = new HashSet<>();
//        MatriX.initBothMat(l,s,capacity,fee,consumerNode,serverCost,3);        // init with k
//        MatriX.initBothMat(l,s,capacity,fee,consumerNode,serverCost,1.5);   // init with multi
//
//        MatriX.updateBothMat(l,s,capacity,fee,consumerNode,serverCost,3);      // update with k
//        MatriX.updateBothMat(l,s,capacity,fee,consumerNode,serverCost,1.5); // update with multi

        int start = capacity.length, end = capacity.length+1, flow = 0;
        for(int[] c:consumerNode)
            flow += c[1];



//        serverCost = 300;
        List<String> list = Graph.getAllFlowPath(start, end, flow, capacity, fee, consumerNode,serverCost);

//        for (String s : list)
//            System.out.println(s);

        String[] result = new String[list.size()+2];
        result[0] = Integer.toString(list.size());
        result[1] = "\r\n";
        Iterator<String> it = list.iterator();
        for(int i=2;i<result.length;i++){
            result[i] = it.next();
        }


        /**do your work here**/
//        return new String[]{list.size()+"\r\n", "0 8 0 20"};
        return result;
    }


    public static void zstTest(int[][] capacity, int[][] fee, int[][] consumerNode,int serverCost) {

//        int[][] newCap = MatriX.fullCapMat(capacity,consumerNode,1.2);
//        TreeMap map = ToolBox.topKTreeMap(ToolBox.sumNodeCap_TreeMap(newCap),1);
//        int[][] newFee = MatriX.fullFeeMat(fee,consumerNode,map,serverCost);
//
        int start = capacity.length, end = capacity.length+1, flow = 0;
        for(int[] c:consumerNode)
            flow += c[1];
        List<String> list = Graph.getAllFlowPath(start, end, flow, capacity, fee, consumerNode,serverCost);
        for (String s : list)
            System.out.println(s);

    }

}


