package jmt.gui.jsimgraph.JGraphMod.arrows;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raffaele on 12/19/16.
 */
public class JmtEfficientPointer extends JmtPointer {

    public JmtEfficientPointer(JmtComponentsMatrix componentsMatrix, JmtIncrementalEdgesMatrix edgesMatrix) {
        super(componentsMatrix, edgesMatrix);
    }

    @Override
    public List<Integer> getValue(JmtMatrixCoordinate coord) {
        if (edgesMatrix.containsKey(coord)) {
            return edgesMatrix.get(coord);
        } else {
            List<Integer> results = new ArrayList<>();
            results.add(componentsMatrix.getMatrixCell(coord).asInteger());
            return results;
        }
    }

    public JmtEfficientPointer(JmtComponentsMatrix componentsMatrix) {
        super(componentsMatrix);
    }

    public JmtEfficientPointer(JmtPointer other) {
        super(other);
    }

    @Override
    public void setValue(JmtMatrixCoordinate coord, int value) {
        List<Integer> l = new ArrayList<>();
        l.add(value);
        edgesMatrix.put(coord, l);
    }

    @Override
    public JmtIncrementalEdgesMatrix getCompleteArray() {
        return edgesMatrix;
    }

    @Override
    public boolean isValidPoint(JmtMatrixCoordinate coord) {
        return coord.getX() >= 0 && coord.getY() >= 0;
    }
}
