package xbot.common.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SafeRobotAssertionTest extends BaseCommonLibTest {

    private static Logger log = LogManager.getLogger(SafeRobotAssertionTest.class);

    @Test
    public void testNoExceptionOnRobot() {
        RobotAssertionManager assertMan = new SilentRobotAssertionManager();
        
        assertMan.throwException(new RuntimeException("Something really bad happened (...but robots never die)"));
    }
    
    @Test
    public void testExceptionThrownInTests() {
        RobotAssertionManager assertMan = new LoudRobotAssertionManager();

        assertThrows(RuntimeException.class, () ->
            assertMan.throwException(new RuntimeException("Something really bad happened (tests are free to die as necessary)")));
    }
    
    @Test
    public void testAssertionContinuesOnRobot() {
        RobotAssertionManager assertMan = new SilentRobotAssertionManager();
        
        assertMan.assertTrue(true, "The world is ending");
        assertMan.assertTrue(false, "false != true");
        log.info("Yet the world keeps turning");
    }
    
    @Test
    public void testAssertionFailedInTests() {
        RobotAssertionManager assertMan = new LoudRobotAssertionManager();

        assertThrows(RobotAssertionException.class, () -> assertMan.assertTrue(false, "false != true"));
    }
    
    @Test()
    public void testAssertionPassedInTests() {
        RobotAssertionManager assertMan = new LoudRobotAssertionManager();
        
        assertMan.assertTrue(true, "The world is ending");
    }
}
