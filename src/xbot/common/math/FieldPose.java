package xbot.common.math;

/**
 * The FieldPose class represents a point on the field as well as a heading.
 * 
 * It also keeps track of some linear equation parameters such as slope and y-intercept.
 * These are used to calculate intersection points between multiple FieldPose instances,
 * which can then be used as part of path-following logic.
 * 
 * @author John
 *
 */
public class FieldPose {

    private double m; // slope
    private double b; // y-intercept
    private ContiguousHeading heading;
    private final XYPair fieldPosition;
    
    public FieldPose(XYPair point, ContiguousHeading heading) {
        m = degreesToSlope(heading.getValue());
        this.fieldPosition = point.clone();
        this.heading = heading.clone();
        
        // if we already have the y-intercept, avoid division by 0.
        if ((point.x == 0) || (Math.abs(m) < 0.01)) {
            b = point.y;
        } else {
            b = point.y - (point.x * m); 
        }   
    }
    
    public FieldPose(XYPair point, double slope, boolean positiveAngle) {
        this.fieldPosition = point;
        m = slope;
        b = point.y - (point.x * m);
        
        // if slope negative, but positive angle, we are in quadrant II
        if (slope < 0 && positiveAngle) {
            heading = new ContiguousHeading(slopeToDegrees(m) + 180);
        }
        // if slope positive, but negative angle, we are in quadrant III
        else if (slope > 0 && !positiveAngle) {
            heading = new ContiguousHeading(slopeToDegrees(m) - 180);
        } else {
            heading = new ContiguousHeading(slopeToDegrees(m));
        }
       
    }
    
    public double getSlope() {
        return m;
    }
    
    public ContiguousHeading getHeading() {
        return heading;
    }
    
    public double getIntercept() {
        return b;
    }
    
    public XYPair getPoint() {
        return fieldPosition;
    }
    
    private double degreesToSlope(double degrees) {
        double deltaY = Math.sin(Math.toRadians(degrees));
        double deltaX = Math.cos(Math.toRadians(degrees));
        
        return deltaY/deltaX;
    }
    
    private double slopeToDegrees(double slope) {
        double x = 1;
        if (Math.abs(this.m) > 90) {
            x = -1;
        }
        
        double rads = Math.atan2(this.m, x);
        
        return Math.toDegrees(rads);
    }
    
    public FieldPose getPerpendicularLineThatIncludesPoint(XYPair point) {
        // for lines of zero slope, this is not great
        
        double perp_m = 0;
        
        if (m == 0) {
            // if we have no slope, instead of a line with infinite slope, we just use a very large slope, 
            // like a million.
            perp_m = 1000000;
        } else {
            perp_m = -1/m; 
        }
        
        return new FieldPose(point, perp_m,  heading.getValue() > 0);
    }
    
    public double getY(double x) {
        return x*m+b;
    }
    
    private XYPair getIntersectionWithLine(FieldPose line) {
        // calculate X point where they meet
        
        // In order to find the intersection point of two lines, you can set their Y values equal
        // to each other to find their common X, and then use that X on either line to find their 
        // common y.
        // y1 = m1x1 + b1, y2 = m2x2 + b1
        // m1x + b1 = m2x + b2
        // m1x - m2x = b2 - b1
        // (m1-m2)x = b2 - b1
        // x = (b2-b1)/(m1-m2)
        
        double x_intersect = (line.b - this.b) / (this.m - line.m);
        double y_intersect = getY(x_intersect);
        
        return new XYPair(x_intersect, y_intersect);
    }
    
    public double getDistanceToLineFromPoint(XYPair currentPoint) {
        // Find the perpendicular line at this point
        FieldPose perpLine = getPerpendicularLineThatIncludesPoint(currentPoint);
        
        // Find where the points meet
        XYPair intersectionPoint = getIntersectionWithLine(perpLine);
        
        // distance formula
        return intersectionPoint.getDistanceToPoint(currentPoint);
    }
    
    public double getPointRelativeYDisplacementFromLine(FieldPose currentLine) {
        // first, subtract the two final points
        XYPair clonedPoint = this.getPoint().clone();
        
        XYPair normalizedPoint = clonedPoint.add(currentLine.getPoint().scale(-1));
        
        // then rotate that point to 90 degrees
        XYPair rotatedPoint = normalizedPoint.rotate(90-currentLine.getHeading().getValue());
        
        // get just the Y aspect
        return rotatedPoint.y;
    }
}
