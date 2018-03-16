package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.subsystems.drive.PurePursuitCommand.PursuitMode;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

public class PurePursuitCommandTest extends BaseWPITest {
    
    ConfigurablePurePursuitCommand command;
    MockDriveSubsystem drive;
    MockBasePoseSubsystem pose;
    
    @Override
    public void setUp() {
        super.setUp();
        command = injector.getInstance(ConfigurablePurePursuitCommand.class);
        this.drive = (MockDriveSubsystem)injector.getInstance(BaseDriveSubsystem.class);
        this.pose = (MockBasePoseSubsystem)injector.getInstance(BasePoseSubsystem.class);
        
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
        
        verifyTankDrive(1, -1);
    }
    
    @Test
    public void changePoints() {
        command.addPoint(new FieldPose(new XYPair(0, 0.1), new ContiguousHeading(90)));
        command.addPoint(new FieldPose(new XYPair(-10, 0), new ContiguousHeading(180)));
        
        command.initialize();
        
        command.execute();
        verifyTankDrive(1, 1);
        
        command.execute();
        verifyTankDrive(-1, 1);
    }
    
    @Test
    public void testSimpleRelative() {
        command.addPoint(new FieldPose(new XYPair(0, 10), new ContiguousHeading(90)));
        command.addPoint(new FieldPose(new XYPair(10, 10), new ContiguousHeading(90)));
        
        command.setMode(PursuitMode.Relative);
        command.initialize();
        
        verifyPose(command.getPlannedPointsToVisit().get(0), 0, 10, 90);
        verifyPose(command.getPlannedPointsToVisit().get(1), 10, 10, 90); 
    }
    
    @Test
    public void testComplexRelative() {
        command.addPoint(new FieldPose(new XYPair(0, 10), new ContiguousHeading(90)));
        command.addPoint(new FieldPose(new XYPair(10, 10), new ContiguousHeading(90)));
        
        // rotate the robot
        pose.setCurrentHeading(180);
        pose.setDriveEncoderDistances(10, 10);
        
        command.setMode(PursuitMode.Relative);
        command.initialize();
        
        verifyPose(command.getPlannedPointsToVisit().get(0), -20, 0, 180);
        verifyPose(command.getPlannedPointsToVisit().get(1), -20, 10, 180); 
    }
    
    @SuppressWarnings("serial")
    public void testSupplier() {
        command.addPoint(new FieldPose(new XYPair(0, 10), new ContiguousHeading(90)));
        command.addPoint(new FieldPose(new XYPair(10, 10), new ContiguousHeading(90)));
        
        command.setPointSupplier(() -> 
        new ArrayList<FieldPose>() {{
            add(new FieldPose(new XYPair(1, 2), new ContiguousHeading(3)));
            }}
        );
        
        command.initialize();
        
        verifyPose(command.getPlannedPointsToVisit().get(0), 1, 2, 3);
    }
    
    @Test
    public void testStickyMode() {
        command.addPoint(new FieldPose(new XYPair(0, 10), new ContiguousHeading(90)));
        command.initialize();
        command.execute();
        verifyTankDrive(1, 1);
        
        pose.forceTotalXandY(1000000, 30);
        command.execute();
        command.execute();
        
        // too far forward, so -1, -1
        // Need to turn hard to the left, so -1, 1
        // should balance to -1, 0
        verifyTankDrive(-1, 1);
    }
    
    protected void verifyPose(FieldPose poseToTest, double x, double y, double heading) {
        assertEquals("Looking at X", x, poseToTest.getPoint().x, 0.001);
        assertEquals("Looking at Y", y, poseToTest.getPoint().y, 0.001);
        assertEquals("Looking at Heading", heading, poseToTest.getHeading().getValue(), 0.001);
    }
    
    protected void verifyTankDrive(double left, double right) {
        assertEquals("Checking Left Drive", left, drive.leftTank.getMotorOutputPercent(), 0.001);
        assertEquals("Checking Right Drive", right, drive.rightTank.getMotorOutputPercent(), 0.001);
    }
}
