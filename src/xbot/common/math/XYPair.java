package xbot.common.math;

/**
 * Pair of X and Y coordinates. Can be used for points, vectors, or anything
 * else that requires a coordinate pair.
 *
 */
public class XYPair {
    public static final XYPair ZERO = new XYPair(0, 0);

    public double x;
    public double y;

    public XYPair(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public XYPair() {
        this.x = 0;
        this.y = 0;
    }

    public XYPair clone() {
        return new XYPair(x, y);
    }

    public static XYPair fromPolar(double angle, double magnitude) {
        XYPair newValue = new XYPair(magnitude, 0);
        return newValue.rotate(angle);
    }
    
    public static XYPair fromUnitPolar(double angle) {
        XYPair newValue = new XYPair(1, 0);
        return newValue.rotate(angle);
    }

    public XYPair scale(double scalarMagnitude) {
        this.x *= scalarMagnitude;
        this.y *= scalarMagnitude;
        return this;
    }

    public XYPair scale(double xMagnitude, double yMagnitude) {
        this.x *= xMagnitude;
        this.y *= yMagnitude;
        return this;
    }

    /**
     * Rotates the current coordinates by a given angle.
     * 
     * @param angle
     *            The angle (in degrees) to rotate the pair by
     * @return the rotated object
     */
    public XYPair rotate(double angle) {
        double cosA = Math.cos(Math.toRadians(angle));
        double sinA = Math.sin(Math.toRadians(angle));
        double tempX = x * cosA - y * sinA;
        double tempY = x * sinA + y * cosA;

        x = tempX;
        y = tempY;

        return this;
    }

    public double getAngle() {
        return Math.toDegrees(Math.atan2(y, x));
    }

    public double getMagnitude() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public XYPair add(XYPair pair) {
        this.x += pair.x;
        this.y += pair.y;
        return this;
    }

    @Override
    public String toString() {
        return "(X:" + x + ", Y:" + y + ")";
    }
}
