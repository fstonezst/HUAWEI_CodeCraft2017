package com.cacheserverdeploy.deploy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peter on 2017/4/2.
 */
public class FlowGraph {

    private Map<String, Integer> vertices = new HashMap<>();

    public void addEdge(int start, int end, int flow) {
        String pair = start+","+end;
        vertices.put(pair, flow);
    }

    public int getTheEdge(int start, int end) {
        return vertices.get(start+","+end);
    }
}
