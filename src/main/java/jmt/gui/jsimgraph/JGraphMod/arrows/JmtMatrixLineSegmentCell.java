package jmt.gui.jsimgraph.JGraphMod.arrows;

import java.util.HashSet;

/**
 * Created by raffaele on 11/30/16.
 */
public class JmtMatrixLineSegmentCell extends JmtMatrixCell {

    private HashSet<Integer> lines;

    public JmtMatrixLineSegmentCell(JmtMatrixCoordinate coordinate, int value) {
        super(coordinate, value);
        lines = new HashSet<>();
    }

    public void addLine(int line) {
        lines.add(line);
    }

    public void removeLine(int line) {
        lines.remove(line);
    }

    public boolean constainsLine(int line) {
        return lines.contains(line);
    }

    public HashSet<Integer> getAllLines() {
        return (HashSet<Integer>) lines.clone();
    }
}
