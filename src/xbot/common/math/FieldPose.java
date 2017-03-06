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
    
    public ContiguousHeading getPerpendicularHeadingTowardsPoint(FieldPose other) {
        boolean direction = getPoseRelativeDisplacement(other).y > 0;
        return heading.clone().shiftValue(direction ? -90 : 90);
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

    public XYPair getPoseRelativeDisplacement(FieldPose other) {
        XYPair clonedPoint = this.getPoint().clone();
        XYPair normalizedPoint = clonedPoint.add(other.getPoint().scale(-1));
        
        // then rotate that point to 90 degrees
        return normalizedPoint.rotate(90 - other.getHeading().getValue());
    }
}
