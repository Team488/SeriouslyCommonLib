package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.TestPoseSubsystem;

public class PurePursuitCommandTest extends BaseWPITest {
    
    PurePursuitCommand command;
    MockDriveSubsystem drive;
    TestPoseSubsystem pose;
    
    @Override
    public void setUp() {
        super.setUp();
        command = injector.getInstance(PurePursuitCommand.class);
        this.drive = (MockDriveSubsystem)injector.getInstance(BaseDriveSubsystem.class);
        this.pose = (TestPoseSubsystem)injector.getInstance(BasePoseSubsystem.class);
        
        pose.setDriveTalons(drive.leftTank, drive.rightTank);
        
        timer.advanceTimeInSecondsBy(10);
        pose.updatePeriodicData();
        
        drive.getPositionalPid().setMaxOutput(1);
        drive.getPositionalPid().setMinOutput(-1);
    }
    
    @Test
    public void simpleTest() {
        command.initialize();
        command.execute();
    }
    
    @Test
    public void goStraightAheadTest() {
        command.addPoint(new FieldPose(new XYPair(0, 10), new ContiguousHeading(90)));
        
        command.initialize();
        command.execute();
        
        verifyTankDrive(1, 1);
    }
    
    @Test
    public void turnRightTest() {
        command.addPoint(new FieldPose(new XYPair(10, 10), new ContiguousHeading(0)));
        
        command.initialize();
        command.execute();
        
        verifyTankDrive(1, 0);
    }
    
    @Test
    public void changePoints() {
        command.addPoint(new FieldPose(new XYPair(0, 0.1), new ContiguousHeading(90)));
        command.addPoint(new FieldPose(new XYPair(-10, 0), new ContiguousHeading(180)));
        
        command.initialize();
        
        command.execute();
        verifyTankDrive(1, 1);
        
        command.execute();
        verifyTankDrive(0, 1);
    }
    
    protected void verifyTankDrive(double left, double right) {
        assertEquals(left, drive.leftTank.getMotorOutputPercent(), 0.001);
        assertEquals(right, drive.rightTank.getMotorOutputPercent(), 0.001);
    }
}
