package xbot.common.math;

public class PlanarEngine {

    double friction = 0.005;
    double power_factor = 0.1;
    double rotate_factor = 3;
    int loops;
    
    XYPair robotPosition;
    XYPair velocityVector;
    
    XYPair goalPoint;
    XYPair goalVector;
    
    public PlanarEngine() {
        velocityVector = new XYPair();
        robotPosition = new XYPair();
        
        goalPoint = new XYPair(10, 0);
        goalVector = new XYPair(1, 0);
    }
    
    public XYPair getGoalPoint() {
        return goalPoint;
    }

    public XYPair step(double forwardPower, double rotatePower) {
        // apply motor power
        velocityVector = velocityVector.addMagnitude(forwardPower*power_factor);
        velocityVector.rotate(rotatePower*rotate_factor);
        
        // apply friction model to velocity
        if(Math.abs(velocityVector.getMagnitude()) > friction) {
            velocityVector = velocityVector.addMagnitude(-friction);
        }
        else {
            velocityVector = new XYPair(0, 0);
        }
        
        robotPosition = robotPosition.add(velocityVector);
        loops++;
        return robotPosition;
    }
    
    public XYPair getVelocity() {
        return velocityVector;
    }
    
    public FieldPose getRobotPose() {
        return new FieldPose(robotPosition, new ContiguousHeading(velocityVector.getAngle()));
    }
    
    public int getLoops() {
        return loops;
    }
}