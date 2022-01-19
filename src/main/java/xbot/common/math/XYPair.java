package xbot.common.math;

import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Pair of X and Y coordinates. Can be used for points, vectors, or anything
 * else that requires a coordinate pair.
 */
public class XYPair {
    public static final XYPair ZERO = new XYPair(0, 0);

    public double x;
    public double y;

    /**
     * Create a coordinate with the specified x and y value.
     * @param x the x value to use
     * @param y the y value to use
     */
    public XYPair(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a coordinate representing the unit vector for a rotation.
     * @param rotation The rotation value.
     */
    public XYPair(Rotation2d rotation) {
        this.x = rotation.getCos();
        this.y = rotation.getSin();
    }

    /**
     * Create a coordinate at the origin.
     */
    public XYPair() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Clone the object
     */
    public XYPair clone() {
        return new XYPair(x, y);
    }

    /**
     * Create a coordinate from an angle and magnitude
     * @param angle angle in degrees
     * @param magnitude magnitude
     * @return coordinate
     */
    public static XYPair fromPolar(double angle, double magnitude) {
        XYPair newValue = new XYPair(magnitude, 0);
        return newValue.rotate(angle);
    }
    
    /**
     * Create a coordinate of magnitude 1 with an angle
     * @param angle angle in degrees
     * @return coordinate
     */
    public static XYPair fromUnitPolar(double angle) {
        XYPair newValue = new XYPair(1, 0);
        return newValue.rotate(angle);
    }

    /**
     * Scales the current coordinate by a magnitude equally in both dimensions.
     * @param scalarMagnitude the magnitude to scale the coordinate by
     * @return the scaled object
     */
    public XYPair scale(double scalarMagnitude) {
        this.x *= scalarMagnitude;
        this.y *= scalarMagnitude;
        return this;
    }

    /**
     * Scales the coordinate by an x- and y- magnitude.
     * @param xMagnitude the magnitude to scale the x-coordinate by
     * @param yMagnitude the magnitude to scale the y-coordinate by
     * @return the scaled object
     */
    public XYPair scale(double xMagnitude, double yMagnitude) {
        this.x *= xMagnitude;
        this.y *= yMagnitude;
        return this;
    }
    
    /**
     * Add the magnitude to the vector represented by the coordinate.
     * @param magnitudeToAdd magnitude to add
     * @return the modified coordinate
     */
    public XYPair addMagnitude(double magnitudeToAdd) {
        double currentMagnitude = this.getMagnitude();
        
        if (currentMagnitude == 0) {
            this.y = magnitudeToAdd;
            return this;
        }
        
        double totalMagnitude = currentMagnitude + magnitudeToAdd;
        return this.scale(totalMagnitude / currentMagnitude);
    }

    /**
     * Rotates the vector by a given angle.
     * @param angle the angle in degrees to rotate the pair by
     * @return the rotated object
     */
    public XYPair rotate(double angle) {
        double cosA = Math.cos(Math.toRadians(angle));
        double sinA = Math.sin(Math.toRadians(angle));
        double tempX = x * cosA - y * sinA;
        double tempY = x * sinA + y * cosA;

        this.x = tempX;
        this.y = tempY;

        return this;
    }

    /**
     * Get the angle of the vector in degrees
     * @return angle in degrees
     */
    public double getAngle() {
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Get the magnitude of the vector (i.e. distance from origin to the coordinate).
     * @return magnitude
     */
    public double getMagnitude() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * Adds the value of a second coordinate to the current coordinate.
     * @param pair coordinate to add
     * @return the combined object
     */
    public XYPair add(XYPair pair) {
        this.x += pair.x;
        this.y += pair.y;
        return this;
    }
    
    /**
     * Get the distance between the current point and a second point.
     * @param otherPoint the point to calculate the distance from
     * @return the distance
     */
    public double getDistanceToPoint(XYPair otherPoint) {
        // Subtract the points from each other
        XYPair normalizedPoint = this.clone().add(otherPoint.clone().scale(-1));
        // Get the magnitude of the resultant vector
        return normalizedPoint.getMagnitude();
    }

    /**
     * Calculate the dot-product between this and another point.
     * @param otherPoint the second point to use for calculating dot product
     * @return the dot product value
     */
    public double dotProduct(XYPair otherPoint) {
        return (this.x * otherPoint.x) + (this.y * otherPoint.y);
    }

    @Override
    public String toString() {
        return "(X:" + x + ", Y:" + y + ")";
    }
}
