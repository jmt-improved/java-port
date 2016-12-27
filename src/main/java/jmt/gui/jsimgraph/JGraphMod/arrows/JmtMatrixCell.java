package jmt.gui.jsimgraph.JGraphMod.arrows;

/**
 * Created by raffaele on 11/30/16.
 */
public class JmtMatrixCell {
    protected final JmtMatrixCoordinate coordinate;
    protected final int value;

    public JmtMatrixCell(JmtMatrixCoordinate coordinate, int value) {
        this.value = value;
        this.coordinate = coordinate;
    }

    public JmtMatrixCoordinate getCoordinate() {
        return coordinate;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public boolean isStation() {
        return value < 0;
    }

    public boolean isLineSegment() {
        return value > 0;
    }

    public final int asInteger() {
        return value;
    }
}
