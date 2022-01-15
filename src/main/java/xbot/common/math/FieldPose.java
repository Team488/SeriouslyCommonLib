package xbot.common.math;

import edu.wpi.first.math.geometry.Rotation2d;

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

    private WrappedRotation2d heading;
    private final XYPair fieldPosition;

    public FieldPose() {
        this.fieldPosition = new XYPair();
        this.heading = WrappedRotation2d.fromDegrees(0);
    }
    
    public FieldPose(XYPair point, Rotation2d heading) {
        this.fieldPosition = point.clone();
        this.heading = new WrappedRotation2d(heading.getRadians());
    }

    public FieldPose(double x, double y, double heading) {
        this.fieldPosition = new XYPair(x, y);
        this.heading = WrappedRotation2d.fromDegrees(heading);
    }

    public FieldPose clone() {
        return new FieldPose(fieldPosition.clone(), heading);
    }
    
    public WrappedRotation2d getHeading() {
        return heading;
    }
    
    public XYPair getPoint() {
        return fieldPosition;
    }
    
    public WrappedRotation2d getPerpendicularHeadingTowardsPoint(FieldPose other) {
        boolean direction = getPoseRelativeDisplacement(other).y > 0;
        return WrappedRotation2d.fromRotation2d(heading.rotateBy(Rotation2d.fromDegrees(direction ? -90 : 90)));
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
        return heading.getCos();
    }
    
    private double getHeadingSine() {
        return heading.getSin();
    }
    
    public FieldPose getRabbitPose(XYPair other, double lookaheadDistance) {
        XYPair closestPoint = getPointAlongPoseClosestToPoint(other);
        XYPair rabbitLocation = closestPoint.add(new XYPair(lookaheadDistance * getHeadingCosine(), lookaheadDistance * getHeadingSine()));
        return new FieldPose(rabbitLocation, getHeading());
    }
    
    public double getDeltaAngleToRabbit(FieldPose other, double lookaheadDistance) {
        return WrappedRotation2d.fromDegrees(getVectorToRabbit(other, lookaheadDistance).getAngle()).minus(other.getHeading()).getDegrees();
    }
    
    public XYPair getVectorToRabbit(FieldPose other, double lookaheadDistance) {
        FieldPose rabbitPose = getRabbitPose(other.getPoint(), lookaheadDistance);
        return rabbitPose.getPoint().add(other.getPoint().clone().scale(-1));
    }

    public double getAngleToPoint(XYPair point) {
        return point.clone().add(this.getPoint().clone().scale(-1)).getAngle();
    }
    
    public double getDistanceToLineFromPoint(XYPair currentPoint) {
        return getPointAlongPoseClosestToPoint(currentPoint).getDistanceToPoint(currentPoint);
    }

    public XYPair getPoseRelativeDisplacement(FieldPose other) {
        XYPair clonedPoint = this.getPoint().clone();
        XYPair normalizedPoint = clonedPoint.add(other.getPoint().scale(-1));
        
        // then rotate that point to 90 degrees
        return normalizedPoint.rotate(90 - other.getHeading().getDegrees());
    }

    /**
     * Projects along the line created by the X,Y pair and the Heading.
     * Positive distances are in front, negative are behind. Keeps the
     * original heading.
     * @param distance Positive distances are in front, negative are behind.
     * @return
     */
    public FieldPose getPointAlongPoseLine(double distance) {
        double deltaX = this.heading.getCos() * distance;
        double deltaY = this.heading.getSin() * distance;
        double updatedX = this.getPoint().x + deltaX;
        double updatedY = this.getPoint().y + deltaY;

        return new FieldPose(new XYPair(updatedX, updatedY), new Rotation2d(deltaX, deltaY));
    }

    /**
     * Returns a FieldPose that's "subtracted" by the offset FieldPose. Useful for setting your current position
     * as 0,0 and measuring relative to that as you move around the field.
     * @param offset The offset location
     * @return
     */
    public FieldPose getFieldPoseOffsetBy(FieldPose offset) {
        XYPair changedPoint = this.getPoint().clone().add(offset.getPoint().clone().scale(-1));
        // Currently only handling point offsets, not heading offsets
        WrappedRotation2d changedHeading = this.getHeading();

        return new FieldPose(changedPoint, changedHeading);
    }

    @Override
    public String toString() {
        String xyString = fieldPosition == null ? "null, null" : fieldPosition.x + ", " + fieldPosition.y;
        String headingString = heading == null ? "null" : Double.toString(heading.getDegrees());
        return "FieldPose(" + xyString + ", "+ headingString + ")";
    }
}
