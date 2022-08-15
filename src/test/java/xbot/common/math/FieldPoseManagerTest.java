package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class FieldPoseManagerTest extends BaseCommonLibTest {

    @Test
    public void testCreation() {
        FieldPosePropertyManager fppm = getInjectorComponent().fieldPosePropertyManagerFactory().create("Suffix", 1, 2, 3);
        fppm.getPose();
        assertEquals(1, fppm.getPose().getPoint().x, 0.001);
        assertEquals(2, fppm.getPose().getPoint().y, 0.001);
        assertEquals(3, fppm.getPose().getHeading().getDegrees(), 0.001);
    }
}