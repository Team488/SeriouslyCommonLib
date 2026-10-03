package xbot.common.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.simulation.DriverStationSim;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;

import static org.junit.jupiter.api.Assertions.assertSame;

public class HumanVsMachineDeciderTest extends BaseCommonLibTest {

    HumanVsMachineDecider decider;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        decider = getInjectorComponent().humanVsMachineDeciderFactory().create("Test");
    }

    @Test
    public void testStandardPath() {
        assertSame(HumanVsMachineMode.Coast, decider.getRecommendedMode(0), "Start in coast while disabled");

        DriverStationSim.setEnabled(true);
        DriverStationSim.notifyNewData();

        assertSame(HumanVsMachineMode.InitializeMachineControl, decider.getRecommendedMode(0), "Start in initialize machine control");
        assertSame(HumanVsMachineMode.MachineControl, decider.getRecommendedMode(0), "Machine Control");

        assertSame(HumanVsMachineMode.HumanControl, decider.getRecommendedMode(1), "Human input brings us back out");
        timer.advanceTimeInSecondsBy(0.01);
        assertSame(HumanVsMachineMode.Coast, decider.getRecommendedMode(0.01), "Then we coast");
        timer.advanceTimeInSecondsBy(1);
        assertSame(HumanVsMachineMode.InitializeMachineControl, decider.getRecommendedMode(0), "Advance to initialize");
        assertSame(HumanVsMachineMode.MachineControl, decider.getRecommendedMode(0), "Machine Control");
    }
}
