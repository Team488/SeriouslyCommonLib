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

    public FieldPose() {
        this.fieldPosition = new XYPair();
        this.heading = new ContiguousHeading();
    }
    
    public FieldPose(XYPair point, ContiguousHeading heading) {
        this.fieldPosition = point.clone();
        this.heading = heading.clone();
    }

    public FieldPose(double x, double y, double heading) {
        this.fieldPosition = new XYPair(x, y);
        this.heading = new ContiguousHeading(heading);
    }

    public FieldPose clone() {
        return new FieldPose(fieldPosition.clone(), heading.clone());
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

    /**
     * Gets the direct angle between the FieldPose point and the other point.
     * @param point Other point
     * @return Direct bearing to the other point
     */
    public double getAngleToPoint(XYPair point) {
        return point.clone().add(this.getPoint().clone().scale(-1)).getAngle();
    }

    /**
     * Gets the angle difference between the FieldPose heading and the other point
     * @param point Other point
     * @return How much the heading would have to rotate to point directly at the other point.
     */
    public double getRelativeAngleToPoint(XYPair point) {
        double directAngle = getAngleToPoint(point);
        return heading.difference(directAngle);
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

    /**
     * Projects along the line created by the X,Y pair and the Heading.
     * Positive distances are in front, negative are behind. Keeps the
     * original heading.
     * @param distance Positive distances are in front, negative are behind.
     * @return
     */
    public FieldPose getPointAlongPoseLine(double distance) {
        double simpleHeading = this.heading.getValue();
        double deltaX = Math.cos(simpleHeading / 180 * Math.PI) * distance;
        double deltaY = Math.sin(simpleHeading / 180 * Math.PI) * distance;
        double updatedX = this.getPoint().x + deltaX;
        double updatedY = this.getPoint().y + deltaY;

        return new FieldPose(new XYPair(updatedX, updatedY), new ContiguousHeading(simpleHeading));
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
        ContiguousHeading changedHeading = this.getHeading().clone();

        return new FieldPose(changedPoint, changedHeading);
    }

    //https://stackoverflow.com/questions/4543506/algorithm-for-intersection-of-2-lines

    public XYPair getCenterOfCircleConnectingFieldPoseAndPoint(XYPair point) {
        // General formula:
        // 1) Find the midpoint between the fieldPose and the point
        // 2) Get the general equation of the perpindicular bisector (Form ax + by = c)
        // 3) Get the general equation of a line perpindicular to the fieldPose (Form ax + by = c)
        // 4) Find where these two points connect

        // 1)
        double distanceToPoint = this.fieldPosition.getDistanceToPoint(point);
        double angleToPoint = point.clone().add(this.fieldPosition.clone().scale(-1)).getAngle();
        XYPair midpoint = new XYPair((this.fieldPosition.x + point.x)/2, (this.fieldPosition.y + point.y)/2);
        FieldPose perpindicularBisector = new FieldPose(midpoint, new ContiguousHeading(angleToPoint+90));

        // 2)
        double pb_m = perpindicularBisector.getHeadingSine() / perpindicularBisector.getHeadingCosine();
        double A1 = -pb_m;
        double B1 = 1;
        double C1 = -pb_m*perpindicularBisector.getPoint().x + perpindicularBisector.getPoint().y;

        // 3)
        FieldPose perpindicularPose = this.clone();
        perpindicularPose.heading.shiftValue(90);

        double pp_m = perpindicularPose.getHeadingSine() / perpindicularPose.getHeadingCosine();
        double A2 = -pp_m;
        double B2 = 1;
        double C2 = -pp_m*perpindicularPose.getPoint().x + perpindicularPose.getPoint().y;

        // 4)
        double delta = A1 * B2 - A2 * B1;
        double x = (B2 * C1 - B1 * C2) / delta;
        double y = (A1 * C2 - A2 * C1) / delta;

        return new XYPair(x, y);
    }

    @Override
    public String toString() {
        String xyString = fieldPosition == null ? "null, null" : fieldPosition.x + ", " + fieldPosition.y;
        String headingString = heading == null ? "null" : Double.toString(heading.getValue());
        return "FieldPose(" + xyString + ", "+ headingString + ")";
    }
}
