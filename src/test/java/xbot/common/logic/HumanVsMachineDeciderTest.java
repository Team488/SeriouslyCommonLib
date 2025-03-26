package xbot.common.logic;

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
        assertTrue("Start in coast while disabled", decider.getRecommendedMode(0) == HumanVsMachineMode.Coast);

        DriverStationSim.setEnabled(true);
        DriverStationSim.notifyNewData();

        assertTrue("Start in machine control", decider.getRecommendedMode(0) == HumanVsMachineMode.MachineControl);

        assertTrue("Human input brings us back out", decider.getRecommendedMode(1) == HumanVsMachineMode.HumanControl);
        timer.advanceTimeInSecondsBy(0.01);
        assertTrue("Then we coast", decider.getRecommendedMode(0.01) == HumanVsMachineMode.Coast);
        timer.advanceTimeInSecondsBy(1);
        assertTrue("Advance to initialize", decider.getRecommendedMode(0) == HumanVsMachineMode.InitializeMachineControl);
        assertTrue("Machine Control", decider.getRecommendedMode(0) == HumanVsMachineMode.MachineControl);
    }
}
