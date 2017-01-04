package jmt.gui.jsimgraph.JGraphMod.arrows;

/**
 * Created by raffaele on 11/30/16.
 */
public class JmtMatrixCoordinate {
    private final int x;
    private final int y;

    public enum DeltaCoordinate {

        /**
         * The bottom adjacent coordinate.
         */
        BOTTOM(0, 1),
        /**
         * The top adjacent coordinate.
         */
        TOP(0, -1),
        /**
         * The left adjacent coordinate.
         */
        LEFT(-1, 0),
        /**
         * The right adjacent coordinate.
         */
        RIGHT(1, 0);

        /**
         * The delta X coordinate.
         */
        private final int dX;

        /**
         * The delta Y coordinate.
         */
        private final int dY;

        private DeltaCoordinate(int dX, int dY) {
            this.dX = dX;
            this.dY = dY;
        }

        public int getDX() {
            return dX;
        }

        public int getDY() {
            return dY;
        }
    }

    public JmtMatrixCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x coordinate.
     *
     * @return the x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JmtMatrixCoordinate that = (JmtMatrixCoordinate) o;

        if (x != that.x) return false;
        return y == that.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 10000000 * result + y;
        return result;
    }

    /**
     * Adds the delta coordinate to the coordinate, constructs a new coordinate
     * with the row and the column as the result of the operation.
     *
     * @param other
     *            the delta coordinate being added
     * @return the new coordinate.
     */
    public JmtMatrixCoordinate add(DeltaCoordinate other) {
        return new JmtMatrixCoordinate(this.x + other.getDX(), this.y + other.getDY());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%d, %d", x, y);
    }

    public int xDistance(JmtMatrixCoordinate other) {
        return Math.abs(other.x - this.x);
    }

    public int yDistance(JmtMatrixCoordinate other) {
        return Math.abs(other.y - this.y);
    }

    public JmtMatrixCoordinate xIncrement(int inc) {
        return new JmtMatrixCoordinate(this.x + inc, this.y);
    }

    public JmtMatrixCoordinate yIncrement(int inc) {
        return new JmtMatrixCoordinate(this.x, this.y + inc);
    }

}
