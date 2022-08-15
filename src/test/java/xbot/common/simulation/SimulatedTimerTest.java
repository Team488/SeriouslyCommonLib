package xbot.common.simulation;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockTimer;

public class SimulatedTimerTest extends BaseSimulationTest {

    MockTimer simulatedTimer;

    @Override
    public void setUp() {
        super.setUp();

        simulatedTimer = (MockTimer)injectorComponent.timerImplementation();
    }

    @Test
    public void basicTest() {
        final double[] times = new double[] {
            0,
            1.23,
            4.56,
            1.23,
            0
        };

        // Check that we can set any positive time value
        for (double time : times) {
            JSONObject worldPosePayload = new JSONObject();
            worldPosePayload.put("Time", new BigDecimal(time));
            JSONObject fullSensorPayload = createSimpleWorldPosePayload(worldPosePayload);
            
            this.distributor.distributeSimulationPayload(fullSensorPayload);

            assertEquals(time, simulatedTimer.getFPGATimestamp(), 0.001);
            assertEquals(time, simulatedTimer.getMatchTime(), 0.001);
        }

        // Check that negative or missing time values are ignored
        JSONObject worldPosePayload = new JSONObject();
        worldPosePayload.put("Time", new BigDecimal(1.23));
        JSONObject fullSensorPayload = createSimpleWorldPosePayload(worldPosePayload);
        
        this.distributor.distributeSimulationPayload(fullSensorPayload);
        assertEquals(1.23, simulatedTimer.getFPGATimestamp(), 0.001);

        worldPosePayload = new JSONObject();
        worldPosePayload.put("Time", new BigDecimal(-1));
        fullSensorPayload = createSimpleWorldPosePayload(worldPosePayload);
        this.distributor.distributeSimulationPayload(fullSensorPayload);
        assertEquals(1.23, simulatedTimer.getFPGATimestamp(), 0.001);

        fullSensorPayload = createSimpleWorldPosePayload(new JSONObject());
        this.distributor.distributeSimulationPayload(fullSensorPayload);
        assertEquals(1.23, simulatedTimer.getFPGATimestamp(), 0.001);
    }
}