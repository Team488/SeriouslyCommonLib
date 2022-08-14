package xbot.common.simulation;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockSolenoid;

public class SimulatedSolenoidTest extends BaseSimulationTest {

    MockSolenoid mockSolenoid;
    final int channel = 1;

    @Override
    public void setUp() {
        super.setUp();

        mockSolenoid = (MockSolenoid)injectorComponent.solenoidFactory().create(channel);
    }

    @Test
    public void testOn() {
        mockSolenoid.set(true);
        JSONObject result = mockSolenoid.getSimulationData();

        assertEquals("Solenoid1", result.get("id")); 
        assertEquals("VIRTUAL_SOLENOID", result.get("mode")); 
        assertEquals("ON", result.get("val")); 
    }

    @Test
    public void testOff() {
        mockSolenoid.set(false);
        JSONObject result = mockSolenoid.getSimulationData();

        assertEquals("Solenoid1", result.get("id")); 
        assertEquals("VIRTUAL_SOLENOID", result.get("mode")); 
        assertEquals("OFF", result.get("val")); 
    }
}
