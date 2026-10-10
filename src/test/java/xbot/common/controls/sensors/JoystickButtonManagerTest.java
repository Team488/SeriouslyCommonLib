package xbot.common.controls.sensors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.driverstation.POVDirection;
import org.wpilib.math.geometry.Translation2d;

import xbot.common.controls.sensors.mock_adapters.MockJoystick;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.RobotAssertionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class JoystickButtonManagerTest extends BaseCommonLibTest {
    
    XJoystick testJoystick;
    RobotAssertionManager assertion;
    
    @BeforeEach
    public void setup() {
        super.setUp();
        
        testJoystick = getInjectorComponent().joystickFactory().create(1, 12);
        assertion = getInjectorComponent().robotAssertionManager();
    }
    
    @Test
    public void testButtonBelowRange() {
        assertThrows(RuntimeException.class, () -> testJoystick.getifAvailable(13));
    }
    
    @Test
    public void testButtonZero() {
        assertThrows(RuntimeException.class, () -> testJoystick.getifAvailable(0));
    }
    
    @Test
    public void testButtonNegative() {
        assertThrows(RuntimeException.class, () -> testJoystick.getifAvailable(-1));
    }
    
    @Test
    public void testAllValidButtons() {
        for (int x = 1; x <= 12; x++) {
            assertTrue(null != testJoystick.getifAvailable(x), "Button " + x + " should not be null.");
        }
        for (int x = 1; x <= 12; x++) {
            assertButtonUnavailable(x);
        }
    }

    @Test
    public void testPovDirections() {
        MockJoystick mockJoystick = (MockJoystick)testJoystick;
        assertEquals(POVDirection.CENTER, mockJoystick.getPOV());

        mockJoystick.setPOV(POVDirection.RIGHT);
        assertEquals(POVDirection.RIGHT, mockJoystick.getPOV());
        assertTrue(testJoystick.getPovIfAvailable(POVDirection.RIGHT).getAsBoolean());
    }

    @Test
    public void testPovDirectionCannotBeAllocatedTwice() {
        testJoystick.getPovIfAvailable(POVDirection.UP);
        assertThrows(IllegalStateException.class, () -> testJoystick.getPovIfAvailable(POVDirection.UP));
    }

    @Test
    public void testVectorUsesBothAxes() {
        MockJoystick mockJoystick = (MockJoystick)testJoystick;
        mockJoystick.setRawAxis(0, 0.25);
        mockJoystick.setRawAxis(1, 0.75);

        Translation2d vector = mockJoystick.getVectorForAxisPair(0, 1);

        assertEquals(0.25, vector.getX(), 0.001);
        assertEquals(0.75, vector.getY(), 0.001);
    }
    
    private void assertButtonUnavailable(int i) {
        try {
            testJoystick.getifAvailable(i);
            fail();
        } 
        catch (Exception e) {
            // nice!
        }
    }

}
