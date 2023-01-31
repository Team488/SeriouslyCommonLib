package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.MockTimer;
import org.junit.Test;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.math.PIDManager;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

public class HeadingModuleTest extends BaseCommonLibTest {

    HeadingModule headingModule;
    BasePoseSubsystem pose;

    @Override
    public void setUp() {
        super.setUp();
        
        PIDManager pid = pf.create("Testo", 100, 0, 0);
        pid.setErrorThreshold(3);
        pid.setEnableErrorThreshold(true);
        
        headingModule = getInjectorComponent().headingModuleFactory().create(pid);
        pose = getInjectorComponent().poseSubsystem();

        XCANTalon left = getInjectorComponent().canTalonFactory().create(new CANTalonInfo(0));
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        XCANTalon right = getInjectorComponent().canTalonFactory().create(new CANTalonInfo(1));
        right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

        ((MockBasePoseSubsystem)pose).setDriveTalons(left, right);

        ((MockTimer)getInjectorComponent().timerImplementation()).advanceTimeInSecondsBy(10);
        pose.periodic();


    }     
    
    @Test
    public void testTurnLeft() {
        setHeading(0);
        double power = headingModule.calculateHeadingPower(90);
        assertEquals(1, power, 0.001);
        
        setHeading(179);
        power = headingModule.calculateHeadingPower(-179);
        assertEquals(1, power, 0.001);
    }
    
    @Test
    public void testTurnRight() {
        setHeading(0);
        double power = headingModule.calculateHeadingPower(-90);
        assertEquals(-1, power, 0.001);
        
        setHeading(-179);
        power = headingModule.calculateHeadingPower(179);
        assertEquals(-1, power, 0.001);
    }
    
    @Test
    public void onTarget() {
        setHeading(0);
        headingModule.reset();
        assertFalse(headingModule.isOnTarget());
        
        headingModule.calculateHeadingPower(1);
        assertTrue(headingModule.isOnTarget());
    }

    protected void setHeading(double heading)
    {
        ((MockGyro)pose.imu).setYaw(heading);
        pose.refreshDataFrame();
        pose.periodic();
    }
}
