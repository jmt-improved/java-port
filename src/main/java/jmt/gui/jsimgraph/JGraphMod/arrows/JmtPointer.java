package jmt.gui.jsimgraph.JGraphMod.arrows;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raffaele on 12/19/16.
 */
public abstract class JmtPointer {
    protected final JmtIncrementalEdgesMatrix edgesMatrix;
    protected final JmtComponentsMatrix componentsMatrix;

    public JmtPointer(JmtComponentsMatrix componentsMatrix,
                      JmtIncrementalEdgesMatrix edgesMatrix) {
        this.edgesMatrix = edgesMatrix;
        this.componentsMatrix = componentsMatrix;
    }

    public JmtPointer(JmtComponentsMatrix componentsMatrix) {
        this.componentsMatrix = componentsMatrix;
        this.edgesMatrix = new JmtIncrementalEdgesMatrix();
    }

    public JmtPointer(JmtPointer other) {
        this.componentsMatrix = other.componentsMatrix;
        this.edgesMatrix = other.edgesMatrix.copy();
    }

    public abstract JmtIncrementalEdgesMatrix getCompleteArray();

    public abstract List<Integer> getValue(JmtMatrixCoordinate coord);

    public abstract void setValue(JmtMatrixCoordinate coord, int value);

    public abstract boolean isValidPoint(JmtMatrixCoordinate coord);
}
