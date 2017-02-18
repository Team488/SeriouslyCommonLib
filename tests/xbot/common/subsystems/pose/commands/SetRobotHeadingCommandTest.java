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
        setHeading = injector.getInstance(SetRobotHeadingCommand.class);
    }
    
    @Test
    public void testSetHeading() {
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
        
        setHeading.setHeadingToApply(74);
        setHeading.isFinished();
        setHeading.initialize();
        setHeading.execute();
        
        verifyRobotHeading(74);
    }
    
    @Test
    public void testSetHeadingLargeValues() {
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
