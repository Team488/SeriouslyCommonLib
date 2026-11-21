package xbot.common.math;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.system.Timer;

public class InterpolatingFieldPoseBuffer {

    private InterpolatingHistoryBuffer xBuffer;
    private InterpolatingHistoryBuffer yBuffer;
    private InterpolatingHistoryBuffer headingBuffer;

    private double unwrappedHeading;

    public InterpolatingFieldPoseBuffer(FieldPose pose) {
        xBuffer = new InterpolatingHistoryBuffer(50, pose.getPoint().x);
        yBuffer = new InterpolatingHistoryBuffer(50, pose.getPoint().y);
        unwrappedHeading = pose.getHeading().getDegrees();
        headingBuffer = new InterpolatingHistoryBuffer(50, unwrappedHeading);
    }

    public void insert(FieldPose pose) {
        xBuffer.insert(Timer.getTimestamp(), pose.getPoint().x);
        yBuffer.insert(Timer.getTimestamp(), pose.getPoint().y);

        double headingDelta = pose.getHeading().minus(Rotation2d.fromDegrees(unwrappedHeading)).getDegrees();
        unwrappedHeading += headingDelta;
        headingBuffer.insert(Timer.getTimestamp(), unwrappedHeading);
    }

    public FieldPose getPoseAtTime(double timeInSeconds) {
        return new FieldPose(new XYPair(xBuffer.getValAtTime(timeInSeconds), yBuffer.getValAtTime(timeInSeconds)),
                Rotation2d.fromDegrees(headingBuffer.getValAtTime(timeInSeconds)));
    }

}
