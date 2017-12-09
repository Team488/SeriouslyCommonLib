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
        
        drive.drive(new XYPair(0, 1), 0);
        verifyTankDrive(1, 1);
        
        drive.drive(new XYPair(0, 0), 0);
        verifyTankDrive(0, 0);
        
        drive.drive(new XYPair(0, 2), 0);
        verifyTankDrive(1, 1);
        
        drive.drive(new XYPair(0, 0), 1);
        verifyTankDrive(-1, 1);
    }
    
    @Test
    public void testSimpleHolonomicDrive() {
        drive.changeIntoMecanum();
        
        verifyHolonomicDrive(0, 0, 0, 0);
        
        drive.drive(new XYPair(0, 1), 0);
        verifyHolonomicDrive(1, 1, 1, 1);
        
        drive.drive(new XYPair(1, 0), 0);
        verifyHolonomicDrive(1, -1, -1, 1);
        
        drive.drive(new XYPair(0, 0), 1);
        verifyHolonomicDrive(-1, 1, -1, 1);
    }
    
    @Test
    public void testScaledDrive() {
        
        verifyTankDrive(0, 0);
        
        drive.drive(new XYPair(0, 1), 0, true);
        verifyTankDrive(1, 1);
        
        drive.drive(new XYPair(0, 1), 2, true);
        verifyTankDrive(-.3333, 1);
    }
    
    
    @Test
    public void testNoTalonsAvailable() {
        // Here, we're just checking the robot does not crash.
        drive.changeIntoNoDrive();
        
        drive.drive(new XYPair(1, 1), 1);
    }
    
    
    protected void verifyTankDrive(double left, double right) {
        assertEquals(left, getOutputPercent(drive.leftTank), 0.001);
        assertEquals(right, getOutputPercent(drive.rightTank), 0.001);
    }
    
    protected void verifyHolonomicDrive(double fl, double fr, double rl, double rr) {
        assertEquals(fl, getOutputPercent(drive.fl), 0.001);
        assertEquals(fr, getOutputPercent(drive.fr), 0.001);
        assertEquals(rl, getOutputPercent(drive.rl), 0.001);
        assertEquals(rr, getOutputPercent(drive.rr), 0.001);
    }
    
    protected double getOutputPercent(XCANTalon t) {
        return t.getOutputVoltage() / MockRobotIO.BUS_VOLTAGE;
    }
}
