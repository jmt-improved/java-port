package jmt.gui.jsimgraph.JGraphMod.arrows;

import jmt.common.functional.DeepClone;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by raffaele on 12/18/16.
 */
public class JmtIncrementalEdgesMatrix {

    Map<JmtMatrixCoordinate, List<Integer>> edges;

    public JmtIncrementalEdgesMatrix() {
        edges = new HashMap<>();
    }

    public Set<JmtMatrixCoordinate> keySet() {
        return edges.keySet();
    }

    public List<Integer> get(JmtMatrixCoordinate key) {
        return DeepClone.deepClone(edges.get(key));
    }

    public void put(JmtMatrixCoordinate key, List<Integer> value) {
        if (containsKey(key)) {
            edges.get(key).addAll(value);
        } else {
            edges.put(key, value);
        }
    }

    public boolean containsKey(JmtMatrixCoordinate key) {
        return edges.containsKey(key);
    }

    public JmtIncrementalEdgesMatrix copy() {
        JmtIncrementalEdgesMatrix newInstance = new JmtIncrementalEdgesMatrix();
        newInstance.edges = DeepClone.deepClone(this.edges);
        return newInstance;
    }

    public int size() {
        return edges.size();
    }
}
