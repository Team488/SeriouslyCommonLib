package xbot.common.logging;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class SafeRobotAssertionTest extends BaseWPITest {
    @Test
    public void testNoExceptionOnRobot() {
        RobotAssertionManager assertMan = new SilentRobotAssertionManager();
        
        assertMan.throwException(new RuntimeException("Something really bad happened (...but robots never die)"));
    }
    
    @Test(expected=RuntimeException.class)
    public void testExceptionThrownInTests() {
        RobotAssertionManager assertMan = new LoudRobotAssertionManager();
        
        assertMan.throwException(new RuntimeException("Something really bad happened (tests are free to die as necessary)"));
    }
    
    @Test
    public void testAssertionContinuesOnRobot() {
        RobotAssertionManager assertMan = new SilentRobotAssertionManager();
        
        assertMan.assertTrue(true, "The world is ending");
        assertMan.assertTrue(false, "false != true");
    }
    
    @Test(expected=RobotAssertionException.class)
    public void testAssertionFailedInTests() {
        RobotAssertionManager assertMan = new LoudRobotAssertionManager();
        
        assertMan.assertTrue(false, "false != true");
    }
    
    @Test()
    public void testAssertionPassedInTests() {
        RobotAssertionManager assertMan = new LoudRobotAssertionManager();
        
        assertMan.assertTrue(true, "The world is ending");
    }
}
