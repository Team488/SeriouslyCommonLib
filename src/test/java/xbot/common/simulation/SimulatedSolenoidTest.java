package xbot.common.simulation;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import xbot.common.controls.actuators.mock_adapters.MockSolenoid;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class SimulatedSolenoidTest extends BaseSimulationTest {

    MockSolenoid mockSolenoid;
    final int channel = 1;

    @BeforeEach
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
