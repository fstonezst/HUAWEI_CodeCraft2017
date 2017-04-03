package com.cacheserverdeploy.matrix;
import com.cacheserverdeploy.deploy.Deploy;
import com.cacheserverdeploy.deploy.Pair;
import com.cacheserverdeploy.deploy.ToolBox;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by root on 17-4-3.
 */
public class MatriX {
    public static int[][] fullCapMat(int[][] capacity, int[][] fee, int cNum, double multiPower){
        int N = capacity.length;
        int K = (int)(cNum*multiPower);
        int[][] fm = new int[N+2][N+2];
        TreeMap map = ToolBox.sumNodeCap_TreeMap(capacity);
        TreeMap topKmap = ToolBox.topKTreeMap(map,K);
        ToolBox.copyTwoDArr(capacity,fm);
        for (int i = N;i<N+2;i++)
            for (int j = N;j<N+2;j++)
                fm[i][j]=0;

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
                    fm[val][N]=fm[N][val]=key.first;
        }

        /*init super END node: #N */
        for (int i = 0; i < N; i++)
            for (int j = i ; j < N; j++) {
                if (fee[i][j] != 0)
                    fm[j][N+1]=fm[N+1][j] = capacity[i][j];
            }

        return fm;
    }
}
