package jmt.gui.jsimgraph.JGraphMod.arrows;

import jmt.common.functional.*;
import jmt.gui.jsimgraph.controller.Mediator;
import org.jgraph.graph.Edge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

import java.util.*;

/**
 * Created by raffaele on 12/18/16.
 */
public class JmtRoutingNew implements Edge.Routing {

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

    public JmtRoutingNew() {
        bestMatrix();
    }

    static int getFirstHDir() { return firstHDir; }
    static void setFirtHDir(int v) { firstHDir = v; }

    static void incrementCounter() { counter++; }
    public static int getCounter() { return counter; }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    private void bestMatrix() {
        final JmtEdgesMatrix lines = new JmtEdgesMatrix();
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
                accumulator.add(findMatricesOfLine(mediator.getComponentsMatrix(), lines, pos + 1));
                return accumulator;
            }
        }, new ArrayList<List<JmtIncrementalEdgesMatrix>>(), lines.getEdges());

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
        combo.getCombinations(paths, -1, -1);
        System.out.println(combo.getResult());
    }

    private List<JmtIncrementalEdgesMatrix> findMatricesOfLine(JmtComponentsMatrix componentsMatrix, JmtEdgesMatrix lines, int i) {
        JmtMatrixCoordinate startCoord = lines.getStartingPointOfEdge(i - 1);
        JmtComponentsMatrix matrix = componentsMatrix.copy();
        JmtPath paths = new JmtPath(matrix, lines.get(i - 1), i, lines.get(i - 1).hasRightDirection());

        return paths.allPaths(null, startCoord, 0, null);
    }

    @Override
    public List route(EdgeView edgeView) {
        return null;
    }

    @Override
    public int getPreferredLineStyle(EdgeView edgeView) {
        return GraphConstants.STYLE_ORTHOGONAL;
    }
}
