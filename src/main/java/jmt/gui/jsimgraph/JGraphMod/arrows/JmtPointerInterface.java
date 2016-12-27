package jmt.gui.jsimgraph.JGraphMod.arrows;

import java.util.ArrayList;

/**
 * Created by raffaele on 12/19/16.
 */
public abstract class JmtPointerInterface {
    protected final JmtIncrementalEdgesMatrix edgesMatrix;
    protected final JmtComponentsMatrix componentsMatrix;

    public JmtPointerInterface(JmtComponentsMatrix componentsMatrix,
                               JmtIncrementalEdgesMatrix edgesMatrix) {
        this.edgesMatrix = edgesMatrix;
        this.componentsMatrix = componentsMatrix;
    }

    public JmtPointerInterface(JmtComponentsMatrix componentsMatrix) {
        this.componentsMatrix = componentsMatrix;
        this.edgesMatrix = new JmtIncrementalEdgesMatrix();
    }

    public JmtPointerInterface(JmtPointerInterface other) {
        this.componentsMatrix = other.componentsMatrix;
        this.edgesMatrix = other.edgesMatrix.copy();
    }

    public abstract JmtIncrementalEdgesMatrix getCompleteArray();

    public abstract ArrayList<Integer> getValue(JmtMatrixCoordinate coord);

    public abstract void setValue(JmtMatrixCoordinate coord, int value);

    public abstract boolean isValidPoint(JmtMatrixCoordinate coord);
}
