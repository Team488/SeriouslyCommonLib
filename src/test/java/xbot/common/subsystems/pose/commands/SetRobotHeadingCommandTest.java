package xbot.common.subsystems.pose.commands;

import org.junit.Before;
import org.junit.Test;

import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.BasePoseTest;

public class SetRobotHeadingCommandTest extends BasePoseTest {

    SetRobotHeadingCommand setHeading;
    
    @Before
    public void setup() {
        super.setup();
        setHeading = getInjectorComponent().setRobotHeadingCommand();
    }
    
    @Test
    public void testSetHeading() {
        mockTimer.setTimeInSeconds(mockTimer.getFPGATimestamp() + 1);
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
        
        setHeading.setHeadingToApply(74);
        setHeading.isFinished();
        setHeading.initialize();
        setHeading.execute();
  
        verifyRobotHeading(74);
    }
    
    @Test
    public void testSetHeadingLargeValues() {
        mockTimer.setTimeInSeconds(2);
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
        
        setHeading.setHeadingToApply(361);
        setHeading.isFinished();
        setHeading.initialize();
        setHeading.execute();
        
        verifyRobotHeading(1);
        
        setHeading.setHeadingToApply(-361);
        setHeading.isFinished();
        setHeading.initialize();
        setHeading.execute();
        
        verifyRobotHeading(-1);
    }
}
