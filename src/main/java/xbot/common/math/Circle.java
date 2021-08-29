package xbot.common.math;

public class Circle {

    /**
     * X and Y coordinates of the cirlce center, in inches.
     */
    public XYPair Center;

    /**
     * Radius of the circle in inches
     */
    public double Radius;

    public Circle(double x, double y, double radius) {
        Center = new XYPair(x, y);
        Radius = radius;
    }
}