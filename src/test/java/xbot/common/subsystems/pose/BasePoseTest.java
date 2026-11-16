package xbot.common.subsystems.pose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.simulation.SimHooks;

import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class BasePoseTest extends BaseCommonLibTest {

    protected MockBasePoseSubsystem pose;

    @BeforeEach
    public void setup() {
        pose = (MockBasePoseSubsystem) getInjectorComponent().poseSubsystem();

        XCANMotorController left = getInjectorComponent().motorControllerFactory()
                .create(CANMotorControllerInfo.builder("Left").withCanBus(CANPort.CAN_S0, 0).build(), "", "");
        XCANMotorController right = getInjectorComponent().motorControllerFactory()
                .create(CANMotorControllerInfo.builder("Right").withCanBus(CANPort.CAN_S0, 1).build(), "", "");

        pose.setDriveMotors(left, right);

        SimHooks.stepTiming(10);
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
