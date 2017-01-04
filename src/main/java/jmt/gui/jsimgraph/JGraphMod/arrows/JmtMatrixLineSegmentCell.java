package jmt.gui.jsimgraph.JGraphMod.arrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by raffaele on 11/30/16.
 */
public class JmtMatrixLineSegmentCell extends JmtMatrixCell {

    private Set<Integer> lines;
    private boolean visited;

    public JmtMatrixLineSegmentCell(JmtMatrixCoordinate coordinate, int value) {
        super(coordinate, value);
        lines = new HashSet<>();
        lines.add(value);
        visited = false;
    }

    public JmtMatrixLineSegmentCell(JmtMatrixCoordinate coordinate, List<Integer> values) {
        super(coordinate, values.get(0));
        lines = new HashSet<>();
        lines.addAll(values);
        visited = false;
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

    public void setVisited(boolean v) {
        this.visited = v;
    }

    public boolean isVisited() {
        return this.visited;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JmtMatrixLineSegmentCell that = (JmtMatrixLineSegmentCell) o;

        if (visited != that.visited) return false;
        return lines != null ? lines.equals(that.lines) : that.lines == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (lines != null ? lines.hashCode() : 0);
        result = 31 * result + (visited ? 1 : 0);
        return result;
    }
}
