package xbot.common.logic;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj.simulation.SimHooks;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;

public class HumanVsMachineDeciderTest extends BaseCommonLibTest {

    HumanVsMachineDecider decider;

    @Override
    public void setUp() {
        super.setUp();
        decider = getInjectorComponent().humanVsMachineDeciderFactory().create("Test");
    }

    @Test
    public void testStandardPath() {
        assertSame("Start in coast while disabled", HumanVsMachineMode.Coast, decider.getRecommendedMode(0));

        DriverStationSim.setEnabled(true);
        DriverStationSim.notifyNewData();

        assertSame("Start in initialize machine control", HumanVsMachineMode.InitializeMachineControl, decider.getRecommendedMode(0));
        assertSame("Machine Control", HumanVsMachineMode.MachineControl, decider.getRecommendedMode(0));

        assertSame("Human input brings us back out", HumanVsMachineMode.HumanControl, decider.getRecommendedMode(1));
        timer.advanceTimeInSecondsBy(0.01);
        assertSame("Then we coast", HumanVsMachineMode.Coast, decider.getRecommendedMode(0.01));
        timer.advanceTimeInSecondsBy(1);
        assertSame("Advance to initialize", HumanVsMachineMode.InitializeMachineControl, decider.getRecommendedMode(0));
        assertSame("Machine Control", HumanVsMachineMode.MachineControl, decider.getRecommendedMode(0));
    }
}
