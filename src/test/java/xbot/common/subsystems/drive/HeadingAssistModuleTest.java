package xbot.common.subsystems.drive;

import edu.wpi.first.wpilibj.MockTimer;
import org.junit.Test;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.subsystems.drive.control_logic.HeadingAssistModule;
import xbot.common.subsystems.drive.control_logic.HeadingAssistModule.HeadingAssistMode;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

import static edu.wpi.first.units.Units.Degrees;
import static org.junit.Assert.assertEquals;

public class HeadingAssistModuleTest extends BaseCommonLibTest {

    HeadingAssistModule ham;
    BasePoseSubsystem pose;

    @Override
    public void setUp() {
        super.setUp();
        pose = getInjectorComponent().poseSubsystem();

        HeadingModule hold = getInjectorComponent().headingModuleFactory().create(pf.create("Hold", 1000, 0, 0));
        HeadingModule decay = getInjectorComponent().headingModuleFactory().create(pf.create("Decay", 0, 0, 1000));
        ham = getInjectorComponent().headingAssistModuleFactory().create(hold, decay, "Test");
        ham.setMode(HeadingAssistMode.HoldOrientation);

        XCANMotorController left = getInjectorComponent().motorControllerFactory().create(new CANMotorControllerInfo("Left", 0), "", "");
        XCANMotorController right = getInjectorComponent().motorControllerFactory().create(new CANMotorControllerInfo("Right", 1), "", "");

        ((MockBasePoseSubsystem)pose).setDriveMotors(left, right);

        ((MockTimer)getInjectorComponent().timerImplementation()).advanceTimeInSecondsBy(10);
        pose.refreshDataFrame();
        pose.periodic();

    }

    @Test
    public void testNoConfig() {
        ham.setMode(null);
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }
    @Test
    public void testFullStateMachine() {
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();

        // robot is returned to original position
        setHeading(0);
        double power = ham.calculateHeadingPower(0);
        assertEquals(0, power, 0.001);
    }

    @Test
    public void interruptedAfterStep1() {
        step1_humanDrive();

        // system then recovers
        interruptingInput();

        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }

    @Test
    public void interruptedAfterStep2() {
        step1_humanDrive();
        step2_humanStops();

        interruptingInput();

        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }

    @Test
    public void interruptedAfterStep3() {
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();
        interruptingInput();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }

    @Test
    public void interruptedAfterStep4() {
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();

        interruptingInput();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }

    private void interruptingInput() {

        double power = ham.calculateHeadingPower(.6627);
        assertEquals(.6627, power, 0.001);

    }

    private void step1_humanDrive() {

        // human is trying to rotate the robot
        double power = ham.calculateHeadingPower(1);
        assertEquals(1, power, 0.001);

    }

    private void step2_humanStops() {

        // human stops trying to rotate the robot
        double power = ham.calculateHeadingPower(0);
        assertEquals(0, power, 0.001);

    }

    private void step3_timePasses() {

        // time passes, this should "set" the desired angle
        timer.advanceTimeInSecondsBy(1);
        double power = ham.calculateHeadingPower(0);
        assertEquals(0, power, 0.001);
    }

    private void step4_robotRotated() {

        // the robot undergoes some automatic rotation
        setHeading(pose.getCurrentHeading().getDegrees()+90);
        double power = ham.calculateHeadingPower(0);
        assertEquals(-1, power, 0.001);
    }


    @Test
    public void testDecay() {
        ham.setMode(HeadingAssistMode.DecayVelocity);
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();

        // just like the position-based one, this should try to turn right if the robot is suddenly rotated left
        // The heading module needs to run at least once, as initially the "previous error" is empty and so no the PID
        // will have no response.
        double power = ham.calculateHeadingPower(0);
        step4_robotRotated();

        // However, unlike the position-based one, this one will try and turn left if the robot is suddenly rotated right.
        setHeading(pose.getCurrentHeading().getDegrees()-90);
        power = ham.calculateHeadingPower(0);
        assertEquals(1, power, 0.001);
    }

    protected void setHeading(double heading) {
        ((MockGyro)pose.imu).setYaw(Degrees.of(heading));
        pose.refreshDataFrame();
        pose.periodic();
    }
}
