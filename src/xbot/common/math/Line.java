package xbot.common.math;

public class Line {

    private double m;
    private double b;
    private final XYPair finalPoint;
    
    public Line(XYPair point, ContiguousHeading heading) {
        m = degreesToSlope(heading.getValue());
        this.finalPoint = point;
        
        // if we already have the y-intercept, avoid division by 0.
        if ((point.x == 0) || (m == 0)) {
            b = point.y;
        } else {
            b = point.y / (point.x * m); 
        }   
    }
    
    public Line(XYPair point, double slope) {
        this.finalPoint = point;
        m = slope;
        b = point.y - (point.x * m);
    }
    
    public double getSlope() {
        return slopeToDegrees(getSlope());
    }
    
    public double getSlopeInDegrees() {
        return getSlopeInDegrees()
    }
    
    public double getIntercept() {
        return b;
    }
    
    public XYPair getFinalPoint() {
        return finalPoint;
    }
    
    private double degreesToSlope(double degrees) {
        double deltaY = Math.sin(Math.toRadians(degrees));
        double deltaX = Math.cos(Math.toRadians(degrees));
        
        return deltaY/deltaX;
    }
    
    private double slopeToDegrees(double slope) {
        return Math.toDegrees(Math.atan2(this.m, 1));
    }
    
    private Line getLinePerpendicularToPoint(XYPair point) {
        // for lines of zero slope, this is not great
        
        double perp_m = 0;
        
        if (m == 0) {
            // if we have no slope, instead of a line with infinite slope, we just use a very large slope, 
            // like a million.
            perp_m = 1000000;
        } else {
            perp_m = -1/m; 
        }
        
        return new Line(point, perp_m);
    }
    
    public double getY(double x) {
        return x*m+b;
    }
    
    private XYPair getIntersectionWithLine(Line line) {
        // calculate X point where they meet
        
        double x_intersect = (line.b - this.b) / (this.m - line.m);
        double y_intersect = getY(x_intersect);
        
        return new XYPair(x_intersect, y_intersect);
    }
    
    public double getDistanceToLineFromPoint(XYPair currentPoint) {
        // Find the perpendicular line at this point
        Line perpLine = getLinePerpendicularToPoint(currentPoint);
        
        // Find where the points meet
        XYPair intersectionPoint = getIntersectionWithLine(perpLine);
        
        // distance formula
        return intersectionPoint.getDistanceToPoint(currentPoint);
    }
    
    public double getPointRelativeYDisplacementFromLine(Line currentLine) {
        // first, subtract the two final points
        XYPair normalizedPoint = this.getFinalPoint().add(currentLine.getFinalPoint().scale(-1));
        
        // then rotate that point to 90 degrees
        XYPair rotatedPoint = normalizedPoint.rotate(90-currentLine.getSlopeInDegrees());
        
        // get just the Y aspect
        return rotatedPoint.y;
    }
}
