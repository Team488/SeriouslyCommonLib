package xbot.common.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.assertion.LoudRobotAssertionHandler;
import xbot.common.logging.assertion.RobotAssertionException;
import xbot.common.logging.assertion.RobotAssertionHandler;
import xbot.common.logging.assertion.SilentRobotAssertionHandler;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SafeRobotAssertionTest extends BaseCommonLibTest {

    private static Logger log = LogManager.getLogger(SafeRobotAssertionTest.class);

    @Test
    public void testNoExceptionOnRobot() {
        RobotAssertionHandler assertionHandler = new SilentRobotAssertionHandler();

        assertionHandler.throwException(new RuntimeException("Something really bad happened (...but robots never die)"));
    }

    @Test
    public void testExceptionThrownInTests() {
        RobotAssertionHandler assertionHandler = new LoudRobotAssertionHandler();

        assertThrows(RuntimeException.class, () ->
            assertionHandler.throwException(new RuntimeException("Something really bad happened (tests are free to die as necessary)")));
    }

    @Test
    public void testAssertionContinuesOnRobot() {
        RobotAssertionHandler assertionHandler = new SilentRobotAssertionHandler();

        assertionHandler.assertTrue(true, "The world is ending");
        assertionHandler.assertTrue(false, "false != true");
        log.info("Yet the world keeps turning");
    }

    @Test
    public void testAssertionFailedInTests() {
        RobotAssertionHandler assertionHandler = new LoudRobotAssertionHandler();

        assertThrows(RobotAssertionException.class, () -> assertionHandler.assertTrue(false, "false != true"));
    }

    @Test()
    public void testAssertionPassedInTests() {
        RobotAssertionHandler assertionHandler = new LoudRobotAssertionHandler();

        assertionHandler.assertTrue(true, "The world is ending");
    }
}
