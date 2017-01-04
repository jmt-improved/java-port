package jmt.gui.jsimgraph.JGraphMod.arrows;

import jmt.common.functional.*;
import jmt.gui.jsimgraph.controller.Mediator;
import org.jgraph.graph.Edge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by raffaele on 12/18/16.
 */
public class JmtRoutingNew implements Edge.Routing {

    private JmtComponentsMatrix result;

    static class Configuration {
        static final int LENGTH_SCORE = 1;
        static final int ANGLE_SCORE = 5;
        static final int OVERLAPPING_SCORE = 30;
        static final boolean RIGHT_CONSTRAINT = true; // the arrows cannot come back in the horizontal line (if I start from the right side I can go only to left)
        static final boolean ALLOW_TWO_ANGLES = false; // allow to have two near angles, in the case this bring to go to the original direction
        static final int ANGLE_LIMITS = 3; // limits of the number of angle for each line, we can also use a number of angles greater than 3 since the Loss of performance is low
        static final int NO_PATHS_GREATER_THAN = 2; // the limit is based on the best solution find until that moment
        static final boolean ORDER_LOGIC = true; // this allows to adopt some heuristics to the generation algohorithm
        static final boolean ADVANCED_DEBUG = false; // advanced debug log
        static final int COMBINATION_DIM = 2;
        static final int COMBINATION_MAX_BESTS = 10;
        static final int PATHS_LIMITS = 10;
    }

    private Mediator mediator;

    private static int firstHDir = 0;
    private static int counter = 0;
    Point2D lastTo, lastFrom;
    int c = 0;

    public JmtRoutingNew() {
        lastTo = new Point2D.Double(-1, -1);
        lastFrom = new Point2D.Double(-1, -1);
    }

    static int getFirstHDir() { return firstHDir; }
    static void setFirtHDir(int v) { firstHDir = v; }

    static void incrementCounter() { counter++; }
    public static int getCounter() { return counter; }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public void bestMatrix() {
        /* Così mi ricordo come funge sta roba
        ArrayList<Integer> input = new ArrayList<>();
        input.add(-1);
        input.add(2);
        input.add(-3);
        input.add(4);

        ArrayList<Integer> test = map(new MapCallable<Integer>() {
            @Override
            public Integer callable(Integer value, int pos) {
                return value + 1;
            }
        }, input);

        ArrayList<Integer> test2 = filter(new FilterCallable<Integer>() {
            @Override
            public boolean callable(Integer value, int pos) {
                return value > 0;
            }
        }, input);

        Integer test3 = reduce(new ReduceCallable<Integer, Integer>() {
            @Override
            public Integer callable(Integer value, Integer accumulator, int pos) {
                return accumulator + value;
            }
        }, 0, input);*/
        /*Object a = Functional.reduce(new ReduceCallable<JmtEdgesMatrix.JmtEdge, ArrayList<ArrayList<JmtIncrementalEdgesMatrix>>>() {
            @Override
            public ArrayList<ArrayList<JmtIncrementalEdgesMatrix>> callable(JmtEdgesMatrix.JmtEdge value, ArrayList<ArrayList<JmtIncrementalEdgesMatrix>> accumulator, int pos) {
                accumulator.add(findMatricesOfLine(mediator.getComponentsMatrix(), lines, pos + 1));
                return accumulator;
            }
        }, new ArrayList<ArrayList<JmtIncrementalEdgesMatrix>>(), lines);*/

        // Phase 1
        List<List<JmtIncrementalEdgesMatrix>> paths = Functional.reduce(new ReduceCallable<JmtEdgesMatrix.JmtEdge, List<List<JmtIncrementalEdgesMatrix>>>() {
            @Override
            public List<List<JmtIncrementalEdgesMatrix>> callable(JmtEdgesMatrix.JmtEdge value, List<List<JmtIncrementalEdgesMatrix>> accumulator, int pos) {
                accumulator.add(findMatricesOfLine(mediator.getComponentsMatrix(), mediator.getConnectionsMatrix(), pos + 1));
                return accumulator;
            }
        }, new ArrayList<List<JmtIncrementalEdgesMatrix>>(), mediator.getConnectionsMatrix().getEdges());

        // Phase 2
        /*List<List<JmtIncrementalEdgesMatrix>> matrices = Functional.reduce(new ReduceCallable<DynamicHash, List<List<JmtIncrementalEdgesMatrix>>>() {
            @Override
            public List<List<JmtIncrementalEdgesMatrix>> callable(DynamicHash matrix, List<List<JmtIncrementalEdgesMatrix>> accumulator, int pos) {
                final int bestMatrix = matrix.get("bestPath");
                List<DynamicHash> matrixPaths = matrix.get("paths");
                List<DynamicHash> filtered = Functional.filter(new FilterCallable<DynamicHash>() {
                    @Override
                    public boolean callable(DynamicHash matrixPath, int pos) {
                        int l = matrixPath.get("level");
                        return l < bestMatrix * Configuration.NO_PATHS_GREATER_THAN;
                    }
                }, matrixPaths);

                List<JmtIncrementalEdgesMatrix> ret = Functional.reduce(new ReduceCallable<DynamicHash, List<JmtIncrementalEdgesMatrix>>() {
                    @Override
                    public List<JmtIncrementalEdgesMatrix> callable(DynamicHash path, List<JmtIncrementalEdgesMatrix> accumulator, int pos) {
                        return path.get("path");
                    }
                }, new ArrayList<JmtIncrementalEdgesMatrix>(), filtered);

                accumulator.add(ret);

                return accumulator;
            }
        }, new ArrayList<List<JmtIncrementalEdgesMatrix>>(), paths);*/

        // Phase 3
        JmtEfficientCombination combo = new JmtEfficientCombination(mediator.getComponentsMatrix());
        result = combo.getCombinations(paths, -1, -1).getResult();
        //System.out.println(result);
    }

