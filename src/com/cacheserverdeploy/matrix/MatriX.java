package com.cacheserverdeploy.matrix;
import com.cacheserverdeploy.deploy.Pair;
import com.cacheserverdeploy.deploy.ToolBox;

import java.util.*;

/**
 * Created by root on 17-4-3.
 */
public class MatriX {

    /**
     *
     * @param capacity             Nodes' Capability Mat
     * @param consumerNode         Clients' consume Mat
     * @param topKmap              Top-K Largest Capability Node
     * @return                     Full Capability Mat
     */
    public static int[][] initCapMat(int[][] capacity, int[][] consumerNode, TreeMap topKmap){
        int N = capacity.length;
        int[][] mat = new int[N+2][N+2];
        ToolBox.copyTwoDArr(capacity, mat);
        for (int i = N;i<N+2;i++)
            for (int j = N;j<N+2;j++)
                mat[i][j]=0;

        /*init super START node: #N */
        Pair key;
        Integer val;
        Iterator it = topKmap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            key = (Pair)entry.getKey();
            val = (Integer)entry.getValue();
            /*
            for (int i = 0; i < N; i++)
                if (i == val)
                */
                    mat[val][N]= mat[N][val]=key.first;
        }

        /*init super END node: #N */
        int neighbour;
        for (int i = 0; i < consumerNode.length; i++) {
            if (consumerNode[i][1] != 0) {
                neighbour = consumerNode[i][0];  // [array_index]:client>>[0:neighbour,1:need_cost]
                mat[neighbour][N + 1] = mat[N + 1][neighbour] = consumerNode[i][1]; //
            }
        }
        return mat;
    }

    /**
     *
     * @param mat                  Nodes' Capability Mat
     * @param consumerNode         Clients' consume Mat
     * @param topKmap              Top-K Largest Capability Node
     * @return                     Full Capability Mat
     */
    public static int[][] fullCapMat(int[][] mat, int[][] consumerNode, TreeMap topKmap){
        int N = mat.length-2; // node server's Mat index, in (N+2)*(N+2) Format

        /*update super START node: #N */
        Pair key;
        Integer val;
        Iterator it = topKmap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            key = (Pair)entry.getKey();
            val = (Integer)entry.getValue();
            for (int i = 0; i < N; i++)
                if (i == val)
                    mat[val][N]= mat[N][val]=key.first;
        }
        return mat;
    }

    /**
     *
     * @param fee                     fee Mat
     * @param consumerNode            clients' consume Mat
     * @param topKmap                 Top K node
     * @param server_cost             server's cost
     * @return
     */
    public static int[][] fullFeeMat(int[][] fee, int[][] consumerNode, TreeMap topKmap, int server_cost) {
        int N = fee.length;
        int[][] mat = new int[N + 2][N + 2];
        ToolBox.copyTwoDArr(fee, mat);

        /*init Top-K server's fee: #N */
        Pair key;
        Integer val;
        Iterator it = topKmap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            key = (Pair) entry.getKey();
            val = (Integer) entry.getValue();
            for (int i = 0; i < N; i++)
                if (i == val)
                    mat[val][N] = mat[N][val] = server_cost;
        }

        /*init super END node's fee: #N */
        int neighbour;
        for (int i = 0; i < consumerNode.length; i++) {
            if (consumerNode[i][1] != 0) {
                neighbour = consumerNode[i][0];  // [array_index]:client>>[0:neighbour,1:need_cost]
                mat[neighbour][N + 1] = mat[N + 1][neighbour] = 0; // INT_MAX -> 0
            }
        }
        return mat;
    }

    public static HashSet<Integer> initBothMat(List<int[][]> l, HashSet<Integer> s, int[][] capacity, int[][] fee, int[][] consumerNode, int server_cost, int k){
        HashSet<Integer> re;
        TreeMap map,topKmap;
        map = ToolBox.sumNodeCap_TreeMap(capacity,s,s);
        topKmap = ToolBox.topKTreeMap(map,k);
        re = MatriX.treeMapToSet(topKmap);
        l.add(MatriX.initCapMat(capacity,consumerNode,topKmap));
        l.add(MatriX.fullFeeMat(fee,consumerNode,topKmap,server_cost));
        return re;
    }

    public static HashSet<Integer> initBothMat(List<int[][]> l,HashSet<Integer> s, int[][] capacity,int[][] fee, int[][] consumerNode,int server_cost,double multi){
        HashSet<Integer> re;
        TreeMap map,topKmap;
        map = ToolBox.sumNodeCap_TreeMap(capacity,s,s);
        topKmap = ToolBox.topKTreeMap(map, consumerNode,multi);
        re = MatriX.treeMapToSet(topKmap);
        l.add(MatriX.initCapMat(capacity,consumerNode,topKmap));
        l.add(MatriX.fullFeeMat(fee,consumerNode,topKmap,server_cost));
        return re;
    }


    public static HashSet<Integer> updateBothMat(HashSet<Integer> badCase, HashSet<Integer> s, int[][] capacity, int[][] fee, int[][] consumerNode, int server_cost, int k){

        HashSet<Integer> re;
        TreeMap map,topKmap;
        map = ToolBox.sumNodeCap_TreeMap(capacity,s,badCase);
        topKmap = ToolBox.topKTreeMap(map,k);

        int sum ;
        for(int i:s){
            sum = 0;
            for(int j = 0;j<capacity[i].length;j++)
                sum+=capacity[i][j];
            topKmap.put(new Pair(sum,i),i);
        }

        re = MatriX.treeMapToSet(topKmap);

        MatriX.fullCapMat(capacity,consumerNode,topKmap);
        MatriX.fullFeeMat(fee,consumerNode,topKmap,server_cost);

        for( int serverId : s){
            fee[fee.length-2][serverId] = 0;
        }

        return re;
    }

    public static HashSet<Integer> updateBothMat(List<int[][]> l, HashSet<Integer> s, int[][] capacity,int[][] fee, int[][] consumerNode,int server_cost,double multi){
        HashSet<Integer> re;
        TreeMap map,topKmap;
        map = ToolBox.sumNodeCap_TreeMap(capacity,s,s);
        topKmap = ToolBox.topKTreeMap(map, consumerNode,multi);
        re = MatriX.treeMapToSet(topKmap);
        l.add(MatriX.fullCapMat(capacity,consumerNode,topKmap));
        l.add(MatriX.fullFeeMat(fee,consumerNode,topKmap,server_cost));
        return re;
    }

    public static HashSet<Integer> treeMapToSet(TreeMap m){
        HashSet<Integer> s = new HashSet<>();
        Iterator it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer val = (Integer) entry.getValue();
            s.add(val);
        }
        return s;

    }

}
