package xbot.common.subsystems.vision;

import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

public class AprilTagVisionSubsystemTest extends BaseCommonLibTest {
    @Test
    public void testConstructor() {
        this.getInjectorComponent().getAprilTagVisionSubsystem();
    }
}
