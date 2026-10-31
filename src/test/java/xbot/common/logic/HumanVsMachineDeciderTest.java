package xbot.common.logic;

import org.junit.jupiter.api.AfterEach;
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

    /**
     * {@code DriverStationSim}'s enabled flag is native/global state that outlives any one test - without
     * resetting it, whichever test enables the robot leaks that into every test that happens to run after it
     * in the same JVM, regardless of test order.
     */
    @AfterEach
    public void resetDriverStation() {
        DriverStationSim.setEnabled(false);
        DriverStationSim.notifyNewData();
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

    /**
     * Human input arriving again partway through a coast period must interrupt it and go back to
     * {@code HumanControl} - not be ignored until the original coast finishes. This also pins down that the
     * coast timer actually restarts from the renewed input: the elapsed time checked afterward is measured
     * against the second burst of input, not the first.
     */
    @Test
    public void testRenewedHumanInputInterruptsCoastAndRestartsTimer() {
        assertSame(HumanVsMachineMode.Coast, decider.getRecommendedMode(0), "Start in coast while disabled");

        DriverStationSim.setEnabled(true);
        DriverStationSim.notifyNewData();
        reachMachineControl();

        assertSame(HumanVsMachineMode.HumanControl, decider.getRecommendedMode(1), "Human input brings us back out");

        timer.advanceTimeInSecondsBy(0.1);
        assertSame(HumanVsMachineMode.Coast, decider.getRecommendedMode(0), "Input stopped, start coasting");

        timer.advanceTimeInSecondsBy(0.1);
        assertSame(HumanVsMachineMode.HumanControl, decider.getRecommendedMode(1),
                "Fresh input mid-coast interrupts it and returns to human control");

        // Only 0.1s has elapsed since the renewed input above, well under the 0.3s default coast time. If the
        // coast timer hadn't actually restarted - e.g. still measuring from the very first burst of input,
        // 0.2s + 0.1s = 0.3s ago - this would incorrectly already be eligible to leave Coast.
        timer.advanceTimeInSecondsBy(0.1);
        assertSame(HumanVsMachineMode.Coast, decider.getRecommendedMode(0),
                "Still within the coast window measured from the renewed input");

        // Now push well past the coast window measured from the renewed input (0.1s + 0.3s = 0.4s total).
        timer.advanceTimeInSecondsBy(0.3);
        assertSame(HumanVsMachineMode.InitializeMachineControl, decider.getRecommendedMode(0),
                "Coast window (from the renewed input) has elapsed");
        assertSame(HumanVsMachineMode.MachineControl, decider.getRecommendedMode(0), "Back to machine control");
    }

    /**
     * If enough time has already passed by the time input is next polled - e.g. the caller isn't ticking every
     * loop, or just a long gap - {@code HumanControl} should hand straight to {@code InitializeMachineControl}
     * without ever reporting {@code Coast} for a tick it didn't actually get a chance to occupy.
     */
    @Test
    public void testHumanControlSkipsCoastWhenGracePeriodAlreadyElapsed() {
        assertSame(HumanVsMachineMode.Coast, decider.getRecommendedMode(0), "Start in coast while disabled");

        DriverStationSim.setEnabled(true);
        DriverStationSim.notifyNewData();
        reachMachineControl();

        assertSame(HumanVsMachineMode.HumanControl, decider.getRecommendedMode(1), "Human input brings us back out");

        // Advance well past the default 0.3s coast time before the next poll, rather than advancing by less
        // than it (as testStandardPath does) - simulating a gap longer than the coast window itself.
        timer.advanceTimeInSecondsBy(1);
        assertSame(HumanVsMachineMode.InitializeMachineControl, decider.getRecommendedMode(0),
                "Grace period already elapsed; skip straight past Coast");
        assertSame(HumanVsMachineMode.MachineControl, decider.getRecommendedMode(0), "Machine Control");
    }

    /**
     * Polls through {@code InitializeMachineControl} to {@code MachineControl}, the common starting point for
     * the scenarios above. The caller must already have enabled the robot (via {@code DriverStationSim}) before
     * calling this.
     */
    private void reachMachineControl() {
        assertSame(HumanVsMachineMode.InitializeMachineControl, decider.getRecommendedMode(0), "Start in initialize machine control");
        assertSame(HumanVsMachineMode.MachineControl, decider.getRecommendedMode(0), "Machine Control");
    }
}
