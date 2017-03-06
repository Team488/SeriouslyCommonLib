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

    private ContiguousHeading heading;
    private final XYPair fieldPosition;
    
    public FieldPose(XYPair point, ContiguousHeading heading) {
        this.fieldPosition = point.clone();
        this.heading = heading.clone();
    }
    
    public ContiguousHeading getHeading() {
        return heading;
    }
    
    public XYPair getPoint() {
        return fieldPosition;
    }
    
    public XYPair getPointAlongPoseClosestToPoint(XYPair other) {

        XYPair relativeVector = new XYPair(other.x - fieldPosition.x, other.y - fieldPosition.y);
        
        double headingCosine = Math.cos(Math.toRadians(heading.getValue()));
        double headingSine = Math.sin(Math.toRadians(heading.getValue()));
        double distanceAlongPoseLine = headingCosine * relativeVector.x + headingSine * relativeVector.y;
        
        return new XYPair(
                fieldPosition.x + headingCosine * distanceAlongPoseLine,
                fieldPosition.y + headingSine * distanceAlongPoseLine);
    }
    
    public double getDistanceToLineFromPoint(XYPair currentPoint) {
        return getPointAlongPoseClosestToPoint(currentPoint).getDistanceToPoint(currentPoint);
    }
}
