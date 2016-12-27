package jmt.gui.jsimgraph.JGraphMod.arrows;

import java.util.ArrayList;

/**
 * Created by raffaele on 12/19/16.
 */
public class JmtEfficientPointer extends JmtPointerInterface {

    public JmtEfficientPointer(JmtComponentsMatrix componentsMatrix, JmtIncrementalEdgesMatrix edgesMatrix) {
        super(componentsMatrix, edgesMatrix);
    }

    @Override
    public ArrayList<Integer> getValue(JmtMatrixCoordinate coord) {
        if (edgesMatrix.containsKey(coord)) {
            return edgesMatrix.get(coord);
        } else {
            return new ArrayList<>(componentsMatrix.getMatrixCell(coord).asInteger());
        }
    }

    public JmtEfficientPointer(JmtComponentsMatrix componentsMatrix) {
        super(componentsMatrix);
    }

    public JmtEfficientPointer(JmtPointerInterface other) {
        super(other);
    }

    @Override
    public void setValue(JmtMatrixCoordinate coord, int value) {
        edgesMatrix.put(coord, new ArrayList<Integer>(value));
    }

    @Override
    public JmtIncrementalEdgesMatrix getCompleteArray() {
        return edgesMatrix;
    }

    @Override
    public boolean isValidPoint(JmtMatrixCoordinate coord) {
        return edgesMatrix.containsKey(coord);
    }
}
