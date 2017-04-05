package com.cacheserverdeploy.graphoperat;

import java.util.*;
import java.util.logging.Logger;

import com.cacheserverdeploy.deploy.ToolBox;
import com.cacheserverdeploy.matrix.MatriX;

/**
 * Created by peter on 2017/4/3.
 */
public class Graph {

    /**
     * SPFA算法实现
     * 在可能存在负边的图中找出start到图中所有点的最短路径(最小费用)
     *
     * @param start 出发点
     * @param f     费用表
     * @return start到各点,
     */
    private static int[] SPFA(int start, int[][] f, int[][] c) {
        Deque<Integer> queue = new LinkedList<>();
        int vNum = f.length;

        int[] post = new int[vNum];
        int[] minCap = new int[vNum];
        int qDstSum = 0; //队列中元素到起点的距离加和
        int[] res = new int[vNum];
        int[] times = new int[vNum];
        boolean[] isIn = new boolean[vNum];
        for (int i = 0; i < vNum; ++i) {
            times[i] = 0;
            isIn[i] = false;
            res[i] = Integer.MAX_VALUE;
            post[i] = -1;
            minCap[i] = Integer.MAX_VALUE;
        }

        queue.add(start);
        times[start] = 1;
        res[start] = 0;
        isIn[start] = true;
        while (queue.size() != 0) {
            //LLL优化
            int avg = qDstSum / queue.size();
            while (res[queue.peekFirst()] > avg)
                queue.addLast(queue.removeFirst());

            int a = queue.removeFirst();
            qDstSum -= res[a];
            for (int i = 0; i < vNum; i++) {
//                if (f[a][i] == 0 || c[a][i] <= 0)
                if (c[a][i] <= 0)
                    continue;
                int dst = res[a] + f[a][i];
                int cap = minCap[a] < c[a][i] ? minCap[a] : c[a][i];

                //如果距离更近或者距离相等但是容量更大则更新路径，
                if (res[i] > dst) {// || (res[i] == dst && cap>minCap[i])) {
                    post[i] = a;
                    if (minCap[i] > c[a][i])
                        minCap[i] = c[a][i];
                    res[i] = dst;
                    if (!isIn[i]) {

//                        if(res[queue.peekFirst()] >= res[i])
//                            queue.addFirst(i);
//                        else
                        queue.addLast(i);

                        qDstSum += res[i];

                        times[i]++;
                        if (times[i] > vNum)
                            return null;
                        isIn[i] = true;
                    }
                }
            }
            isIn[a] = false;
        }
        return post;
    }

