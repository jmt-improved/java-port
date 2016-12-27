package jmt.gui.jsimgraph.JGraphMod.arrows;

import jmt.common.functional.DeepClone;
import jmt.gui.jsimgraph.controller.Mediator;
import org.apache.commons.lang.NotImplementedException;

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by raffaele on 11/30/16.
 */
public class JmtComponentsMatrix {
    private Mediator mediator;
    private int cellSize;
    private static final JmtMatrixCell EMPTY_CELL = new JmtMatrixCell(new JmtMatrixCoordinate(-1, -1), 0);

    private Map<JmtMatrixCoordinate, JmtMatrixCell> cells;

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

    public void addStationCell(Rectangle2D r) {
        JmtMatrixCell cell = new JmtMatrixStationCell(new JmtMatrixCoordinate((int) r.getX() / cellSize,
                (int) r.getY() / cellSize));
        addMatrixCell(cell);
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
    }

    public void removeMatrixCell(JmtMatrixCoordinate coord) { cells.remove(coord); }

    public void removeMatrixCell(JmtMatrixCell cell) {
        cells.remove(cell.getCoordinate());
    }

    public Map<JmtMatrixCoordinate.DeltaCoordinate, JmtMatrixCell> getNeighborsOfCell(JmtMatrixCell cell) {
        Map<JmtMatrixCoordinate.DeltaCoordinate, JmtMatrixCell> neighborsMap =
                new EnumMap<JmtMatrixCoordinate.DeltaCoordinate, JmtMatrixCell>(JmtMatrixCoordinate.DeltaCoordinate.class);

        for (JmtMatrixCoordinate.DeltaCoordinate coord: JmtMatrixCoordinate.DeltaCoordinate.values()) {
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
        return m;
    }
}
