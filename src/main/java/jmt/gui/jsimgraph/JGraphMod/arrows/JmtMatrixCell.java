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

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JmtMatrixCell that = (JmtMatrixCell) o;

        if (value != that.value) return false;
        return coordinate != null ? coordinate.equals(that.coordinate) : that.coordinate == null;
    }

    @Override
    public int hashCode() {
        int result = coordinate != null ? coordinate.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }
}
