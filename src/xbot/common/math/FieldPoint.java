package xbot.common.math;

public class FieldPoint {

    private double m;
    private double b;
    private ContiguousHeading heading;
    private final XYPair fieldPosition;
    
    public FieldPoint(XYPair point, ContiguousHeading heading) {
        m = degreesToSlope(heading.getValue());
        this.fieldPosition = point;
        this.heading = heading;
        
        // if we already have the y-intercept, avoid division by 0.
        if ((point.x == 0) || (m == 0)) {
            b = point.y;
        } else {
            b = point.y / (point.x * m); 
        }   
    }
    
    public FieldPoint(XYPair point, double slope, boolean positiveAngle) {
        this.fieldPosition = point;
        m = slope;
        b = point.y - (point.x * m);
        
        // if slope negative, but positive angle, we are in quadrant II
        if (slope < 0 && positiveAngle) {
            heading = new ContiguousHeading(slopeToDegrees(m) + 180);
        }
        // if slope positive, but negative angle, we are in quadrant III
        if (slope > 0 && !positiveAngle) {
            heading = new ContiguousHeading(slopeToDegrees(m) - 180);
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
    
    public XYPair getFinalPoint() {
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
    
    private FieldPoint getLinePerpendicularToPoint(XYPair point) {
        // for lines of zero slope, this is not great
        
        double perp_m = 0;
        
        if (m == 0) {
            // if we have no slope, instead of a line with infinite slope, we just use a very large slope, 
            // like a million.
            perp_m = 1000000;
        } else {
            perp_m = -1/m; 
        }
        
        return new FieldPoint(point, perp_m,  heading.getValue() > 0);
    }
    
    public double getY(double x) {
        return x*m+b;
    }
    
    private XYPair getIntersectionWithLine(FieldPoint line) {
        // calculate X point where they meet
        
        double x_intersect = (line.b - this.b) / (this.m - line.m);
        double y_intersect = getY(x_intersect);
        
        return new XYPair(x_intersect, y_intersect);
    }
    
    public double getDistanceToLineFromPoint(XYPair currentPoint) {
        // Find the perpendicular line at this point
        FieldPoint perpLine = getLinePerpendicularToPoint(currentPoint);
        
        // Find where the points meet
        XYPair intersectionPoint = getIntersectionWithLine(perpLine);
        
        // distance formula
        return intersectionPoint.getDistanceToPoint(currentPoint);
    }
    
    public double getPointRelativeYDisplacementFromLine(FieldPoint currentLine) {
        // first, subtract the two final points
        XYPair normalizedPoint = this.getFinalPoint().add(currentLine.getFinalPoint().scale(-1));
        
        // then rotate that point to 90 degrees
        XYPair rotatedPoint = normalizedPoint.rotate(90-currentLine.getHeading().getValue());
        
        // get just the Y aspect
        return rotatedPoint.y;
    }
}
