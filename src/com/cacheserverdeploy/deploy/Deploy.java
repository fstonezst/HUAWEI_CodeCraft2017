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

        Queue sNodeCap;

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

        int start = 0, end = 3, flow = 17;
        FlowGraph flowGraph = minFeeFlow(start, end, flow, capacity, fee);
        List<String> list = getAllFlowPath(start, end, flowGraph);
        for (String s : list)
            System.out.println(s);
    }


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
        int qDstSum = 0;
        int[] res = new int[vNum];
        int[] times = new int[vNum];
        boolean[] isIn = new boolean[vNum];
        for (int i = 0; i < vNum; ++i) {
            times[i] = 0;
            isIn[i] = false;
            res[i] = Integer.MAX_VALUE;
            post[i] = -1;
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
                if (f[a][i] == 0 || c[a][i] <= 0)
                    continue;
                int dst = res[a] + f[a][i];
                if (res[i] > dst) {
                    post[i] = a;
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
    private static List<Integer> getPath(int end, int[] post) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        if(post == null)
            return list;
        int p = end;
        list.add(p);
        while (post[p] != -1) {
            list.addFirst(post[p]);
            p = post[p];
        }
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
        int from = it.next();
        while (it.hasNext()) {
            int to = it.next();
            //1.设置正向流量
            //将该路径的容量直接减去流量
            //subject to  flow <= cap[from][to]
            residualCap[from][to] -= flow;

            //设置流量图，如果正向流量大于逆向流量则将正向流量设为正向流量减去逆流
            //否则将逆流减去正流
            int backFlow = flowGraph.getTheEdgeFlow(to, from);
            if (flow > backFlow) {
                flowGraph.setEdgeFlow(from, to, flow - backFlow);
                flowGraph.setEdgeFlow(to, from, 0);
            } else {
                flowGraph.setEdgeFlow(to, from, backFlow - flow);
            }

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
     *
     * @param start 起点
     * @param end   终点
     * @param flow  流量
     * @param cap   容量图
     * @param fee   费用图
     * @return
     */
    private static FlowGraph minFeeFlow(int start, int end, int flow, int[][] cap, int[][] fee) {
        int vNum = cap.length;
        FlowGraph flowGraph = new FlowGraph(); //流图
        int[][] residualFee = new int[vNum][vNum]; //残余费用图
        int[][] residualCap = new int[vNum][vNum]; //残余容量图
        int pathFlow = 0;  //增广路径流量
        ToolBox.copyTwoDArr(fee, residualFee);
        ToolBox.copyTwoDArr(cap, residualCap);

        int flowSum = flow; //已获取的流量和

        while (flowSum > 0) {
            int[] post = SPFA(start, residualFee, residualCap);
            List<Integer> path = getPath(end, post);
            if (path.get(0) != start)
                break;
            pathFlow = getMinCap(path, residualCap);
            if (pathFlow == 0)
                break;
            pathFlow = pathFlow > flowSum ? flowSum : pathFlow;
            reSetGraph(path, pathFlow, cap, residualCap, residualFee, flowGraph);
            flowSum -= pathFlow;
        }
        return flowGraph;
    }


    private static String getAFlowPath(int start, int end, FlowGraph flowGraph) {
        StringBuffer sb = new StringBuffer();
        FlowGraph.Edge first = flowGraph.removeAEdge(start);
        if (first == null)
            return null;
        int flow = first.flow;
        int p = first.end;
        sb.append(start + " " + p + " ");
        while (p != end) {
            FlowGraph.Edge edge = flowGraph.getAUnZeroEdge(p);
            if (edge == null)
                return null;
            p = edge.end;
            sb.append(p + " ");
            edge.flow -= flow;
        }
        sb.append(flow);
        return sb.toString();
    }

    private static List<String> getAllFlowPath(int start, int end, FlowGraph flowGraph) {
        List<String> res = new LinkedList<>();
        String s = getAFlowPath(start, end, flowGraph);
        while (s != null) {
            res.add(s);
            s = getAFlowPath(start, end, flowGraph);
        }
        return res;
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
        }
        return re;
    }

}


