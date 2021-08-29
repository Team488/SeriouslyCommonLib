package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.math.Circle;
import xbot.common.subsystems.drive.control_logic.CircleFollowingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

public class CircleFollowingModuleTest extends BaseWPITest {

    MockBasePoseSubsystem pose;
    CircleFollowingModule cfm;

    @Override
    public void setUp() {
        super.setUp();
        pose = (MockBasePoseSubsystem)injector.getInstance(BasePoseSubsystem.class);
        cfm = clf.createCircleFollowingModule("Test");
    }

    @Test
    public void simpleTest() {
        pose.setCurrentPosition(100, 0);
        cfm.setSpiralFactors(0, 0);
        
        //Counter clockwise
        double alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 10) , false);
        assertEquals(90, alignToCircleHeading, 0.001);

        // Clockwise
        alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 10) , true);
        assertEquals(-90, alignToCircleHeading, 0.001);
    }

    @Test
    public void spiralIn() {
        pose.setCurrentPosition(100, 0);
        cfm.setSpiralFactors(10000, 10);
        
        //Counter clockwise
        double alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 10) , false);
        assertEquals(100, alignToCircleHeading, 0.001);

        //clockwise
        alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 10) , true);
        assertEquals(-100, alignToCircleHeading, 0.001);
    }

    @Test
    public void spiralOut() {
        pose.setCurrentPosition(100, 0);
        cfm.setSpiralFactors(10000, 10);
        
        //Counter clockwise
        double alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 200) , false);
        assertEquals(80, alignToCircleHeading, 0.001);

        //clockwise
        alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 200) , true);
        assertEquals(-80, alignToCircleHeading, 0.001);
    }

    @Test
    public void proportionateSpiralOut() {
        pose.setCurrentPosition(100, 0);
        cfm.setSpiralFactors(1, 10000);
        
        //Counter clockwise
        double alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 115) , false);
        assertEquals(75, alignToCircleHeading, 0.001);

        //clockwise
        alignToCircleHeading = cfm.alignToCircle(new Circle(0, 0, 115) , true);
        assertEquals(-75, alignToCircleHeading, 0.001);
    }
}