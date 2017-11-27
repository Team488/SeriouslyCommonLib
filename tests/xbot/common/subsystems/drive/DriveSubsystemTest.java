package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.BaseWPITest;
import xbot.common.math.XYPair;
import xbot.common.subsystems.BaseDriveSubsystem;

public class DriveSubsystemTest extends BaseWPITest {

    MockDriveSubsystem drive;
    
    @Override
    public void setUp() {
        super.setUp();
        
        this.drive = (MockDriveSubsystem)injector.getInstance(BaseDriveSubsystem.class);
    }
    
    @Test
    public void testSimpleTankDrive() {
        MockTankPlatform t = injector.getInstance(MockTankPlatform.class);
        drive.setDrivePlatform(t);
        
        verifyTankDrive(t, 0, 0);
        
        drive.drive(new XYPair(0, 1), 0);
        verifyTankDrive(t, 1, 1);
        
        drive.drive(new XYPair(0, 0), 0);
        verifyTankDrive(t, 0, 0);
        
        drive.drive(new XYPair(0, 2), 0);
        verifyTankDrive(t, 1, 1);
        
        drive.drive(new XYPair(0, 0), 1);
        verifyTankDrive(t, -1, 1);
    }
    /*
    @Test
    public void testNoTalonsAvailable() {
        // The default "tank" drive returns null when asked for holonomic talons.
        // Here, we're just checking the robot does not crash.
        MockNullPlatform n = injector.getInstance(MockNullPlatform.class);
        drive.setDrivePlatform(n);
        
        drive.simpleHolonomicDrive(new XYPair(0, 0), 0);
        drive.simpleTankDrive(1, 1);
    }*/
    /*
    @Test
    public void testHolonomic() {
        MockHolonomicPlatform h = injector.getInstance(MockHolonomicPlatform.class);
        drive.setDrivePlatform(h);
        
        drive.simpleHolonomicDrive(new XYPair(0, 1), 0);
        verifyHolonomicDrive(1, 1, 1, 1);
        
        drive.simpleHolonomicDrive(new XYPair(0, -1), 0);
        verifyHolonomicDrive(-1, -1, -1, -1);
        
        drive.simpleHolonomicDrive(new XYPair(1, 0), 0);
        verifyHolonomicDrive(1, -1, -1, 1);
        
        drive.simpleHolonomicDrive(new XYPair(-1, 0), 0);
        verifyHolonomicDrive(-1, 1, 1, -1);
        
        drive.simpleHolonomicDrive(new XYPair(0, 0), 1);
        verifyHolonomicDrive(-1, 1, -1, 1);
        
        drive.simpleHolonomicDrive(new XYPair(0, 0), -1);
        verifyHolonomicDrive(1, -1, 1, -1);
        
        
        drive.simpleHolonomicDrive(new XYPair(1, 1), 0);
        verifyHolonomicDrive(1, 0, 0, 1);
        
        drive.simpleHolonomicDrive(new XYPair(1, 1), 1);
        verifyHolonomicDrive(1, 1, -1, 1);
    }*/
    /*
    @Test
    public void testScaledHolonomic() {
        MockHolonomicPlatform h = injector.getInstance(MockHolonomicPlatform.class);
        drive.setDrivePlatform(h);
        
        drive.simpleHolonomicDrive(new XYPair(1, 1), 1, true);
        verifyHolonomicDrive(.33333, .33333, -.33333, 1);
    }*/
    
    protected void verifyTankDrive(MockTankPlatform t, double left, double right) {
        assertEquals(left, getOutputPercent(t.leftMaster), 0.001);
        assertEquals(right, getOutputPercent(t.rightMaster), 0.001);
        
    }
    
    protected double getOutputPercent(XCANTalon t) {
        return t.getOutputVoltage() / MockRobotIO.BUS_VOLTAGE;
    }
}
