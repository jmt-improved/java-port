package jmt.gui.jsimgraph.JGraphMod.arrows;

import jmt.common.functional.DeepClone;
import jmt.gui.jsimgraph.controller.Mediator;
import org.apache.commons.lang.NotImplementedException;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Created by raffaele on 11/30/16.
 */
public class JmtComponentsMatrix {
    private Mediator mediator;
    private int cellSize;
    private static final JmtMatrixCell EMPTY_CELL = new JmtMatrixCell(new JmtMatrixCoordinate(-1, -1), 0);

    private int maxX = -1;
    private int maxY = -1;

    private Map<JmtMatrixCoordinate, JmtMatrixCell> cells;

    public class NextLineSegment {
        final private JmtMatrixCoordinate.DeltaCoordinate direction;
        final private JmtMatrixLineSegmentCell cell;

        private NextLineSegment(JmtMatrixCoordinate.DeltaCoordinate direction, JmtMatrixLineSegmentCell cell) {
            this.direction = direction;
            this.cell = cell;
        }

        public JmtMatrixCoordinate.DeltaCoordinate getDirection() {
            return direction;
        }

        public JmtMatrixLineSegmentCell getCell() {
            return cell;
        }
    }

    public JmtComponentsMatrix(Mediator mediator) {
        this.mediator = mediator;
        this.cells = new HashMap<>();
        this.cellSize = 20;
    }

    public JmtComponentsMatrix(Mediator mediator, int cellSize) {
        this(mediator);
        this.cellSize = cellSize;
        System.out.println("cellSize=" + cellSize);
    }

    public int getCellSize() {
        return cellSize;
    }

    public void addStationCell(Rectangle2D r) {
        System.out.println(r);
        System.out.println("x min = " + r.getX() / cellSize + ", x max = " + (r.getX() + r.getWidth()) / cellSize);
        System.out.println("y min = " + r.getY() / cellSize + ", y max = " + (r.getY() + r.getHeight()) / cellSize);
        for(int x = (int) r.getX() / cellSize; x < (r.getX() + r.getWidth()) / cellSize; x++) {
            for (int y = (int) Math.floor(r.getY() / cellSize); y < (r.getY() + r.getHeight()) / cellSize; y++) {
                JmtMatrixCell cell = new JmtMatrixStationCell(new JmtMatrixCoordinate(x, y));
                addMatrixCell(cell);
            }
        }
        System.out.println("Components Matrix:\n" + this);
    }

    public JmtMatrixCell getMatrixCell(JmtMatrixCoordinate coord) {
        if (cells.containsKey(coord)) {
            return cells.get(coord);
        }
        return EMPTY_CELL;
    }

    public void removeStationCell(Rectangle2D r) {
        removeMatrixCell(new JmtMatrixCoordinate((int) r.getX() / cellSize, (int) r.getY() / cellSize));
    }

    public void addMatrixCell(JmtMatrixCell cell) {
        cells.put(cell.getCoordinate(), cell);

        if (cell.getCoordinate().getX() > maxX) {
            maxX = cell.getCoordinate().getX();
        }

        if (cell.getCoordinate().getY() > maxY) {
            maxY = cell.getCoordinate().getY();
        }
    }

    public void resetVisit() {
        for(JmtMatrixCell cell: cells.values()) {
            if (cell instanceof JmtMatrixLineSegmentCell) {
                ((JmtMatrixLineSegmentCell) cell).setVisited(false);
            }
        }
    }

    public NextLineSegment getNextLineSegment(JmtMatrixLineSegmentCell cell, Integer value) {

        for (JmtMatrixCoordinate.DeltaCoordinate coord : JmtMatrixCoordinate.DeltaCoordinate.values()) {
            JmtMatrixCell neighborCell = cells.get(cell.getCoordinate().add(coord));
            if (neighborCell != null && neighborCell.getClass() == cell.getClass()) {
                JmtMatrixLineSegmentCell nCell = (JmtMatrixLineSegmentCell) neighborCell;
                if (nCell.isVisited()) {
                    continue;
                }

                Set<Integer> intersection = new HashSet<>(nCell.getAllLines());
                intersection.retainAll(cell.getAllLines());
                //if (intersection.size() > 0) {
                if(nCell.constainsLine(value)) {
                    nCell.setVisited(true);
                    return new NextLineSegment(coord, nCell);
                }
            }
        }

        return null;
    }

    public void removeMatrixCell(JmtMatrixCoordinate coord) {
        cells.remove(coord);
    }

    public void removeMatrixCell(JmtMatrixCell cell) {
        cells.remove(cell.getCoordinate());
    }

    public Map<JmtMatrixCoordinate.DeltaCoordinate, JmtMatrixCell> getNeighborsOfCell(JmtMatrixCell cell) {
        Map<JmtMatrixCoordinate.DeltaCoordinate, JmtMatrixCell> neighborsMap =
                new EnumMap<JmtMatrixCoordinate.DeltaCoordinate, JmtMatrixCell>(JmtMatrixCoordinate.DeltaCoordinate.class);

        for (JmtMatrixCoordinate.DeltaCoordinate coord : JmtMatrixCoordinate.DeltaCoordinate.values()) {
            JmtMatrixCell neighborCell = cells.get(cell.getCoordinate().add(coord));
            if (neighborCell != null) {
                neighborsMap.put(coord, neighborCell);
            }
        }

        return neighborsMap;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        System.out.println("New cellSize=" + cellSize);
    }

    public JmtComponentsMatrix copy() {
        JmtComponentsMatrix m = new JmtComponentsMatrix(this.mediator);
        m.cellSize = this.cellSize;
        m.cells = DeepClone.deepClone(this.cells);
        m.maxX = this.maxX;
        m.maxY = this.maxY;
        return m;
    }

    public boolean containsCell(JmtMatrixCoordinate coord) {
        return cells.containsKey(coord);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[ \n");
        for (int y = 0; y <= maxY; y++) {
            sb.append(" [ ");
            for (int x = 0; x < maxX; x++) {
                sb.append(getMatrixCell(new JmtMatrixCoordinate(x, y)).toString() + ", ");
            }
            sb.append(getMatrixCell(new JmtMatrixCoordinate(maxX, y)).toString());
            sb.append(" ],\n");
        }
        sb.append("]\n");

        return sb.toString();
    }
}
