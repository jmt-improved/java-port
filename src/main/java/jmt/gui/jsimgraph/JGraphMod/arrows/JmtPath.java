package jmt.gui.jsimgraph.JGraphMod.arrows;

import jmt.common.ConnectionCheck;
import jmt.common.functional.DynamicHash;

import java.util.ArrayList;
import java.util.List;

// TODO: check X, Y coordinates with js code it might be that they need to be swapped here
class JmtPath  {
    private int score;
    private JmtComponentsMatrix componentsMatrix;
    private JmtEdgesMatrix.JmtEdge edge;
    private int value;
    private boolean right;
    private int solutionsFound = 0;

    private class AngleInfo {
        int direction = 0;
        int turned = 0;
        int previousDirection = 0;
        int previousPreviousDirection = 0;
        int turnedCounter = 0;

        public AngleInfo copy() {
            AngleInfo i = new AngleInfo();
            i.direction = this.direction;
            i.turned = this.turned;
            i.previousDirection = this.previousDirection;
            i.previousPreviousDirection = this.previousPreviousDirection;
            i.turnedCounter = turnedCounter;
            return i;
        }
    }

    JmtPath(JmtComponentsMatrix baseMatrix, JmtEdgesMatrix.JmtEdge edge, int value, boolean right) {
        score = 1000000;
        componentsMatrix = baseMatrix;
        this.edge = edge;
        this.value = value;
        this.right = right;
    }

    int getScore() {
        return score;
    }

    List<DynamicHash> allPaths(JmtPointerInterface pointer, JmtMatrixCoordinate coord,
                                    int level, AngleInfo angleInfo) {
        ArrayList<DynamicHash> matrices = new ArrayList<>();

        if (solutionsFound > JmtRoutingNew.Configuration.PATHS_LIMITS) {
            return matrices;
        }

        if (pointer == null) {
            pointer = new JmtEfficientPointer(this.componentsMatrix);
            pointer.setValue(coord, value);
        }

        if (angleInfo == null) {
            angleInfo = new AngleInfo();
        }

        if (coord.equals(edge.getEndingPoint())) {
            if (level < score) {
                this.score = level;
            }

            DynamicHash h = new DynamicHash();
            h.put("level", level);
            h.put("path", pointer.getCompleteArray());
            List<DynamicHash> l = new ArrayList<>();
            l.add(h);

            solutionsFound++;
            return l;
        }

        if (angleInfo.turned == 0)
            angleInfo.turnedCounter++;

        if (angleInfo.turnedCounter > JmtRoutingNew.Configuration.ANGLE_LIMITS)
            return new ArrayList<>();

        //TODO apply analogous idea with score (only length and angles)
        if ((level + edge.getEndingPoint().xDistance(coord) + edge.getEndingPoint().yDistance(coord)) >
                (this.score * JmtRoutingNew.Configuration.NO_PATHS_GREATER_THAN))
            return new ArrayList<>();

        if (angleInfo.turned >= 2 && (!JmtRoutingNew.Configuration.ALLOW_TWO_ANGLES ||
                (angleInfo.direction != angleInfo.previousPreviousDirection &&
                        angleInfo.previousPreviousDirection != 0)))
            return new ArrayList<>();

        //break if right and the limit was passed
        if(JmtRoutingNew.Configuration.RIGHT_CONSTRAINT){
            if(this.right && coord.getX() > edge.getEndingPoint().getX())
                return new ArrayList<>();
            if(!this.right && coord.getX() < edge.getEndingPoint().getX())
                return new ArrayList<>();
        }

        //break if try to go in a direction that I cannot reverse
        if((angleInfo.turnedCounter - 1) >= JmtRoutingNew.Configuration.ANGLE_LIMITS
                && edge.getEndingPoint().xDistance(coord) >1 && edge.getEndingPoint().yDistance(coord) > 1
                && ((coord.getY() < edge.getEndingPoint().getY() && angleInfo.direction == 3)
                || (coord.getY() > edge.getEndingPoint().getY() && angleInfo.direction == 1)
                || (coord.getX() < edge.getEndingPoint().getY() && angleInfo.direction == 4)
                || (coord.getX() > edge.getEndingPoint().getY() && angleInfo.direction == 2)))
            return new ArrayList<>();

        //break if I cannot reach the  target and I have not angles available
        if(angleInfo.turnedCounter >= JmtRoutingNew.Configuration.ANGLE_LIMITS
                && (((angleInfo.direction == 1 || angleInfo.direction == 3) && edge.getEndingPoint().xDistance(coord) > 1) //turn at the end
                || ((angleInfo.direction == 2 || angleInfo.direction == 4) && edge.getEndingPoint().yDistance(coord) > 1)))
            return new ArrayList<>();

        ArrayList<Integer> order = new ArrayList<>();
        order.add(1);
        order.add(2);
        order.add(3);
        order.add(4);

        //TODO check this... do this only at the beginning?
        if(JmtRoutingNew.Configuration.ORDER_LOGIC){
            //TODO improve if equal do the vertical actions
            //width
            if (coord.getX() < edge.getEndingPoint().getX()){
                order.set(0, 2);
                order.set(3, 4);
            } else {
                order.set(0, 4);
                order.set(3, 2);
            }

            //height
            if (coord.getY() <= edge.getEndingPoint().getY()) {
                if(JmtRoutingNew.Configuration.ADVANCED_DEBUG)
                    if(JmtRoutingNew.getFirstHDir() != 1)
                        System.out.println("dir changed to" + 1);
                JmtRoutingNew.setFirtHDir(1);
                order.set(1, 1);
                order.set(2, 3);
            } else {
                if(JmtRoutingNew.Configuration.ADVANCED_DEBUG)
                    if(JmtRoutingNew.getFirstHDir() != 3)
                        System.out.println("dir changed to" + 3);
                JmtRoutingNew.setFirtHDir(3);
                order.set(1, 3);
                order.set(2, 1);
            }
        }
        JmtRoutingNew.incrementCounter();

        for (int i = 0; i <= 3; i++) {
            //no 180 angles
            if(angleInfo.direction == 1 && order.get(i) == 3
                    || angleInfo.direction == 3 && order.get(i) == 1
                    || angleInfo.direction == 4 && order.get(i) == 2
                    || angleInfo.direction == 2 && order.get(i) == 4)
                break;
            matrices.addAll(this.nextStep(pointer, coord, level, angleInfo, order.get(i)));
        }

        return matrices;
    }

