package xbot.common.trajectory;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import xbot.common.math.XYPair;

public class Obstacle extends Rectangle2D.Double {

    private static final long serialVersionUID = 1L;

    public XYPair topLeft;
    public XYPair topRight;
    public XYPair bottomLeft;
    public XYPair bottomRight;

    public boolean topLeftAvailable = true;
    public boolean topRightAvailable = true;
    public boolean bottomLeftAvailable = true;
    public boolean bottomRightAvailable = true;

    public boolean defaultTopLeft = true;
    public boolean defaultTopRight = true;
    public boolean defaultBottomLeft = true;
    public boolean defaultBottomRight = true;

    private Line2D.Double topLine;
    private Line2D.Double bottomLine;
    private Line2D.Double leftLine;
    private Line2D.Double rightLine;

    public String name;

    /**
     * Creates an obstacle, represented by an Axis-Aligned Bounding Box.
     *
     * @param x      CenterX of the obstacle
     * @param y      CenterY of the obstacle
     * @param width  width (x) of the obstacle
     * @param height height (y) of the obstacle
     * @param name   name
     */
    public Obstacle(double x, double y, double width, double height, String name) {
        super(x - width / 2, y - height / 2, width, height);
        this.name = name;
        // Generate the corner points of the bounding box.
        // We will put the points sliiiiightly outside the bounding box, so we don't
        // collide with the true corners later.
        topLeft = new XYPair(x - width / 2 * 1.01, y + height / 2 * 1.01);
        topRight = new XYPair(x + width / 2 * 1.01, y + height / 2 * 1.01);
        bottomLeft = new XYPair(x - width / 2 * 1.01, y - height / 2 * 1.01);
        bottomRight = new XYPair(x + width / 2 * 1.01, y - height / 2 * 1.01);

        topLine = new Line2D.Double(topLeft.x, topLeft.y, topRight.x, topRight.y);
        bottomLine = new Line2D.Double(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y);
        leftLine = new Line2D.Double(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);
        rightLine = new Line2D.Double(topRight.x, topRight.y, bottomRight.x, bottomRight.y);
    }

    /**
     * Sets the corners back to default availability.
     */
    public void resetCorners() {
        topLeftAvailable = defaultTopLeft;
        topRightAvailable = defaultTopRight;
        bottomLeftAvailable = defaultBottomLeft;
        bottomRightAvailable = defaultBottomRight;
    }

    /**
     * Gets the distance between the center of this obstacle and a given point.
     *
     * @param other The given point
     * @return distance to that point from the center of the obstacle.
     */
    public double getDistanceToCenter(XYPair other) {
        return new XYPair(this.getCenterX(), this.getCenterY()).getDistanceToPoint(other);
    }

    /**
     * Finds the two intersection points of a line that passes through this
     * obstacle, and averages them together.
     *
     * @param start Line x1,y1
     * @param end   Line x2,y2
     * @return The average intersection point, or null if there is no intersection
     *         point.
     */
    public XYPair getIntersectionAveragePoint(XYPair start, XYPair end) {
        // test each of the four lines to get any intersection points, then average
        // those points together.
        XYPair topLine = getLineIntersectionPoint(start, end, topLeft, topRight);
        XYPair bottomLine = getLineIntersectionPoint(start, end, bottomLeft, bottomRight);
        XYPair leftLine = getLineIntersectionPoint(start, end, topLeft, bottomLeft);
        XYPair rightLine = getLineIntersectionPoint(start, end, topRight, bottomRight);

        // We take advantage of the fact that getLineIntersectionPoint() returns 0,0 for
        // no intersections,
        // and just add all the points together and divide by two. If we still have 0,0,
        // there probably
        // wasn't an intersection at all.
        XYPair combinedPoint = topLine.clone().add(bottomLine).add(leftLine).add(rightLine);
        if (combinedPoint.getMagnitude() < 0.01) {
            // No intersection points.
            return null;
        } else {
            // Average the two points by dividing x and y by 2.
            return combinedPoint.scale(0.5);
        }
    }

