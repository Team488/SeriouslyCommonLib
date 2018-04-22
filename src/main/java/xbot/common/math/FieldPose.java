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
        

        // Found by taking the derivative of the distance between any point along the
        // projected pose, denoted by a distance P along the line, and the "other" point:
        //     d/dP(D(P)=sqrt((sin(a)*P-y)^2+(cos(a)*P-x)^2))
        // Then finding the zero of the resultant function, denoting the position along
        // the projected line which is closest to the target:
        //     solve (-x cos(a) - y sin(a) + P)/sqrt(-2 P x cos(a) - 2 P y sin(a) + P^2 + x^2 + y^2) for P
        // ...and finally using that distance formula to construct the final point:
        //     (cos(a)*P, sin(a)*P), for P=xcos(a)+ysin(a)
        double headingCosine = getHeadingCosine();
        double headingSine = getHeadingSine();
        double distanceAlongPoseLine = headingCosine * relativeVector.x + headingSine * relativeVector.y;
        
        return new XYPair(
                fieldPosition.x + headingCosine * distanceAlongPoseLine,
                fieldPosition.y + headingSine * distanceAlongPoseLine);
    }
    
    public double getDistanceAlongPoseLine(XYPair other) {
        XYPair relativeVector = new XYPair(other.x - fieldPosition.x, other.y - fieldPosition.y);
        double headingCosine = getHeadingCosine();
        double headingSine = getHeadingSine();
        return headingCosine * relativeVector.x + headingSine * relativeVector.y;
    }
    
    private double getHeadingCosine() {
        return  Math.cos(Math.toRadians(heading.getValue()));
    }
    
    private double getHeadingSine() {
        return Math.sin(Math.toRadians(heading.getValue()));
    }
    
    public FieldPose getRabbitPose(XYPair other, double lookaheadDistance) {
        XYPair closestPoint = getPointAlongPoseClosestToPoint(other);
        XYPair rabbitLocation = closestPoint.add(new XYPair(lookaheadDistance * getHeadingCosine(), lookaheadDistance * getHeadingSine()));
        return new FieldPose(rabbitLocation, getHeading());
    }
    
    public double getDeltaAngleToRabbit(FieldPose other, double lookaheadDistance) {
        return other.getHeading().difference(getVectorToRabbit(other, lookaheadDistance).getAngle());    
    }
    
    public XYPair getVectorToRabbit(FieldPose other, double lookaheadDistance) {
        FieldPose rabbitPose = getRabbitPose(other.getPoint(), lookaheadDistance);
        return rabbitPose.getPoint().add(other.getPoint().clone().scale(-1));
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
    
    @Override
    public String toString() {
        String xyString = fieldPosition == null ? "null, null" : fieldPosition.x + ", " + fieldPosition.y;
        String headingString = heading == null ? "null" : Double.toString(heading.getValue());
        return "FieldPose(" + xyString + ", "+ headingString + ")";
    }
}
