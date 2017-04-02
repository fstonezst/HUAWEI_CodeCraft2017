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

        for (int i = 0; i < vNum; i++)
            for (int j = 0; j < vNum; j++)
                capacity[i][j] = Integer.MAX_VALUE;

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

        int start = 11;
        int end = 13;
//        printG(capacity,fee);

        int[] distance = SPFA(start, fee);
//
        for (int i : distance)
            System.out.print(i + " ");

        System.out.println();
        List<Integer> list = getPath(end,distance);
        for (int i : list)
            System.out.print(i + " ");


//        System.out.println(vNum + " " + eNum + " " + cNum + " " + cost + " ");
//        printG(capacity, fee);
        /**do your work here**/
        return new String[]{"17", "\r\n", "0 8 0 20"};
    }

    /**
     * 按照输入文件的格式在控制台上打印图
     *
     * @param c 容量表
     * @param f 费用表
     */
    private static void printG(int[][] c, int[][] f) {
        for (int i = 0; i < c.length; i++)
            for (int j = 0; j < c[0].length; j++)
                if (c[i][j] < Integer.MAX_VALUE)
                    System.out.println(i + " " + j + " " + c[i][j] + " " + f[i][j]);
    }


    /**
     * SPFA算法实现
     * 在可能存在负边的图中找出start到图中所有点的最短路径(最小费用)
     *
     * @param start 出发点
     * @param f     费用表
     * @return start到各点,
     */
    private static int[] SPFA(int start, int[][] f) {
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
                if (f[a][i] == 0)
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
        int p = end;
        list.add(p);
        while (post[p] != -1) {
            list.addFirst(post[p]);
            p = post[p];
        }
        return list;
//        p = 0;
//        int[] res = new int[list.size()];
//        for(int i : list)
//            res[p++] = i;
//        return res;
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
     * 根据新的增广路径设置回流以及回流的最大容量
     *
     * @param path        路径
     * @param flow        路径流量
     * @param cap         初始网络容量
     * @param residualCap 残余网络容量
     * @param residualFee 残余网络费用
     */
    private static void reSetGraph(List<Integer> path, int flow, int[][] cap, int[][] residualCap, int[][] residualFee ,int[][] flowGraph) {
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
            if (flowGraph[from][to] > flowGraph[to][from]){
                flowGraph[from][to] = flowGraph[from][to] - flowGraph[to][from] ;
                flowGraph[to][from] = 0;
            } else {
                flowGraph[to][from] -= flowGraph[from][to];
            }

            //如果该路径费用为负，则流量经过的路径为回流，
            //如果回流的剩余容量等于0则将该回流去掉，重设为初始路径

            if (residualFee[from][to] < 0 && residualCap[from][to] == 0) {
                residualCap[from][to] = cap[from][to];
                residualFee[from][to] *= -1;
            } else if (residualFee[from][to] > 0){
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

    private static void f1(){

    }
    private static void f2(){
    }
}

