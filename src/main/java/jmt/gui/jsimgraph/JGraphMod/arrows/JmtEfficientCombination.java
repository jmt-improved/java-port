package jmt.gui.jsimgraph.JGraphMod.arrows;

import jmt.common.functional.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raffaele on 12/23/16.
 */
public class JmtEfficientCombination {
    private JmtComponentsMatrix componentsMatrix;
    private JmtIncrementalEdgesMatrix bestCombinationMatrix;
    private int score;
    private int scoreTime;
    private int mergeTime;

    class Bests {
        List<DynamicHash> data;
        int limit;

        Bests(int limit) {
            this.limit = limit;
            data = new ArrayList<>();
        }

        DynamicHash getLastData() {
            if (data.size() == 0) {
                DynamicHash h = new DynamicHash();
                h.put("data", null);
                h.put("value", 0);
                return h;
            }
            return data.get(data.size() - 1);
        }

        void add(JmtIncrementalEdgesMatrix matrix, int value) {
            int lastValue = getLastData().get("value");
            if (value > lastValue) {
                if (data.size() >= limit) {
                    return;
                }
                DynamicHash h = new DynamicHash();
                h.put("data", matrix);
                h.put("value", value);
                data.add(h);
                return;
            }

            for (int i = 0; i < data.size(); i++) {
                int iValue = data.get(i).get("value");
                if (value < iValue) {
                    int newLimit = (int) Math.min(data.size(), limit - 1);
                    List<DynamicHash> newData = data.subList(0, i);
                    DynamicHash h = new DynamicHash();
                    h.put("data", matrix);
                    h.put("value", value);
                    newData.add(h);
                    newData.addAll(data.subList(i, newLimit));
                    data = newData;
                    return;
                }
            }
        }

        List<JmtIncrementalEdgesMatrix> getData() {
            return Functional.reduce(new ReduceCallable<DynamicHash, List<JmtIncrementalEdgesMatrix>>() {
                @Override
                public List<JmtIncrementalEdgesMatrix> callable(DynamicHash value, List<JmtIncrementalEdgesMatrix> accumulator, int pos) {
                    JmtIncrementalEdgesMatrix d = value.get("data");
                    accumulator.add(d);
                    return accumulator;
                }
            }, new ArrayList<JmtIncrementalEdgesMatrix>(), data);
        }
    }

    public JmtEfficientCombination(JmtComponentsMatrix componentsMatrix) {
        this.componentsMatrix = componentsMatrix;
        this.score = 1000000;
        this.scoreTime = 0;
        this.mergeTime = 0;
    }

    public JmtEfficientCombination getCombinations(List<List<JmtIncrementalEdgesMatrix>> matrices,
                                                   int dim, int maxBests) {
        if (dim <= 0) {
            dim = JmtRoutingNew.Configuration.COMBINATION_DIM;
        }

        if (maxBests <= 0) {
            maxBests = JmtRoutingNew.Configuration.COMBINATION_MAX_BESTS;
        }

        bestCombinationMatrix = recursiveBest(matrices, dim, maxBests).get(0);

        return this;
    }

    private List<JmtIncrementalEdgesMatrix> recursiveBest(List<List<JmtIncrementalEdgesMatrix>> matrices,
                                                               int dim, int maxBests) {
        double steps = Math.floor((double) matrices.size() / dim);
        List<List<JmtIncrementalEdgesMatrix>> newArray;
        if (steps > 1) {
            newArray = new ArrayList<>();
            for (int i = 0; i < matrices.size(); i+= steps) {
                List<List<JmtIncrementalEdgesMatrix>> tmpArray = matrices.subList(i, (int) Math.min(matrices.size(), i + steps));
                List<JmtIncrementalEdgesMatrix> tmp = recursiveBest(tmpArray, dim, maxBests);
                newArray.add(tmp);
            }
        } else {
            newArray = matrices;
        }
        return getBests(newArray, 0, newArray.size(), maxBests);
    }

    private List<JmtIncrementalEdgesMatrix> getBests(List<List<JmtIncrementalEdgesMatrix>> matrices, int offset,
                                                     final int len, int maxBests) {
        // TODO: we can do something better here
        final List<Integer> lengths = new ArrayList<>(len - offset + 1);
        lengths.add(1);
        for (int i = 1; i <= (len - offset); i++) {
            lengths.add(0);
        }
        int totLength = 1;
        List<List<JmtIncrementalEdgesMatrix>> arrayAllied = new ArrayList<>();

        for (int i = offset; i < len; i++) {
            lengths.set(i - offset + 1, matrices.get(i).size());
            totLength *= matrices.get(i).size();
            arrayAllied.add(matrices.get(i));
        }

        Bests bests = new Bests(maxBests);

        for (int i = 0; i < totLength; i++) {
            int grandPos = i;
            List<JmtIncrementalEdgesMatrix> arrays = new ArrayList<>();
            for (int j = 0; j < arrayAllied.size(); j++) {
                grandPos = (int) Math.floor(grandPos / lengths.get(j));
                int pos = grandPos % lengths.get(j + 1);
                arrays.add(matrices.get(j).get(pos));
            }

            JmtIncrementalEdgesMatrix merged = mergePaths(arrays);
            int score = efficientCalculateScore(merged);

            bests.add(merged, score);
        }
        return bests.getData();
    }

    private JmtIncrementalEdgesMatrix mergePaths(List<JmtIncrementalEdgesMatrix> matrices) {
        JmtIncrementalEdgesMatrix ret = new JmtIncrementalEdgesMatrix();
        for (JmtIncrementalEdgesMatrix matrix: matrices) {
            for (JmtMatrixCoordinate coord: matrix.keySet()) {
                ret.put(coord, matrix.get(coord));
            }
        }
        return ret;
    }

    private int efficientCalculateScore(JmtIncrementalEdgesMatrix matrix) {
        int score = 0;
        for (JmtMatrixCoordinate coord: matrix.keySet()) {
            score += (matrix.get(coord).size() - 1) * JmtRoutingNew.Configuration.OVERLAPPING_SCORE;
            score += matrix.get(coord).size() * JmtRoutingNew.Configuration.LENGTH_SCORE;
            int angles = efficientCalculateAnglesNumber(matrix, coord);
            score += angles * JmtRoutingNew.Configuration.ANGLE_SCORE;
        }
        return score;
    }

    private int efficientCalculateAnglesNumber(final JmtIncrementalEdgesMatrix matrix, final JmtMatrixCoordinate coord) {
        List<Integer> values = matrix.get(coord);
        return Functional.filter(new FilterCallable<Integer>() {
            @Override
            public boolean callable(Integer value, int pos) {
                if ((matrix.containsKey(coord.yIncrement(-1)) && matrix.containsKey(coord.xIncrement(1)))
                    || (matrix.containsKey(coord.yIncrement(-1)) && matrix.containsKey(coord.xIncrement(-1)))
                    || (matrix.containsKey(coord.yIncrement(1)) && matrix.containsKey(coord.xIncrement(1)))
                    || (matrix.containsKey(coord.yIncrement(1)) && matrix.containsKey(coord.xIncrement(-1))))
                    return true;
                return false;
            }
        }, values).size();
    }

    public JmtComponentsMatrix getResult() {
        JmtComponentsMatrix result = componentsMatrix.copy();
        for (JmtMatrixCoordinate coord : bestCombinationMatrix.keySet()) {
            JmtMatrixLineSegmentCell lineSegmentCell = new JmtMatrixLineSegmentCell(coord, bestCombinationMatrix.get(coord));
            result.addMatrixCell(lineSegmentCell);

        }
        return result;
    }
}
