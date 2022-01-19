package xbot.common.math;

import edu.wpi.first.math.geometry.Rotation2d;

public class PlanarEngine {

    double friction = 0.005;
    double power_factor = 0.1;
    double heading_factor = 0.1;
    double rotate_factor = 3;
    int loops;
    
    XYPair robotPosition;
    double velocity;
    Rotation2d heading;
    XYPair goalPoint;
    XYPair goalVector;
    
    public PlanarEngine() {
        robotPosition = new XYPair();
        heading = Rotation2d.fromDegrees(90);
        
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
        heading = heading.plus(Rotation2d.fromDegrees(rotatePower*rotate_factor));
        
        // apply friction model to velocity
        velocity *= 0.9;

        // sharp turns also murder velocity
        velocity -= (Math.abs(rotatePower))*velocity*.05;
        
        robotPosition.x += heading.getCos()*velocity;
        robotPosition.y += heading.getSin()*velocity;
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