package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Translation2d;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Obstacle extends Rectangle2D.Double {

    private static final long serialVersionUID = 1L;

    public Translation2d topLeft;
    public Translation2d topRight;
    public Translation2d bottomLeft;
    public Translation2d bottomRight;

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
        setKeyParameters(x-width/2, x+width/2, y-height/2, y+height/2);
    }

    private void setKeyParameters(double minX, double maxX, double minY, double maxY) {
        double fudgeDistance = 0.0762; // roughly 3 inches
        this.setRect(minX, minY, maxX - minX, maxY - minY);

        this.topLine = new Line2D.Double(minX, maxY, maxX, maxY);
        this.bottomLine = new Line2D.Double(minX, minY, maxX, minY);
        this.leftLine = new Line2D.Double(minX, minY, minX, maxY);
        this.rightLine = new Line2D.Double(maxX, minY, maxX, maxY);

        this.topLeft = new Translation2d(minX-fudgeDistance, maxY+fudgeDistance);
        this.topRight = new Translation2d(maxX+fudgeDistance, maxY+fudgeDistance);
        this.bottomLeft = new Translation2d(minX-fudgeDistance, minY-fudgeDistance);
        this.bottomRight = new Translation2d(maxX+fudgeDistance, minY-fudgeDistance);
    }

    public String getName() {
        return name;
    }

    public Translation2d getCenter() {
        return new Translation2d(this.getCenterX(), this.getCenterY());
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
    public double getDistanceToCenter(Translation2d other) {
        return new Translation2d(this.getCenterX(), this.getCenterY()).getDistance(other);
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
    public Translation2d getIntersectionAveragePoint(Translation2d start, Translation2d end) {
        // test each of the four lines to get any intersection points, then average
        // those points together.
        Translation2d topLine = getLineIntersectionPoint(start, end, topLeft, topRight);
        Translation2d bottomLine = getLineIntersectionPoint(start, end, bottomLeft, bottomRight);
        Translation2d leftLine = getLineIntersectionPoint(start, end, topLeft, bottomLeft);
        Translation2d rightLine = getLineIntersectionPoint(start, end, topRight, bottomRight);

        Translation2d[] translations = {topLine, bottomLine, leftLine, rightLine};
        int count = 0;

        for (Translation2d translation : translations) {
            if (Math.abs(translation.getX()) > 0.001 || Math.abs(translation.getY()) > 0.001) {
                count++;
            }
        }

        System.out.println("Number of Translation2d instances that have values other than 0,0: " + count);

        // We take advantage of the fact that getLineIntersectionPoint() returns 0,0 for
        // no intersections,
        // and just add all the points together and divide by two. If we still have 0,0,
        // there probably
        // wasn't an intersection at all.
        Translation2d combinedPoint = new Translation2d(topLine.getX(), topLine.getY()).plus(bottomLine).plus(leftLine).plus(rightLine);

        if (combinedPoint.getDistance(new Translation2d()) < 0.01) {
            // No intersection points.
            return null;
        } else {
            // Average the two points by dividing x and y by the number of intersections.
            return combinedPoint.times(1.0 / count);
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
    public Translation2d getLineIntersectionPoint(Translation2d lineA1, Translation2d lineA2, Translation2d lineB1, Translation2d lineB2) {
        Translation2d candidate = new Translation2d();

        // quick check to see if the line segments cross
        Line2D.Double l1 = new Line2D.Double(lineA1.getX(), lineA1.getY(), lineA2.getX(), lineA2.getY());
        Line2D.Double l2 = new Line2D.Double(lineB1.getX(), lineB1.getY(), lineB2.getX(), lineB2.getY());

        if (!l1.intersectsLine(l2)) {
            return candidate;
        }

        double x1 = lineA1.getX();
        double y1 = lineA1.getY();
        double x2 = lineA2.getX();
        double y2 = lineA2.getY();

        double x3 = lineB1.getX();
        double y3 = lineB1.getY();
        double x4 = lineB2.getX();
        double y4 = lineB2.getY();

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d != 0) {
            double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

            candidate = new Translation2d(xi, yi);
        }
        return candidate;
    }

    public Translation2d getLineIntersectionPoint(Line2D.Double lineA, Line2D.Double lineB) {
        return getLineIntersectionPoint(
                new Translation2d(lineA.getX1(), lineA.getY1()),
                new Translation2d(lineA.getX2(), lineA.getY2()),
                new Translation2d(lineB.getX1(), lineB.getY1()),
                new Translation2d(lineB.getX2(), lineB.getY2()));
    }

    public boolean doesPointLieAlongMidlines(Translation2d point) {
        return (Math.abs(this.getCenterX()-point.getX()) < 0.025) || (Math.abs(this.getCenterY()-point.getY()) < 0.025);
    }

    public enum ParallelCrossingType {
        TopAndBottom,
        LeftAndRight,
        None
    }

    public ParallelCrossingType getParallelCrossingType(Translation2d point) {
        if (Math.abs(this.getCenterX()-point.getX()) < 0.025) {
            return ParallelCrossingType.LeftAndRight;
        } else if (Math.abs(this.getCenterY()-point.getY()) < 0.025) {
            return ParallelCrossingType.TopAndBottom;
        } else {
            return ParallelCrossingType.None;
        }
    }

    /**
     * Finds the closest available corner of this obstacle from a given point. Once
     * it returns a point, that point is unavailable until resetCorners() is called.
     *
     * @param other The point to measure from
     * @return The closest corner, or 0,0 if no corner is found.
     * @see xbot.common.trajectory.Obstacle#resetCorners()
     */
    public Translation2d getClosestCornerToPoint(Translation2d other) {
        Translation2d candidate = new Translation2d();
        double minimumDistance = 100000;

        if (topLeftAvailable) {
            double distance = topLeft.getDistance(other);
            if (distance < minimumDistance) {
                candidate = topLeft;
                minimumDistance = distance;
            }
        }
        if (topRightAvailable) {
            double distance = topRight.getDistance(other);
            if (distance < minimumDistance) {
                candidate = topRight;
                minimumDistance = distance;
            }
        }
        if (bottomLeftAvailable) {
            double distance = bottomLeft.getDistance(other);
            if (distance < minimumDistance) {
                candidate = bottomLeft;
                minimumDistance = distance;
            }
        }
        if (bottomRightAvailable) {
            double distance = bottomRight.getDistance(other);
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
        return candidate;
    }

    public void restoreCorner(Translation2d corner) {
        if (corner == topLeft) {
            topLeftAvailable = true;
        }
        if (corner == topRight) {
            topRightAvailable = true;
        }
        if (corner == bottomLeft) {
            bottomLeftAvailable = true;
        }
        if (corner == bottomRight) {
            bottomRightAvailable = true;
        }
    }

    private double bonusOffset = 0.25;

    public double getBonusOffset() {
        return bonusOffset;
    }

    public void setBonusOffset(double bonusOffset) {
        // Only positive values make sense.
        this.bonusOffset = Math.abs(bonusOffset);
    }

    public Translation2d movePointOutsideOfBounds(Translation2d point) {
        point = new Translation2d(point.getX(), point.getY());
        // Quick check - if the point is already outside of the obstacle,
        // then nothing needs to be done.
        if (!this.contains(point.getX(), point.getY())) {
            return point;
        }

        // Check each line segment, and prepare to shift the point accordingly.
        double xDelta = 0;
        double yDelta = 0;
        double minDistance = 10000;

        double topDistance = topLine.ptLineDist(point.getX(), point.getY());
        if (topDistance < minDistance && (topLeftAvailable || topRightAvailable)) {
            minDistance = topDistance;
            xDelta = 0;
            yDelta = minDistance + bonusOffset;
        }
        double bottomDistance = bottomLine.ptLineDist(point.getX(), point.getY());
        if (bottomDistance < minDistance && (bottomLeftAvailable || bottomRightAvailable)) {
            minDistance = bottomDistance;
            xDelta = 0;
            yDelta = -minDistance - bonusOffset;
        }
        double leftDistance = leftLine.ptLineDist(point.getX(), point.getY());
        if (leftDistance < minDistance && (topLeftAvailable || bottomLeftAvailable)) {
            minDistance = leftDistance;
            xDelta = -minDistance - bonusOffset;
            yDelta = 0;
        }
        double rightDistance = rightLine.ptLineDist(point.getX(), point.getY());
        if (rightDistance < minDistance && (topRightAvailable || bottomRightAvailable)) {
            minDistance = rightDistance;
            xDelta = minDistance + bonusOffset;
            yDelta = 0;
        }

        // put the point way outside
        xDelta *= 1.01;
        yDelta *= 1.01;

        point = point.plus(new Translation2d(xDelta, yDelta));
        return point;
    }

    public enum PointProjectionCombination {
        BothInside,
        FirstInside,
        SecondInside,
        BothOutside,
        NotRelevant
    }

    public PointProjectionCombination getPointProjectionCombination(Translation2d first, Translation2d second,
                                                                    ParallelCrossingType crossingType) {
        if (crossingType == ParallelCrossingType.TopAndBottom) {
            return checkXCombination(first, second);
        } else if (crossingType == ParallelCrossingType.LeftAndRight) {
            return checkYCombination(first, second);
        }
        return PointProjectionCombination.NotRelevant;
    }

    public PointProjectionCombination checkXCombination(Translation2d first, Translation2d second) {
        boolean firstOutsideX = (first.getX() < this.getMinX() || first.getX() > this.getMaxX());
        boolean secondOutsideX = (second.getX() < this.getMinX() || second.getX() > this.getMaxX());

        if (firstOutsideX && secondOutsideX) {
            return PointProjectionCombination.BothOutside;
        } else if (firstOutsideX) {
            return PointProjectionCombination.SecondInside;
        } else if (secondOutsideX) {
            return PointProjectionCombination.FirstInside;
        } else {
            return PointProjectionCombination.BothInside;
        }
    }

    public PointProjectionCombination checkYCombination(Translation2d first, Translation2d second) {
        boolean firstOutsideY = (first.getY() < this.getMinY() || first.getY() > this.getMaxY());
        boolean secondOutsideY = (second.getY() < this.getMinY() || second.getY() > this.getMaxY());

        if (firstOutsideY && secondOutsideY) {
            return PointProjectionCombination.BothOutside;
        } else if (firstOutsideY) {
            return PointProjectionCombination.SecondInside;
        } else if (secondOutsideY) {
            return PointProjectionCombination.FirstInside;
        } else {
            return PointProjectionCombination.BothInside;
        }
    }

    public void absorbObstacle(Obstacle other) {
        this.add(other);
        setKeyParameters(this.getMinX(), this.getMaxX(), this.getMinY(), this.getMaxY());
        name = name + "And" + other.name;

        defaultBottomRight &= other.defaultBottomRight;
        defaultBottomLeft &= other.defaultBottomLeft;
        defaultTopRight &= other.defaultTopRight;
        defaultTopLeft &= other.defaultTopLeft;
    }

    public double findClosestPointOnPerimeterToPoint(Translation2d point) {
        var center = getCenter();
        Line2D.Double ray = new Line2D.Double(center.getX(), center.getY(), point.getX(), point.getY());
        // check ray collision with each side. If we get non-zero result, just return that.
        var topIntersection = getLineIntersectionPoint(ray, topLine);
        if (topIntersection.getDistance(new Translation2d()) > 0.001) {
            return point.getDistance(topIntersection);
        }
        var bottomIntersection = getLineIntersectionPoint(ray, bottomLine);
        if (bottomIntersection.getDistance(new Translation2d()) > 0.001) {
            return point.getDistance(bottomIntersection);
        }
        var leftIntersection = getLineIntersectionPoint(ray, leftLine);
        if (leftIntersection.getDistance(new Translation2d()) > 0.001) {
            return point.getDistance(leftIntersection);
        }
        var rightIntersection = getLineIntersectionPoint(ray, rightLine);
        if (rightIntersection.getDistance(new Translation2d()) > 0.001) {
            return point.getDistance(rightIntersection);
        }
        return -1;
    }
}