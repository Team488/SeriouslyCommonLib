package xbot.common.subsystems.drive.control_logic;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.math.Circle;
import xbot.common.math.MathUtils;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class CircleFollowingModule {

    final BasePoseSubsystem pose;
    final DoubleProperty spiralFactorProp;
    final DoubleProperty spiralFactorMaxProp;

    @AssistedInject
    public CircleFollowingModule(
        @Assisted("prefix") String prefix,
        BaseDriveSubsystem drive,
        BasePoseSubsystem pose,
        PropertyFactory propertyFactory
    ) {
        this.pose = pose;

        propertyFactory.setPrefix(prefix + "CircleFollowingModule/");
        spiralFactorProp = propertyFactory.createPersistentProperty("SpiralFactor", 1/12);
        spiralFactorMaxProp = propertyFactory.createPersistentProperty("SpiralFactor Max", 30);
    }

    public void setSpiralFactors(double factor, double max) {
        spiralFactorProp.set(factor);
        spiralFactorMaxProp.set(max);
    }

    public double alignToCircle(Circle circle, boolean clockwise) {
        // Get the angle between the center of the robot and the center of the circle.
        // Then, by either adding or subtracting 90 degrees, we will attempt to orbit the center.
        double angleFromCircleCenterToRobot = circle.Center.getAngleToPoint(pose.getCurrentFieldPose().getPoint());
        double extraHeadingToFollowCircle = clockwise ? -90 : 90;

        double orbitCircleAngle = angleFromCircleCenterToRobot + extraHeadingToFollowCircle;

        // Just following that angle would let us orbit, but we want to actually get on the circle proper.
        // For that, we need to look at how far away we are from the center of the circle vs the radius of the circle.
        // If we are outside the circle, we need to spiral inwards.
        // If we are inside the circle, we need to spiral outwards.
        
        // Positive values mean that we are outside the circle, negative values mean that we are inside the circle.
        double distanceToCircleEdge = pose.getCurrentFieldPose().getPoint().getDistanceToPoint(circle.Center) - circle.Radius;

        // So, we need to add some "spiral" heading to our orbit heading, up to some reasonable limit.
        // When we are outside the circle, it will attempt to turn left towards the circle (it assumes counterclockwise behavior)
        double spiralAngleChange = distanceToCircleEdge * spiralFactorProp.get();
        spiralAngleChange = MathUtils.constrainDouble(spiralAngleChange, -spiralFactorMaxProp.get(), spiralFactorMaxProp.get());
        // Flip the direction if we want to operate clockwise
        spiralAngleChange *= clockwise ? -1 : 1;

        double currentAngleGoal  = orbitCircleAngle + spiralAngleChange;
        return currentAngleGoal;
    }
}