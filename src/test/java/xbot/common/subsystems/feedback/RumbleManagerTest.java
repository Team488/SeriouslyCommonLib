package xbot.common.subsystems.feedback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.sensors.mock_adapters.MockFTCGamepad;
import xbot.common.injection.BaseCommonLibTest;

/**
 * Unit tests for RumbleManager
 */
public class RumbleManagerTest extends BaseCommonLibTest {

    MockFTCGamepad gamepad;
    RumbleManager rumbleManager;
    MockTimer timer;
    
    @Override
    public void setUp() {
        super.setUp();
        
        this.gamepad = (MockFTCGamepad)getInjectorComponent().ftcGamepadFactory().create(0, 10);
        this.rumbleManager = new RumbleManager(this.gamepad);
        this.timer = (MockTimer)getInjectorComponent().timerImplementation();
    }
    
    @Test
    public void testGetIsRumbling() {
        assertFalse(rumbleManager.getIsRumbling());

        rumbleManager.rumbleGamepad(5, 10);
        assertTrue(rumbleManager.getIsRumbling());

        rumbleManager.stopGamepadRumble();
        assertFalse(rumbleManager.getIsRumbling());
    }
    
    @Test
    public void testRumbleGamepad() {
        double rumbleDuration = 10;
        rumbleManager.rumbleGamepad(5, rumbleDuration);
        assertTrue(rumbleManager.getIsRumbling());

        timer.advanceTimeInSecondsBy(rumbleDuration + 0.01);
        rumbleManager.periodic();
        assertFalse(rumbleManager.getIsRumbling());
    }
}
