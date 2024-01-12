package xbot.common.subsystems.simple;

import edu.wpi.first.wpilibj2.command.Command;
import org.junit.Before;
import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.BaseWPITest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleMotorSubsystemTest extends BaseCommonLibTest {
    private MockSimpleMotorSubsystem subsystem;

    @Before
    public void setup() {
        this.subsystem = this.getInjectorComponent().mockSimpleMotorSubsystem();
    }

    @Test
    public void verifyDefaultStateStopped() {
        assertEquals(0, this.subsystem.currentPower, 0);
    }

    @Test
    public void verifySetForward() {
        assertEquals(0, this.subsystem.currentPower, 0);
        this.subsystem.setForward();
        assertEquals(1, this.subsystem.currentPower, 0.001);
    }

    @Test
    public void verifySetReverse() {
        assertEquals(0, this.subsystem.currentPower, 0);
        this.subsystem.setReverse();
        assertEquals(-1, this.subsystem.currentPower, 0.001);
    }

    @Test
    public void verifyStop() {
        assertEquals(0, this.subsystem.currentPower, 0);
        this.subsystem.setReverse();
        this.subsystem.stop();
        assertEquals(0, this.subsystem.currentPower, 0.001);
    }

    @Test
    public void verifyGetForwardCommand() {
        assertEquals(0, this.subsystem.currentPower, 0);
        Command command = this.subsystem.getForwardCommand();
        assertEquals("mock-Forward", command.getName());
        command.initialize();
        command.execute();
        assertEquals(1, this.subsystem.currentPower, 0.001);
        assertFalse(command.isFinished());
    }

    @Test
    public void verifyGetReverseCommand() {
        assertEquals(0, this.subsystem.currentPower, 0);
        Command command = this.subsystem.getReverseCommand();
        assertEquals("mock-Reverse", command.getName());
        command.initialize();
        command.execute();
        assertEquals(-1, this.subsystem.currentPower, 0.001);
        assertFalse(command.isFinished());
    }

    @Test
    public void verifyGetStopCommand() {
        assertEquals(0, this.subsystem.currentPower, 0);
        this.subsystem.setForward();
        Command command = this.subsystem.getStopCommand();
        assertEquals("mock-Stop", command.getName());
        command.initialize();
        command.execute();
        assertEquals(0, this.subsystem.currentPower, 0.001);
        assertFalse(command.isFinished());
    }
}
