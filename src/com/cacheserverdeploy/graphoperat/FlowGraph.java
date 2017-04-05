package com.cacheserverdeploy.graphoperat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by peter on 2017/4/2.
 */
public class FlowGraph {

    private Map<Integer, Edge> vertices = new HashMap<>();

    class Edge {
        int flow;
        int end;
        Edge next;

        public int getFlow() {
            return flow;
        }

        public void setFlow(int flow) {
            this.flow = flow;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public Edge getNext() {
            return next;
        }

        public void setNext(Edge next) {
            this.next = next;
        }

        public Edge(int flow, int end, Edge next) {
            this.end = end;
            this.flow = flow;
            this.next = next;
        }
    }

    public void addEdge(int start, int end, int flow) {

        Edge e = vertices.get(start);
        if (e == null) {
            vertices.put(start, new Edge(flow, end, null));
            return;
        }

        while (e.end != end) {
            if (e.next == null) {
                e.next = new Edge(flow, end, null);
                return;
            } else {
                e = e.next;
            }
        }
        e.flow = flow;
    }

    public void setEdgeFlow(int start, int end, int flow) {
        addEdge(start, end, flow);
    }

    public int getTheEdgeFlow(int start, int end) {
        Edge e = vertices.get(start);
        while (e != null) {
            if (e.end == end)
                return e.flow;
            e = e.next;
        }
        return 0;
    }

    public Edge getTheEdge(int start, int end) {
        Edge e = vertices.get(start);
        while (e != null) {
            if (e.end == end)
                return e;
            e = e.next;
        }
        return null;
    }

    public Edge removeAEdge(int start) {
        Edge p = vertices.get(start);
        if (p == null) {
            return null;
        } else {
            vertices.put(start, p.next);
            return p;
        }
    }

    public Edge getANonZeroEdge(int start) {
        Edge e = vertices.get(start);
        while (e != null && e.flow <= 0) {
            e = e.next;
        }
        return e;
    }

    public void printFG(){
        Logger.getGlobal().info("FlowGraphPrint");
        for(int i: vertices.keySet()) {
            System.out.print(i + ":");
            Edge e = vertices.get(i);
            while(e != null) {
                System.out.print(e.end + "->");
                e = e.next;
            }
            System.out.println();
        }
    }

    public int getSumOutCap(int start){
        int sum = 0;
        Edge e= vertices.get(start);
        while (e!=null){
            sum+=e.flow;
            e=e.next;
        }
        return sum;
    }
}