    private List<JmtIncrementalEdgesMatrix> findMatricesOfLine(JmtComponentsMatrix componentsMatrix, JmtEdgesMatrix lines, int i) {
        JmtMatrixCoordinate startCoord = lines.getStartingPointOfEdge(i - 1);
        JmtComponentsMatrix matrix = componentsMatrix.copy();
        JmtPath paths = new JmtPath(matrix, lines.get(i - 1), i, lines.get(i - 1).hasRightDirection());

        return paths.allPaths(null, startCoord, 0, null);
    }

    @Override
    public List<Point2D> route(EdgeView edgeView) {
        Point2D from = edgeView.getPoint(0);
        Point2D to = edgeView.getPoint(edgeView.getPointCount() - 1);

        if (!from.equals(lastFrom) || !to.equals(lastTo)) {
            bestMatrix();
            lastFrom = from;
            lastTo = to;
            System.out.println(++c);
        }

        JmtMatrixCell startCell = result.getMatrixCell(new JmtMatrixCoordinate((int) from.getX() / result.getCellSize() + 1, (int) from.getY() / result.getCellSize()));
        JmtMatrixCell endCell = result.getMatrixCell(new JmtMatrixCoordinate((int) to.getX() / result.getCellSize() - 1, (int) to.getY() / result.getCellSize()));
        List<Point2D> points = new ArrayList<>();

        assert (startCell instanceof JmtMatrixLineSegmentCell);
        assert (endCell instanceof JmtMatrixLineSegmentCell);

        Set<Integer> intersection = new HashSet<>(((JmtMatrixLineSegmentCell) startCell).getAllLines());
        intersection.retainAll(((JmtMatrixLineSegmentCell) endCell).getAllLines());

        assert(intersection.size() == 1);
        JmtMatrixCoordinate.DeltaCoordinate curDirection = JmtMatrixCoordinate.DeltaCoordinate.LEFT;
        JmtMatrixLineSegmentCell curCell = (JmtMatrixLineSegmentCell) startCell;

        result.resetVisit();

        curCell.setVisited(true);

        points.add(from);

        Point2D curPoint = from;
        while(!curCell.equals(endCell)) {
            JmtComponentsMatrix.NextLineSegment nextLineSegment = result.getNextLineSegment(curCell, intersection.iterator().next());

            switch (curDirection) {
                case LEFT:
                case RIGHT:
                    if (nextLineSegment.getDirection() == JmtMatrixCoordinate.DeltaCoordinate.TOP
                            || nextLineSegment.getDirection() == JmtMatrixCoordinate.DeltaCoordinate.BOTTOM) {
                        curPoint = new Point2D.Double(curCell.getCoordinate().getX() * result.getCellSize() + result.getCellSize() / 2, curPoint.getY());
                    }
                    break;
                case BOTTOM:
                case TOP:
                    if (nextLineSegment.getDirection() == JmtMatrixCoordinate.DeltaCoordinate.LEFT
                            || nextLineSegment.getDirection() == JmtMatrixCoordinate.DeltaCoordinate.RIGHT) {
                        curPoint = new Point2D.Double(curPoint.getX(), curCell.getCoordinate().getY() * result.getCellSize() + result.getCellSize() / 2);
                    }
                    break;

            }
            points.add(curPoint);

            curCell = nextLineSegment.getCell();
            curDirection = nextLineSegment.getDirection();
        }

        points.add(to);
        return points;
    }

    @Override
    public int getPreferredLineStyle(EdgeView edgeView) {
        return GraphConstants.STYLE_ORTHOGONAL;
    }
}
