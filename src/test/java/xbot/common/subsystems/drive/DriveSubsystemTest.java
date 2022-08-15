package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.math.XYPair;

public class DriveSubsystemTest extends BaseCommonLibTest {

    MockDriveSubsystem drive;
    
    @Override
    public void setUp() {
        super.setUp();
        
        this.drive = (MockDriveSubsystem)getInjectorComponent().driveSubsystem();
    }
    
    @Test
    public void testComplexTankDrive() {        
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
    public void testFieldOrientedHolonomicDrive() {
        drive.changeIntoMecanum();
        verifyHolonomicDrive(0, 0, 0, 0);
        
        // robot pointed right (0 degrees) but wants to go away from driver station (+Y)
        drive.fieldOrientedDrive(new XYPair(0, 1), 0, 0, false);
        // this should command the robot to strafe left from it's own perspective
        verifyHolonomicDrive(-1, 1, 1, -1);
        
        // robot pointed down and right, commanded to go down and left
        drive.fieldOrientedDrive(new XYPair(-1,-1), 0, -45, false);
        // locally, this should be a strafe right
        verifyHolonomicDrive(1, -1, -1, 1);
        
        // robot pointed down and right, commanded to go up and right
        drive.fieldOrientedDrive(new XYPair(1,1), 0, -45, false);
        // from its local perspective, this is another strafe left.
        verifyHolonomicDrive(-1, 1, 1, -1);
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
    public void testSimpleTankDrive() {        
        drive.drive(0, 0);
        verifyTankDrive(0, 0);
        
        drive.drive(1, 1);
        verifyTankDrive(1, 1);
        
        drive.drive(1, 0);
        verifyTankDrive(1, 0);
        
        drive.drive(-.25, .6668);
        verifyTankDrive(-.25, 0.6668);
    }
    
    
    @Test
    public void testNoTalonsAvailable() {
        // Here, we're just checking the robot does not crash.
        drive.changeIntoNoDrive();
        
        drive.drive(new XYPair(1, 1), 1);
    }

    @Test
    public void testCheesyDriveNoQuickTurn() {
        drive.cheesyDrive(0, 1);
        verifyTankDrive(0, 0);
    }
    
    @Test
    public void testCheesyDriveYesQuickTurn() {
        drive.setQuickTurn(true);
        drive.cheesyDrive(0, 1);
        verifyTankDrive(-1, 1);
    }

    @Test
    public void testCheesyDriveTypicalDrive() {
        drive.cheesyDrive(1, 0);
        verifyTankDrive(1, 1);

        drive.cheesyDrive(0.5, 0);
        verifyTankDrive(0.5, 0.5);

        drive.cheesyDrive(0.5, 0.5);
        verifyTankDrive(0.25, 0.75);
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
        return t.getMotorOutputPercent();
    }
}
