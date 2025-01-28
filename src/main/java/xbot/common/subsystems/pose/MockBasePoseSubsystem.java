package xbot.common.subsystems.pose;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.properties.PropertyFactory;

import static edu.wpi.first.units.Units.Rotations;

@Singleton
public class MockBasePoseSubsystem extends BasePoseSubsystem {

    private XCANMotorController left;
    private XCANMotorController right;

    @Inject
    public MockBasePoseSubsystem(XGyroFactory gyroFactory, PropertyFactory propManager) {
        super(gyroFactory, propManager);
    }

    public void setDriveMotors(XCANMotorController left, XCANMotorController right) {
        this.left = left;
        this.right = right;
    }

    @Override
    protected double getLeftDriveDistance() {
        return ((MockCANMotorController)left).getPosition().magnitude();
    }

    @Override
    protected double getRightDriveDistance() {
        return ((MockCANMotorController)right).getPosition().magnitude();
    }

    public void setDriveEncoderDistances(int left, int right) {
        ((MockCANMotorController)this.left).setPosition(Rotations.of(left));
        ((MockCANMotorController)this.right).setPosition(Rotations.of(right));
        periodic();
    }

    public void forceTotalXandY(double x, double y) {
        totalDistanceX = x;
        totalDistanceY = y;
    }
}
