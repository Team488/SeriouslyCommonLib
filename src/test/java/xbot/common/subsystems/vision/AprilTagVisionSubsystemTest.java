package xbot.common.subsystems.vision;

import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

public class AprilTagVisionSubsystemTest extends BaseCommonLibTest {
    @Test
    public void test() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var visionConsumer = (MockVisionConsumer) this.getInjectorComponent().getAprilTagVisionConsumer();
        assertEquals(0, subsystem.getCameraCount());
        subsystem.periodic();
        assertEquals(0, visionConsumer.posesReceived.size());
    }
}
