package xbot.common.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.simulation.SimHooks;

import xbot.common.injection.BaseCommonLibTest;

public class TimeLoggerTest extends BaseCommonLibTest {

    TimeLogger tl;

    @BeforeEach

    @Override
    public void setUp() {
        super.setUp();
        tl = new TimeLogger("Examplo", 1);
    }

    @Test
    public void simpleTest() {
        tl.start();

        SimHooks.stepTiming(2);
        tl.stop();
    }

    @Test
    public void testMultipleMeasurements() {
        tl.start();
        SimHooks.stepTiming(0.25);
        tl.stop();

        SimHooks.stepTiming(1);

        tl.start();
        SimHooks.stepTiming(0.75);
        tl.stop();
    }

    @Test
    public void testOutOfOrder() {
        tl.stop();
        tl.stop();
        tl.stop();

        tl.start();
        tl.start();
        tl.start();

        tl.stop();
        tl.stop();
        tl.stop();
    }
}