    /**
     * Calculates the intersection point of two line segments. Copied from
     * https://stackoverflow.com/questions/40314303/java-find-intersection-of-line-and-rectangle
     *
     * @param lineA1 Line A x1,y1
     * @param lineA2 Line A x2,y2
     * @param lineB1 Line B x1,y1
     * @param lineB2 Line B x2,y2
     * @return 0,0 if the segments do not intersect.
     */
    public XYPair getLineIntersectionPoint(XYPair lineA1, XYPair lineA2, XYPair lineB1, XYPair lineB2) {
        XYPair candidate = new XYPair();

        // quick check to see if the line segments cross
        Line2D.Double l1 = new Line2D.Double(lineA1.x, lineA1.y, lineA2.x, lineA2.y);
        Line2D.Double l2 = new Line2D.Double(lineB1.x, lineB1.y, lineB2.x, lineB2.y);

        if (!l1.intersectsLine(l2)) {
            return candidate;
        }

        double x1 = lineA1.x;
        double y1 = lineA1.y;
        double x2 = lineA2.x;
        double y2 = lineA2.y;

        double x3 = lineB1.x;
        double y3 = lineB1.y;
        double x4 = lineB2.x;
        double y4 = lineB2.y;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d != 0) {
            double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

            candidate = new XYPair(xi, yi);
        }
        return candidate;
    }

    public boolean doesPointLieAlongMidlines(XYPair point) {
        return (Math.abs(this.getCenterX()-point.x) < 1) || (Math.abs(this.getCenterY()-point.y) < 1);
    }

    /**
     * Finds the closest available corner of this obstacle from a given point. Once
     * it returns a point, that point is unavailable until resetCorners() is called.
     *
     * @param other The point to measure from
     * @return The closest corner, or 0,0 if no corner is found.
     * @see competition.subsystems.pose.Obstacle#resetCorners()
     */
    public XYPair getClosestCornerToPoint(XYPair other) {
        XYPair candidate = new XYPair();
        double minimumDistance = 100000;

        if (topLeftAvailable) {
            double distance = topLeft.getDistanceToPoint(other);
            if (distance < minimumDistance) {
                candidate = topLeft;
                minimumDistance = distance;
            }
        }
        if (topRightAvailable) {
            double distance = topRight.getDistanceToPoint(other);
            if (distance < minimumDistance) {
                candidate = topRight;
                minimumDistance = distance;
            }
        }
        if (bottomLeftAvailable) {
            double distance = bottomLeft.getDistanceToPoint(other);
            if (distance < minimumDistance) {
                candidate = bottomLeft;
                minimumDistance = distance;
            }
        }
        if (bottomRightAvailable) {
            double distance = bottomRight.getDistanceToPoint(other);
            if (distance < minimumDistance) {
                candidate = bottomRight;
                minimumDistance = distance;
            }
        }

        if (candidate == topLeft) {
            topLeftAvailable = false;
        }
        if (candidate == topRight) {
            topRightAvailable = false;
        }
        if (candidate == bottomLeft) {
            bottomLeftAvailable = false;
        }
        if (candidate == bottomRight) {
            bottomRightAvailable = false;
        }
        return candidate.clone();
    }

    public XYPair movePointOutsideOfBounds(XYPair point) {
        point = point.clone();
        // Quick check - if the point is already outside of the obstacle,
        // then nothing needs to be done.
        if (!this.contains(point.x, point.y)) {
            return point;
        }

        // Check each line segment, and prepare to shift the point accordingly.
        double xDelta = 0;
        double yDelta = 0;
        double minDistance = 10000;

        double topDistance = topLine.ptLineDist(point.x, point.y);
        if (topDistance < minDistance && (topLeftAvailable || topRightAvailable)) {
            minDistance = topDistance;
            xDelta = 0;
            yDelta = minDistance;
        }
        double bottomDistance = bottomLine.ptLineDist(point.x, point.y);
        if (bottomDistance < minDistance && (bottomLeftAvailable || bottomRightAvailable)) {
            minDistance = bottomDistance;
            xDelta = 0;
            yDelta = -minDistance;
        }
        double leftDistance = leftLine.ptLineDist(point.x, point.y);
        if (leftDistance < minDistance && (topLeftAvailable || bottomLeftAvailable)) {
            minDistance = leftDistance;
            xDelta = -minDistance;
            yDelta = 0;
        }
        double rightDistance = rightLine.ptLineDist(point.x, point.y);
        if (rightDistance < minDistance && (topRightAvailable || bottomRightAvailable)) {
            minDistance = rightDistance;
            xDelta = minDistance;
            yDelta = 0;
        }

        // put the point slightly outside the bounding box
        xDelta *= 1.01;
        yDelta *= 1.01;

        point.add(new XYPair(xDelta, yDelta));
        return point;
    }

}