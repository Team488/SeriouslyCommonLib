package xbot.common.math;

import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldPoseTunableManagerTest extends BaseCommonLibTest {

    @Test
    public void testCreation() {
        FieldPoseTunableManager fieldPoseTunables =
                getInjectorComponent().fieldPoseTunableManagerFactory().create("Suffix", 1, 2, 3);
        assertEquals(1, fieldPoseTunables.getPose().getPoint().x, 0.001);
        assertEquals(2, fieldPoseTunables.getPose().getPoint().y, 0.001);
        assertEquals(3, fieldPoseTunables.getPose().getHeading().getDegrees(), 0.001);
    }
}
