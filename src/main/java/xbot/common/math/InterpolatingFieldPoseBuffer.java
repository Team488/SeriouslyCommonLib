package xbot.common.math;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.controls.sensors.XTimer;

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
        xBuffer.insert(XTimer.getFPGATimestamp(), pose.getPoint().x);
        yBuffer.insert(XTimer.getFPGATimestamp(), pose.getPoint().y);

        double headingDelta = pose.getHeading().minus(Rotation2d.fromDegrees(unwrappedHeading)).getDegrees();
        unwrappedHeading += headingDelta;
        headingBuffer.insert(XTimer.getFPGATimestamp(), unwrappedHeading);
    }

    public FieldPose getPoseAtTime(double timeInSeconds) {
        return new FieldPose(new XYPair(
            xBuffer.getValAtTime(timeInSeconds),
            yBuffer.getValAtTime(timeInSeconds)),
            Rotation2d.fromDegrees(headingBuffer.getValAtTime(timeInSeconds)));
    }

}