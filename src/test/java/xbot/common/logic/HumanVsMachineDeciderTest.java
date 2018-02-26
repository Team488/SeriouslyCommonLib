package xbot.common.logic;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.injection.BaseWPITest;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;

public class HumanVsMachineDeciderTest extends BaseWPITest {

    HumanVsMachineDecider decider;
    
    @Override
    public void setUp() {
        super.setUp();
        decider = clf.createHumanVsMachineDecider("Test");
    }
    
    @Test
    public void testStandardPath() {
        assertTrue("Start in coast", decider.getRecommendedMode(0) == HumanVsMachineMode.Coast);
        timer.advanceTimeInSecondsBy(1);
        assertTrue("Advance to initialize", decider.getRecommendedMode(0) == HumanVsMachineMode.InitializeMachineControl);
        assertTrue("Machine Control", decider.getRecommendedMode(0) == HumanVsMachineMode.MachineControl);
        
        assertTrue("Human input brings us back out", decider.getRecommendedMode(1) == HumanVsMachineMode.HumanControl);
        timer.advanceTimeInSecondsBy(0.01);
        assertTrue("Then we coast", decider.getRecommendedMode(0.01) == HumanVsMachineMode.Coast);
        timer.advanceTimeInSecondsBy(1);
        assertTrue("Advance to initialize", decider.getRecommendedMode(0) == HumanVsMachineMode.InitializeMachineControl);
        assertTrue("Machine Control", decider.getRecommendedMode(0) == HumanVsMachineMode.MachineControl);
    }
}
