package xbot.common.math;

public class PlanarEngine {

    double friction = 0.005;
    double power_factor = 0.1;
    double heading_factor = 0.1;
    double rotate_factor = 3;
    int loops;
    
    XYPair robotPosition;
    double velocity;
    ContiguousHeading heading;
    XYPair goalPoint;
    XYPair goalVector;
    
    public PlanarEngine() {
        robotPosition = new XYPair();
        heading = new ContiguousHeading(90);
        
        goalPoint = new XYPair(10, 0);
        goalVector = new XYPair(1, 0);
    }
    
    public XYPair getGoalPoint() {
        return goalPoint;
    }

    public XYPair step(double forwardPower, double rotatePower) {
        double left = MathUtils.constrainDoubleToRobotScale(forwardPower-rotatePower);
        double right = MathUtils.constrainDoubleToRobotScale(forwardPower+rotatePower);
        velocity += ((left+right)/2*power_factor);
        heading.shiftValue(rotatePower*rotate_factor);
        
        // apply friction model to velocity
        if(Math.abs(velocity) > friction) {
            velocity -= friction;
        }
        else {
            velocity = 0;
        }

        // sharp turns also murder velocity
        velocity -= (Math.abs(rotatePower))*velocity*.05;
        
        robotPosition.x += Math.cos(heading.getValue() / 180 * Math.PI)*velocity;
        robotPosition.y += Math.sin(heading.getValue() / 180 * Math.PI)*velocity;
        loops++;
        return robotPosition;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public FieldPose getRobotPose() {
        return new FieldPose(robotPosition, heading);
    }
    
    public int getLoops() {
        return loops;
    }
}