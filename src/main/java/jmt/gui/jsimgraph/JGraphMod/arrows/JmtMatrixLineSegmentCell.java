package jmt.gui.jsimgraph.JGraphMod.arrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by raffaele on 11/30/16.
 */
public class JmtMatrixLineSegmentCell extends JmtMatrixCell {

    private Set<Integer> lines;

    public JmtMatrixLineSegmentCell(JmtMatrixCoordinate coordinate, int value) {
        super(coordinate, value);
        lines = new HashSet<>();
        lines.add(value);
    }

    public JmtMatrixLineSegmentCell(JmtMatrixCoordinate coordinate, List<Integer> values) {
        super(coordinate, values.get(0));
        lines = new HashSet<>();
        lines.addAll(values);
    }

    public void addLine(int line) {
        lines.add(line);
    }

    public void addLines(List<Integer> lines) {
        lines.addAll(lines);
    }

    public void removeLine(int line) {
        lines.remove(line);
    }

    public void removeLines(List<Integer> lines) {
        lines.removeAll(lines);
    }

    public boolean constainsLine(int line) {
        return lines.contains(line);
    }

    public Set<Integer> getAllLines() {
        return lines;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Integer line : lines) {
            sb.append(" " + line);
        }
        sb.append(" ]");
        return sb.toString();
    }
}
