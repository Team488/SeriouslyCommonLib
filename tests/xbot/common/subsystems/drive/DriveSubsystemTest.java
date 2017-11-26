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
        verifyTankDrive(0, 0);
        
        drive.simpleTankDrive(1, 1);
        verifyTankDrive(1, 1);
        
        drive.simpleTankDrive(0, 0);
        verifyTankDrive(0, 0);
        
        drive.simpleTankDrive(2, 2);
        verifyTankDrive(1, 1);
    }
    
    @Test
    public void testNoTalonsAvailable() {
        // The default "tank" drive returns null when asked for holonomic talons.
        // Here, we're just checking the robot does not crash.
        MockNullPlatform n = injector.getInstance(MockNullPlatform.class);
        drive.setDrivePlatform(n);
        
        drive.simpleHolonomicDrive(new XYPair(0, 0), 0);
        drive.simpleTankDrive(1, 1);
    }
    
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
    }
    
    @Test
    public void testScaledHolonomic() {
        MockHolonomicPlatform h = injector.getInstance(MockHolonomicPlatform.class);
        drive.setDrivePlatform(h);
        
        drive.simpleHolonomicDrive(new XYPair(1, 1), 1, true);
        verifyHolonomicDrive(.33333, .33333, -.33333, 1);
    }
    
    protected void verifyTankDrive(double left, double right) {
        drive.getDrivePlatform().getLeftMasterTalons().stream()
        .forEach((t) -> assertEquals(left, getOutputPercent(t), 0.001));
        
        drive.getDrivePlatform().getRightMasterTalons().stream()
        .forEach((t) -> assertEquals(right, getOutputPercent(t), 0.001));
    }
    
    protected void verifyHolonomicDrive(double fl, double fr, double rl, double rr) {
        assertEquals(fl, getOutputPercent(drive.getDrivePlatform().getFrontLeftMasterTalon()), 0.001);
        assertEquals(fr, getOutputPercent(drive.getDrivePlatform().getFrontRightMasterTalon()), 0.001);
        assertEquals(rl, getOutputPercent(drive.getDrivePlatform().getRearLeftMasterTalon()), 0.001);
        assertEquals(rr, getOutputPercent(drive.getDrivePlatform().getRearRightMasterTalon()), 0.001);
    }
    
    protected double getOutputPercent(XCANTalon t) {
        return t.getOutputVoltage() / MockRobotIO.BUS_VOLTAGE;
    }
}
