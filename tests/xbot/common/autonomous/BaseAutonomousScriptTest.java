package xbot.common.autonomous;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.wpi.first.wpilibj.command.Scheduler;
import xbot.common.autonomous.TestScriptedCommandFactory.ExecutionCounterCommandProvider;
import xbot.common.command.XScheduler;
import xbot.common.injection.BaseWPITest;

public class BaseAutonomousScriptTest extends BaseWPITest {

    @Test
    public void testBasicCommandExecution() {
        TestScriptedCommandFactory scriptedCommandFactory = new TestScriptedCommandFactory();
        AutonomousScriptedCommand scriptedCommand = new AutonomousScriptedCommand(scriptedCommandFactory);
        scriptedCommand.executeScriptFromString(
                "robot.requireCommands('CounterCommand');"
                + " robot.invokeCounterCommand();",
                "TestScript");
        
        XScheduler scheduler = injector.getInstance(XScheduler.class);
        
        scheduler.add(scriptedCommand);
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommandProvider lastCommandProvider = scriptedCommandFactory.getLastExecutionCounterCommandProvider();
        assertNotNull(lastCommandProvider);
        ExecutionCounterCommand lastCommand = lastCommandProvider.getLastCommand();
        assertNotNull(lastCommand);

        assertEquals(1, lastCommand.getInitCount());
        assertEquals(1, lastCommand.getExecCount());
    }

}
