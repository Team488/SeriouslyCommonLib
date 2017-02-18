package xbot.common.subsystems.pose.commands;

import org.junit.Before;
import org.junit.Test;

import xbot.common.subsystems.pose.BasePoseTest;

public class ResetDistanceCommandTest extends BasePoseTest {

    ResetDistanceCommand reset;
    
    @Before
    public void setup() {
        super.setup();
        reset = injector.getInstance(ResetDistanceCommand.class);
    }
    
    @Test
    public void testPositionReset() {
        verifyRobotOrientedDistance(0);
        verifyAbsoluteDistance(0, 0);
        
        pose.setDistanceTraveled(100, 100);
        verifyRobotOrientedDistance(100);
        verifyAbsoluteDistance(0, 100);
        
        reset.isFinished();
        reset.initialize();
        reset.execute();
        
        verifyRobotOrientedDistance(0);
        verifyAbsoluteDistance(0, 0);
    }
    
    @Test
    public void testResetTwiceInSuccession() {
        verifyRobotOrientedDistance(0);
        verifyAbsoluteDistance(0, 0);
        
        pose.setDistanceTraveled(100, 100);
        verifyRobotOrientedDistance(100);
        verifyAbsoluteDistance(0, 100);
        
        reset.isFinished();
        reset.initialize();
        reset.execute();
        reset.isFinished();
        reset.initialize();
        reset.execute();
        
        verifyRobotOrientedDistance(0);
        verifyAbsoluteDistance(0, 0);
    }
    
    @Test
    public void testResetTwice() {
        verifyRobotOrientedDistance(0);
        verifyAbsoluteDistance(0, 0);
        
        pose.setDistanceTraveled(100, 100);
        verifyRobotOrientedDistance(100);
        verifyAbsoluteDistance(0, 100);
        
        reset.isFinished();
        reset.initialize();
        reset.execute();
        
        pose.setDistanceTraveled(200, 200);
        verifyRobotOrientedDistance(100);
        verifyAbsoluteDistance(0, 100);
        
        reset.isFinished();
        reset.initialize();
        reset.execute();
        
        verifyRobotOrientedDistance(0);
        verifyAbsoluteDistance(0, 0);
    }
    
}
