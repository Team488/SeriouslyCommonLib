package xbot.common.controls.actuators;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockSolenoid;
import xbot.common.controls.actuators.XDoubleSolenoid.DoubleSolenoidMode;
import xbot.common.injection.BaseWPITest;

public class XDoubleSolenoidTest extends BaseWPITest {

    MockSolenoid forwardSolenoid;   
    MockSolenoid reverseSolenoid;
    XDoubleSolenoid xDoubleSol;
    
    @Before
    public void setup()
    {
        super.setUp();

        forwardSolenoid = (MockSolenoid)clf.createSolenoid(1);
        reverseSolenoid = (MockSolenoid)clf.createSolenoid(2);
        xDoubleSol = clf.createDoubleSolenoid(forwardSolenoid, reverseSolenoid);
    }

    @Test
    public void testXDoubleSolenoidIfInverted() {
        xDoubleSol.setInverted(true);

        xDoubleSol.setDoubleSolenoid(DoubleSolenoidMode.FORWARD);
        assertFalse(forwardSolenoid.get());
        assertTrue(reverseSolenoid.get());

        xDoubleSol.setDoubleSolenoid(DoubleSolenoidMode.REVERSE);
        assertTrue(forwardSolenoid.get());
        assertFalse(reverseSolenoid.get());
    }

    @Test
    public void testXDoubleSolenoidNotInverted() {
        xDoubleSol.setInverted(false);

        xDoubleSol.setDoubleSolenoid(DoubleSolenoidMode.FORWARD);
        assertTrue(forwardSolenoid.get());
        assertFalse(reverseSolenoid.get());

        xDoubleSol.setDoubleSolenoid(DoubleSolenoidMode.REVERSE);
        assertFalse(forwardSolenoid.get());
        assertTrue(reverseSolenoid.get());

    }
}