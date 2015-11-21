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
    
    @Test
    public void testExceptionThrownInTests() {
        SafeRobotAssertionManager assertMan = injector.getInstance(SafeRobotAssertionManager.class);
        
        try {
            assertMan.throwException(new RuntimeException("Something really bad happened (tests are free to die as necessary)"));
        }
        catch (Throwable e) {
            // We want it to throw -- this is good
            return;
        }
        
        // If it didn't throw, something went wrong!
        fail();
    }
}