    private List<DynamicHash> nextStep(JmtPointerInterface pointer, JmtMatrixCoordinate coord, int level, AngleInfo angleInfo, Integer direction) {
        switch (direction){
            case 1:
                coord = coord.xIncrement(1);
                break;
            case 2:
                coord = coord.yIncrement(1);
                break;
            case 3:
                coord = coord.xIncrement(-1);
                break;
            case 4:
                coord = coord.yIncrement(-1);
                break;
        }

        if(direction == 2 && !(this.right || !JmtRoutingNew.Configuration.RIGHT_CONSTRAINT))
            return new ArrayList<>();

        if(direction == 4 && !(!this.right || !JmtRoutingNew.Configuration.RIGHT_CONSTRAINT))
            return new ArrayList<>();

        if(pointer.isValidPoint(coord) && pointer.getValue(coord).get(0) == 0) {
            JmtEfficientPointer tmpPointer = new JmtEfficientPointer(pointer);
            tmpPointer.setValue(coord, value);
            AngleInfo tmpAngleInfo = angleInfo.copy();
            tmpAngleInfo.previousPreviousDirection = tmpAngleInfo.previousDirection;
            tmpAngleInfo.previousDirection = tmpAngleInfo.direction;
            tmpAngleInfo.direction = direction;
            if (tmpAngleInfo.direction != angleInfo.direction)
                tmpAngleInfo.turned++;
            else
                tmpAngleInfo.turned = 0;
            return this.allPaths(tmpPointer, coord, level + 1, tmpAngleInfo);
        }
        return new ArrayList<>();
    }
}
