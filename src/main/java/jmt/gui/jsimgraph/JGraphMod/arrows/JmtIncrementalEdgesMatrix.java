package jmt.gui.jsimgraph.JGraphMod.arrows;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by raffaele on 12/18/16.
 */
public class JmtIncrementalEdgesMatrix {

    Map<JmtMatrixCoordinate, ArrayList<Integer>> edges;

    public JmtIncrementalEdgesMatrix() {
        edges = new HashMap<>();
    }

    public Set<JmtMatrixCoordinate> keySet() {
        return edges.keySet();
    }

    public ArrayList<Integer> get(JmtMatrixCoordinate key) {
        return edges.get(key);
    }

    public ArrayList<Integer> put(JmtMatrixCoordinate key, ArrayList<Integer> value) {
        if (containsKey(key)) {
            get(key).addAll(value);
        } else {
            edges.put(key, value);
        }

        return get(key);
    }

    public boolean containsKey(JmtMatrixCoordinate key) {
        return edges.containsKey(key);
    }

    public JmtIncrementalEdgesMatrix copy() {
        throw new NotImplementedException();
    }

    public int size() {
        return edges.size();
    }
}
