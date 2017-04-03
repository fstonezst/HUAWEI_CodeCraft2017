package com.cacheserverdeploy.matrix;
import com.cacheserverdeploy.deploy.Pair;
import com.cacheserverdeploy.deploy.ToolBox;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by root on 17-4-3.
 */
public class MatriX {
    /**
     *
     * @param capacity             Nodes' Capability Mat
     * @param consumerNode         Clients' consume Mat
     * @param multiPower           multiply factor
     * @return                     Full Capability Mat
     */
    public static int[][] fullCapMat(int[][] capacity, int[][] consumerNode, double multiPower){
        int N = capacity.length;
        int K = (int)(consumerNode.length*multiPower);
        K = K>N?N:K;
        int[][] mat = new int[N+2][N+2];
        TreeMap map = ToolBox.sumNodeCap_TreeMap(capacity);
        TreeMap topKmap = ToolBox.topKTreeMap(map,K);
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
            for (int i = 0; i < N; i++)
                if (i == val)
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
     * @param fee                     fee Mat
     * @param consumerNode            clients' consume Mat
     * @param topKmap                 Top K node
     * @param server_cost            server's cost
     * @return
     */
    public static int[][] fullFeeMat(int[][] fee, int[][] consumerNode, TreeMap topKmap, int server_cost) {
        int N = fee.length;
        int M = consumerNode.length;
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

}
