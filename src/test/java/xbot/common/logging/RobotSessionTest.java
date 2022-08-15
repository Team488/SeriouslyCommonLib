package xbot.common.logging;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class RobotSessionTest extends BaseCommonLibTest {

    RobotSession rs;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        rs = getInjectorComponent().robotSession();
    }

    @Test
    public void simpleTest() {
        String id = rs.getSessionId();
        System.out.println("SessionId: " + id);
    }

    @Test
    public void testTypicalProgression() {
        String id1 = rs.getSessionId();
        rs.autoInit();
        String id2 = rs.getSessionId();

        assertFalse("New ID should have been generated when starting auto", id1.equals(id2));

        rs.teleopInit();
        String id3 = rs.getSessionId();

        assertTrue("Id should remain the same when entering teleop from auto", id2.equals(id3));

        rs.teleopInit();
        String id4 = rs.getSessionId();

        assertFalse("New ID should have been generated when starting teleop from teleop", id3.equals(id4));
    }

    @Test
    public void testEnteringTeleopFromNothing() {
        String id1 = rs.getSessionId();
        rs.teleopInit();
        String id2 = rs.getSessionId();
        rs.teleopInit();
        String id3 = rs.getSessionId();

        assertTrue("First teleop entry (from nothing) should keep the ID", id1.equals(id2));
        assertFalse("New ID should have been generated when entering telepo again", id2.equals(id3));
    }
}