    /**
     * 从post中获取路径
     *
     * @param end  路径的最后一个节点编号
     * @param post 保存路径中每个节点的前驱节点标号
     * @return 路径
     */
    private static List<Integer> getPath(int start, int end, int[] post) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        if (post == null)
            return list;
        int p = end;
        list.add(p);
        while (post[p] != -1) {
            list.addFirst(post[p]);
            p = post[p];
        }
        if (p != start)
            return null;
        return list;
    }

    /**
     * 找出路径中容量最小的那段路径的容量大小
     *
     * @param path 路径
     * @param cap  图的容量
     * @return 最小容量
     */

    private static int getMinCap(List<Integer> path, int[][] cap) {
        if (path == null || path.size() < 2) return 0;
        int min = Integer.MAX_VALUE;
        Iterator<Integer> it = path.iterator();
        int from = it.next();
        while (it.hasNext()) {
            int to = it.next();
            if (min > cap[from][to])
                min = cap[from][to];
            from = to;
        }
        return min;
    }

    /**
     * 保持超级源点与已选服务器的连接
     *
     * @param residualCap 残余容量图
     * @param residualFee 残余费用图
     */
    private static void maintainServerConnect(int[][] residualCap, int[][] residualFee) {
        int superStartId = residualFee.length - 2;
        for (int i = 0; i < residualFee[superStartId].length; i++) {
            if (residualFee[superStartId][i] == 0 && residualCap[superStartId][i] == 0) {
                for (int j = 0; j < residualCap[i].length; j++)
                    residualCap[superStartId][i] += residualCap[i][j];
            }
        }
    }

    /**
     * 根据新的增广路径设置回流、回流的容量以及流量图
     *
     * @param path        路径
     * @param flow        路径流量
     * @param cap         初始网络容量
     * @param residualCap 残余网络容量
     * @param residualFee 残余网络费用
     * @param flowGraph   流量图
     */
    private static void reSetGraph(List<Integer> path, int flow, int[][] cap, int[][] residualCap, int[][] residualFee, FlowGraph flowGraph) {
        Iterator<Integer> it = path.iterator();
        int start1 = path.get(0), start2 = path.get(1);
//        int end1 = path.get(path.size()-2),end2 = path.get(path.size()-1);

        residualFee[start1][start2] = 0;

        int from = it.next();
        while (it.hasNext()) {
            int to = it.next();

            //1.设置流量图，如果正向流量大于逆向流量则将正向流量设为正向流量减去逆流
            //否则将逆流减去正流
            int backFlow = flowGraph.getTheEdgeFlow(to, from);
            int oldFlow;
            if (flow > backFlow) {
                oldFlow = flowGraph.getTheEdgeFlow(from, to);

                flowGraph.setEdgeFlow(from, to, flow + oldFlow - backFlow);
                flowGraph.setEdgeFlow(to, from, 0);
            } else {
                flowGraph.setEdgeFlow(to, from, backFlow - flow);
            }

            //2.设置残余网络
            //将该路径的容量直接减去流量
            //subject to  flow <= cap[from][to]
            residualCap[from][to] -= flow;

            //如果该路径费用为负，则流量经过的路径为回流，
            //如果回流的剩余容量等于0则将该回流去掉，重设为初始路径
            if (residualFee[from][to] < 0 && residualCap[from][to] == 0) {
                residualCap[from][to] = cap[from][to];
                residualFee[from][to] *= -1;
            } else if (residualFee[from][to] > 0) {
                //如果该路径的费用为正则需要设置回流
                //如果回流已经存在则将回流的容量加上流量
                //否则设置一条回流
                if (residualFee[to][from] > 0) {
                    residualCap[to][from] = flow;
                    residualFee[to][from] *= -1;
                } else {
                    residualCap[to][from] += flow;
                }
            }
            from = to;
        }

    }

    /**
     * 生成从start到end的最小费用流量图
     * 若存在从start到end 带宽为flow的路径则返回true，否则返回false
     *
     * @param start 起点
     * @param end   终点
     * @param flow  流量
     * @param cap   容量图
     * @param fee   费用图
     * @param flowGraph 流量图，用于返回
     * @return 是否可以得到满足流量大小为flow的路径
     *//*
    private static int minFeeFlow(int start, int end, int flow, int[][] cap, int[][] fee, FlowGraph flowGraph) {
        int vNum = cap.length;
        int[][] residualFee = new int[vNum][vNum]; //残余费用图
        int[][] residualCap = new int[vNum][vNum]; //残余容量图
        int pathFlow = 0;  //增广路径流量
        ToolBox.copyTwoDArr(fee, residualFee);
        ToolBox.copyTwoDArr(cap, residualCap);

        int flowSum = 0; //已获取的流量和

        while (flowSum < flow) {
            int[] post = SPFA(start, residualFee, residualCap);
            List<Integer> path = getPath(start, end, post);
            if (path == null)
                break;
            pathFlow = getMinCap(path, residualCap);
            if (pathFlow == 0)
                break;
            pathFlow = pathFlow > flow ? flow : pathFlow;
            reSetGraph(path, pathFlow, cap, residualCap, residualFee, flowGraph);
            flowSum += pathFlow;
        }
        return flowSum;
    }*/

    /**
     * 生成从start到end的最小费用流量图
     * 若存在从start到end 带宽为flow的路径则返回true，否则返回false
     *
     * @param start     起点
     * @param end       终点
     * @param flow      流量
     * @param cap       容量图
     * @param flowGraph 流量图，用于返回
     * @return 是否可以得到满足流量大小为flow的路径
     */
    private static HashMap<Integer, Integer> minFeeFlow(int start, int end, int flow, int[][] cap, int[][] residualCap, int[][] residualFee, FlowGraph flowGraph) {

//        int vNum = cap.length;
//        int[][] residualFee = new int[vNum][vNum]; //残余费用图
//        int[][] residualCap = new int[vNum][vNum]; //残余容量图
//        ToolBox.copyTwoDArr(fee, residualFee);
//        ToolBox.copyTwoDArr(cap, residualCap);

        int pathFlow;  //增广路径流量
        int flowSum = 0; //已获取的流量和

        List<List<Integer>> list = new ArrayList<>();

        while (flowSum < flow) {
            int[] post = SPFA(start, residualFee, residualCap);
            List<Integer> path = getPath(start, end, post);
            if (path == null)
                break;
            pathFlow = getMinCap(path, residualCap);
            if (pathFlow == 0)
                break;
            pathFlow = pathFlow > flow ? flow : pathFlow;

            reSetGraph(path, pathFlow, cap, residualCap, residualFee, flowGraph);
            flowSum += pathFlow;

            path.add(pathFlow);
            list.add(path);

//            for(int i : path){
//                System.out.print(i+" ");
//            }
//            System.out.println();
        }
//
//        System.out.println();

        //统计每个被使用的服务器的输出流量
        HashMap<Integer, Integer> serverFlow = new HashMap<>();
        for (List<Integer> l : list) {
            int serverId = l.get(1);
            int pathF = l.get(l.size() - 1);
            Integer sFlow = serverFlow.get(serverId);
            if (sFlow == null)
                serverFlow.put(serverId, pathF);
            else
                serverFlow.put(serverId, serverFlow.get(serverId) + pathF);
        }

//        Logger.getGlobal().info("print server out flow size");
//
//        for (int i : serverFlow.keySet())
//            System.out.println(i + ":" + serverFlow.get(i));
//        System.out.println("----------------------");

        serverFlow.put(cap.length, flowSum);

//        return flowSum;
        return serverFlow;
    }

    /**
     * 得到一条流量流
     * 从流量图中获取最小费用流集（增广流的集合）中的一条流量流
     * 如果起点到终点存在一条流量流则返回比赛要求的流量流格式 [start p1 ... pn end fee]
     * 否则返回Null
     *
     * @param start     起点
     * @param end       终点
     * @param flowGraph 流量图
     * @return 比赛要求的流量流格式 [start p1 ... pn end fee] OR null
     */
    private static String getAFlowPath(int start, int end, FlowGraph flowGraph, int[][] consumer) {

        StringBuffer sb = new StringBuffer(); //保存路径的顶点
        List<FlowGraph.Edge> path = new LinkedList<>(); //保存路径的边
        FlowGraph.Edge first = flowGraph.getANonZeroEdge(start); //获取第一条边

        if (first == null)
            return null;
        path.add(first);

        int p = first.getEnd();
//        sb.append(p + " ");

        FlowGraph.Edge edge;
        int minFlow = first.getFlow();
        while (true) {
            sb.append(p + " ");
            edge = flowGraph.getANonZeroEdge(p);
            if (edge == null) {
                return null;
            }
            path.add(edge);
            if (edge.getFlow() < minFlow) {
                minFlow = edge.getFlow();
            }
            if (edge.getEnd() == end)
                break;
            p = edge.getEnd();
        }

        for (int i = 0; i < consumer.length; i++) {
            if (consumer[i][0] == p) {
                sb.append(i + " ");
                break;
            }
        }

        for (FlowGraph.Edge e : path) {
            e.setFlow(e.getFlow() - minFlow);
        }

        sb.append(minFlow);
        return sb.toString();

    }


    /**
     * 返回start到end的最小费用流量流集中的所有流量流
     *
     * @param start 起点
     * @param end   终点
     * @param flow  流量
     * @param cap   容量图
     * @param fee   费用图
     * @return 流量流列表
     */
    public static List<String> getAllFlowPath(int start, int end, int flow, int[][] cap, int[][] fee, int[][] consumerNode, int serverCost) {
        int timeOut = 85 * 1000;
        long startTime = System.currentTimeMillis();

        int initServerNumRate = 5;
        int vNum = cap.length;
        int flowSum;
        HashSet<Integer> set = new HashSet<>();
        List<int[][]> list = new LinkedList<>();

        List<String> res = null;
        List<String> bestRes = null;

        int[][] residualFee = new int[vNum + 2][vNum + 2]; //残余费用图
        int[][] residualCap = new int[vNum + 2][vNum + 2]; //残余容量图
        int bestCost = Integer.MAX_VALUE;
        HashSet<Integer> bestSet = new HashSet<>();
        HashSet<Integer> badCase = new HashSet<>();
        HashMap<Integer, Integer> map;// = new HashMap<>();

        FlowGraph flowGraph;
        int k = consumerNode.length;


        set.addAll(MatriX.initBothMat(list, set, cap, fee, consumerNode, serverCost, k / initServerNumRate));

        // cap = 扩增为N+2维
        // fee = 扩增为N+2维

        int[][] capacity = list.get(0);
        int[][] f = list.get(1);

        while ((System.currentTimeMillis() - startTime) < timeOut) {

            while(true) {
                flowGraph = new FlowGraph(); //流量图
                ToolBox.copyTwoDArr(capacity, residualCap);
                ToolBox.copyTwoDArr(f, residualFee);

//            Logger.getGlobal().info("use_server:");
//            for (int i : set) {
//                System.out.print(i + " ");
//            }
//            System.out.println();

                map = minFeeFlow(start, end, flow, cap, residualCap, residualFee, flowGraph);


                if (map.size() - 1 > set.size()) {
                    for (int serverId : map.keySet()) {
                        set.add(serverId);
                    }
                    set.addAll(MatriX.updateBothMat(badCase, set, capacity, f, consumerNode, serverCost, 0));      // update with k
                    continue;
                }else{
                    break;
                }
            }

            /*for (int serverId : map.keySet()) {
                set.add(serverId);
            }*/

            flowSum = map.get(cap.length);

            if (flowSum < flow) {
                // 增加一个连接服务器，修改cap与fee
                set.addAll(MatriX.updateBothMat(badCase, set, capacity, f, consumerNode, serverCost, 1));      // update with k
                continue;
            } else {
                res = new LinkedList<>();
                String s;
                while (true) {
                    s = getAFlowPath(start, end, flowGraph, consumerNode);
                    if (s == null)
                        break;
                    res.add(s);
                }
                int cost = ToolBox.countPathListFee(res, f, serverCost);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestRes = res;
                    bestSet.clear();
                    bestSet.addAll(set);
                }
                //退火寻优
                set.addAll(MatriX.updateBothMat(badCase, set, capacity, f, consumerNode, serverCost, 1));      // update with k
                break;

            }

        }
//        System.out.println("fee:" + ToolBox.countPathListFee(res, f, serverCost));
        return bestRes;

    }
}
