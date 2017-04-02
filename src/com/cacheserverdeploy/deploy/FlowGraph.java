package com.cacheserverdeploy.deploy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peter on 2017/4/2.
 */
public class FlowGraph {

    private Map<Integer, Edge> vertices = new HashMap<>();

    class Edge {
        int flow;
        int end;
        Edge next;

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

    public Edge getAUnZeroEdge(int start) {
        Edge e = vertices.get(start);
        while (e != null && e.flow <= 0) {
            e = e.next;
        }
        return e;
    }
}
