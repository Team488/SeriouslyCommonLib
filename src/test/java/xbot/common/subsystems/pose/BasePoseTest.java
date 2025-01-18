package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;

@Ignore
public class BasePoseTest extends BaseCommonLibTest {

    protected MockBasePoseSubsystem pose;
    protected MockTimer mockTimer;

    @Before
    public void setup() {
        mockTimer = (MockTimer)getInjectorComponent().timerImplementation();
        pose = (MockBasePoseSubsystem)getInjectorComponent().poseSubsystem();

        XCANMotorController left = getInjectorComponent().motorControllerFactory().create(new CANMotorControllerInfo("Left", 0), "", "");
        XCANMotorController right = getInjectorComponent().motorControllerFactory().create(new CANMotorControllerInfo("Right", 1), "", "");

        pose.setDriveMotors(left, right);

        mockTimer.advanceTimeInSecondsBy(10);
        pose.periodic();
    }

    protected void verifyRobotHeading(double expectedHeading) {
        assertEquals(expectedHeading, pose.getCurrentHeading().getDegrees(), 0.001);
    }

    protected void verifyRobotOrientedDistance(double expectedDistance) {
        assertEquals(expectedDistance, pose.getRobotOrientedTotalDistanceTraveled(), 0.001);
    }

    protected void verifyAbsoluteDistance(double x, double y) {
        assertEquals(x, pose.getFieldOrientedTotalDistanceTraveled().x, 0.001);
        assertEquals(y, pose.getFieldOrientedTotalDistanceTraveled().y, 0.001);
    }
}
