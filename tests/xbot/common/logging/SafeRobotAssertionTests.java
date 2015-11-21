package xbot.common.logging;

import static org.junit.Assert.*;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class SafeRobotAssertionTests extends BaseWPITest {
    @Test
    public void testNoExceptionOnRobot() {
        SafeRobotAssertionManager assertMan = injector.getInstance(SafeRobotAssertionManager.class);
        assertMan.setExceptionsEnabled(false);
        
        assertMan.throwException(new RuntimeException("Something really bad happened (...but robots never die)"));
    }
    
    @Test(expected=RuntimeException.class
            )
    public void testExceptionThrownInTests() {
        SafeRobotAssertionManager assertMan = injector.getInstance(SafeRobotAssertionManager.class);
        
        assertMan.throwException(new RuntimeException("Something really bad happened (tests are free to die as necessary)"));
    }
    
    @Test
    public void testAssertionContinuesOnRobot() {
        SafeRobotAssertionManager assertMan = injector.getInstance(SafeRobotAssertionManager.class);
        assertMan.setExceptionsEnabled(false);
        
        assertMan.assertTrue(true, "The world is ending");
        assertMan.assertTrue(false, "false != true");
    }
    
    @Test(expected=SafeRobotAssertionException.class)
    public void testAssertionFailedInTests() {
        SafeRobotAssertionManager assertMan = injector.getInstance(SafeRobotAssertionManager.class);
        
        assertMan.assertTrue(false, "false != true");
    }
    
    @Test()
    public void testAssertionPassedInTests() {
        SafeRobotAssertionManager assertMan = injector.getInstance(SafeRobotAssertionManager.class);
        
        assertMan.assertTrue(true, "The world is ending");
    }
}
